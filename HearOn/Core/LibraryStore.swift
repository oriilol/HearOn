import Foundation
import Observation

/// Favorites, history, playlists, custom covers and manual lyrics.
/// Stored as JSON (keeps order and is safe with any characters in names,
/// unlike the `|||`-joined string sets used on Android).
@Observable
final class LibraryStore {
    static let shared = LibraryStore()

    private(set) var liked: [Track] = [] { didSet { save() } }
    private(set) var recent: [Track] = [] { didSet { save() } }
    var playlists: [Playlist] = [] { didSet { save() } }
    private(set) var customCovers: [String: String] = [:] { didSet { save() } }
    private(set) var manualLyrics: [String: String] = [:] { didSet { save() } }
    /// Bumped when a custom cover changes so image views reload.
    private(set) var coverVersion = 0

    private struct Snapshot: Codable {
        var liked: [Track]
        var recent: [Track]
        var playlists: [Playlist]
        var customCovers: [String: String]
        var manualLyrics: [String: String]
    }

    static let supportDir: URL = {
        let dir = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)[0]
        try? FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
        return dir
    }()

    private var fileURL: URL { Self.supportDir.appending(path: "library.json") }
    private var loading = true

    private init() {
        if let data = try? Data(contentsOf: fileURL),
           let s = try? JSONDecoder().decode(Snapshot.self, from: data) {
            liked = s.liked
            recent = s.recent
            playlists = s.playlists
            customCovers = s.customCovers
            manualLyrics = s.manualLyrics
        }
        loading = false
    }

    private func save() {
        guard !loading else { return }
        let s = Snapshot(liked: liked, recent: recent, playlists: playlists, customCovers: customCovers, manualLyrics: manualLyrics)
        if let data = try? JSONEncoder().encode(s) {
            try? data.write(to: fileURL, options: .atomic)
        }
    }

    // MARK: Favorites

    func isLiked(_ t: Track) -> Bool { liked.contains { $0.id == t.id } }

    func toggleLike(_ t: Track) {
        if isLiked(t) { liked.removeAll { $0.id == t.id } } else { liked.append(t) }
    }

    // MARK: History

    func addRecent(_ t: Track) {
        var r = recent.filter { $0.id != t.id }
        r.insert(t, at: 0)
        recent = Array(r.prefix(50))
    }

    func clearHistory() { recent = [] }

    // MARK: Playlists

    func createPlaylist(_ name: String) { playlists.append(Playlist(name: name, tracks: [])) }
    func deletePlaylist(_ id: UUID) { playlists.removeAll { $0.id == id } }

    func rename(_ id: UUID, to name: String) {
        guard let i = playlists.firstIndex(where: { $0.id == id }) else { return }
        playlists[i].name = name
    }

    func add(_ t: Track, to id: UUID) {
        guard let i = playlists.firstIndex(where: { $0.id == id }) else { return }
        playlists[i].tracks.append(t)
    }

    func remove(at index: Int, from id: UUID) {
        guard let i = playlists.firstIndex(where: { $0.id == id }), playlists[i].tracks.indices.contains(index) else { return }
        playlists[i].tracks.remove(at: index)
    }

    func move(in id: UUID, from: IndexSet, to: Int) {
        guard let i = playlists.firstIndex(where: { $0.id == id }) else { return }
        playlists[i].tracks.move(fromOffsets: from, toOffset: to)
    }

    // MARK: Custom covers

    func customCoverURL(for id: String) -> URL? {
        guard let name = customCovers[id] else { return nil }
        return Self.supportDir.appending(path: name)
    }

    func setCustomCover(_ data: Data, for id: String) {
        let name = "cover_\(id).jpg"
        try? data.write(to: Self.supportDir.appending(path: name), options: .atomic)
        customCovers[id] = name
        coverVersion += 1
    }

    // MARK: Lyrics

    func setManualLyrics(_ text: String, for id: String) { manualLyrics[id] = text }
}
