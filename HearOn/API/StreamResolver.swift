import Foundation
import YouTubeKit

/// Replaces NewPipeExtractor (Java, Android only). YouTubeKit resolves
/// YouTube stream URLs natively in Swift, including signature deciphering.
enum StreamResolver {
    struct Resolved {
        let url: URL
        let bitrate: Int?
    }

    private static var cache: [String: (Resolved, Date)] = [:]
    private static let lock = NSLock()

    static func audioURL(for videoId: String) async -> Resolved? {
        // Stream URLs expire after ~6h; reuse them for 1h.
        let cached = lock.withLock { cache[videoId] }
        if let (r, at) = cached, Date.now.timeIntervalSince(at) < 3600 { return r }

        do {
            let streams = try await YouTube(videoID: videoId, methods: [.local, .remote]).streams
            // AVPlayer cannot play WebM/Opus, so only m4a (AAC) audio streams are usable.
            let audio = streams.filter { $0.includesAudioTrack && !$0.includesVideoTrack && $0.fileExtension == .m4a }
            let sorted = audio.sorted { ($0.bitrate ?? 0) > ($1.bitrate ?? 0) }
            let pick: YouTubeKit.Stream? = switch AppSettings.shared.audioQuality {
            case .high: sorted.first
            case .normal: sorted.count > 1 ? sorted[sorted.count / 2] : sorted.first
            case .low: sorted.last
            }
            guard let pick else { return nil }
            let r = Resolved(url: pick.url, bitrate: pick.bitrate)
            lock.withLock { cache[videoId] = (r, .now) }
            return r
        } catch {
            print("StreamResolver error for \(videoId): \(error)")
            return nil
        }
    }
}
