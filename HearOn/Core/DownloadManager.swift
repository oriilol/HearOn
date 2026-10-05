import Foundation
import Observation

/// Saves tracks for offline playback. Every played track is cached
/// automatically (the "invisible cache" from Android) and tracks can also be
/// downloaded explicitly.
///
/// YouTube throttles or cuts long un-ranged requests to its stream URLs, which
/// is why the Android cache ended with empty/partial files. Here files are
/// fetched in ranged chunks, the way NewPipe/yt-dlp do it.
@Observable
final class DownloadManager {
    static let shared = DownloadManager()

    private(set) var tracks: [Track] = []
    private(set) var inProgress: Set<String> = []
    private(set) var totalBytes: Int64 = 0

    private let chunkSize: Int64 = 1024 * 1024 * 9
    private let minValidSize: Int64 = 50_000

    private let dir: URL = {
        let d = LibraryStore.supportDir.appending(path: "downloads")
        try? FileManager.default.createDirectory(at: d, withIntermediateDirectories: true)
        var values = URLResourceValues()
        values.isExcludedFromBackup = true
        var url = d
        try? url.setResourceValues(values)
        return d
    }()
    private var manifestURL: URL { dir.appending(path: "manifest.json") }

    private init() {
        if let data = try? Data(contentsOf: manifestURL),
           let list = try? JSONDecoder().decode([Track].self, from: data) {
            tracks = list
        }
        verify()
    }

    func fileURL(_ id: String) -> URL { dir.appending(path: "\(id).m4a") }

    func isDownloaded(_ id: String) -> Bool {
        let size = (try? FileManager.default.attributesOfItem(atPath: fileURL(id).path)[.size] as? Int64) ?? 0
        return size >= minValidSize
    }

    /// Drops manifest entries whose file is missing or truncated.
    func verify() {
        tracks = tracks.filter { isDownloaded($0.id) }
        persist()
    }

    func download(_ track: Track) {
        guard !isDownloaded(track.id), !inProgress.contains(track.id) else { return }
        inProgress.insert(track.id)
        Task.detached(priority: .utility) {
            let ok = await self.fetch(track)
            await MainActor.run {
                self.inProgress.remove(track.id)
                if ok, !self.tracks.contains(where: { $0.id == track.id }) {
                    self.tracks.insert(track, at: 0)
                }
                self.persist()
            }
        }
    }

    func remove(_ id: String) {
        try? FileManager.default.removeItem(at: fileURL(id))
        tracks.removeAll { $0.id == id }
        persist()
    }

    func clearAll() {
        for t in tracks { try? FileManager.default.removeItem(at: fileURL(t.id)) }
        tracks = []
        persist()
    }

    private func persist() {
        if let data = try? JSONEncoder().encode(tracks) { try? data.write(to: manifestURL, options: .atomic) }
        totalBytes = tracks.reduce(0) { sum, t in
            sum + ((try? FileManager.default.attributesOfItem(atPath: fileURL(t.id).path)[.size] as? Int64) ?? 0)
        }
    }

    private func fetch(_ track: Track) async -> Bool {
        guard let stream = await StreamResolver.audioURL(for: track.id) else { return false }
        let tmp = dir.appending(path: "\(track.id).part")
        FileManager.default.createFile(atPath: tmp.path, contents: nil)
        guard let handle = try? FileHandle(forWritingTo: tmp) else { return false }
        defer { try? handle.close() }

        var start: Int64 = 0
        var total: Int64? = nil
        do {
            while total == nil || start < total! {
                var req = URLRequest(url: stream.url)
                req.setValue("bytes=\(start)-\(start + chunkSize - 1)", forHTTPHeaderField: "Range")
                req.setValue("Mozilla/5.0", forHTTPHeaderField: "User-Agent")
                let (data, resp) = try await URLSession.shared.data(for: req)
                guard let http = resp as? HTTPURLResponse, (200...299).contains(http.statusCode), !data.isEmpty else { break }
                try handle.write(contentsOf: data)
                start += Int64(data.count)
                if let range = http.value(forHTTPHeaderField: "Content-Range"),
                   let t = range.split(separator: "/").last.flatMap({ Int64($0) }) {
                    total = t
                } else {
                    break // server ignored Range and sent the whole file
                }
            }
        } catch {
            try? FileManager.default.removeItem(at: tmp)
            return false
        }

        let size = (try? FileManager.default.attributesOfItem(atPath: tmp.path)[.size] as? Int64) ?? 0
        guard size >= minValidSize, total == nil || size >= total! else {
            try? FileManager.default.removeItem(at: tmp)
            return false
        }
        try? FileManager.default.removeItem(at: fileURL(track.id))
        do { try FileManager.default.moveItem(at: tmp, to: fileURL(track.id)) } catch { return false }
        return true
    }
}
