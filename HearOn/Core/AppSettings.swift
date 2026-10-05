import Foundation
import Observation

enum ThemeMode: String, CaseIterable { case system, light, dark }
enum AudioQuality: String, CaseIterable { case high, normal, low }

@Observable
final class AppSettings {
    static let shared = AppSettings()
    private let d = UserDefaults.standard

    var lang: String { didSet { d.set(lang, forKey: "app_lang") } }
    var theme: ThemeMode { didSet { d.set(theme.rawValue, forKey: "theme") } }
    var dynamicColors: Bool { didSet { d.set(dynamicColors, forKey: "dynamic_colors") } }
    var audioQuality: AudioQuality { didSet { d.set(audioQuality.rawValue, forKey: "audio_quality") } }
    var crossfadeEnabled: Bool { didSet { d.set(crossfadeEnabled, forKey: "crossfade_enabled") } }
    var crossfadeDuration: Double { didSet { d.set(crossfadeDuration, forKey: "crossfade_dur") } }
    var normalizeAudio: Bool { didSet { d.set(normalizeAudio, forKey: "norm_audio") } }
    var dataSaver: Bool { didSet { d.set(dataSaver, forKey: "data_saver") } }
    var maxImageCacheMB: Double {
        didSet {
            d.set(maxImageCacheMB, forKey: "max_img_cache")
            ImageCache.configure(maxMB: maxImageCacheMB)
        }
    }
    var notifyUpdate: Bool { didSet { d.set(notifyUpdate, forKey: "notify_update") } }

    private init() {
        lang = d.string(forKey: "app_lang") ?? "system"
        theme = ThemeMode(rawValue: d.string(forKey: "theme") ?? "") ?? .system
        dynamicColors = d.object(forKey: "dynamic_colors") as? Bool ?? true
        audioQuality = AudioQuality(rawValue: d.string(forKey: "audio_quality") ?? "") ?? .high
        crossfadeEnabled = d.bool(forKey: "crossfade_enabled")
        crossfadeDuration = max(1, d.object(forKey: "crossfade_dur") as? Double ?? 1)
        normalizeAudio = d.object(forKey: "norm_audio") as? Bool ?? true
        dataSaver = d.bool(forKey: "data_saver")
        maxImageCacheMB = d.object(forKey: "max_img_cache") as? Double ?? 512
        notifyUpdate = d.bool(forKey: "notify_update")
    }
}

enum ImageCache {
    static func configure(maxMB: Double) {
        URLCache.shared = URLCache(
            memoryCapacity: 50 * 1024 * 1024,
            diskCapacity: Int(maxMB) * 1024 * 1024,
            directory: FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0].appending(path: "covers")
        )
    }

    static func clear() { URLCache.shared.removeAllCachedResponses() }
}

/// Builds the cover URL for a given display size. YouTube Music covers
/// (lh3.googleusercontent.com) accept a `=wN-hN` suffix; video thumbnails
/// (i.ytimg.com) do not, so those are returned unchanged.
enum Covers {
    static func url(_ track: Track, size: Int) -> URL? {
        if let custom = LibraryStore.shared.customCoverURL(for: track.id) { return custom }
        let px = AppSettings.shared.dataSaver ? min(size, 226) : size
        var s = track.coverUrl
        if s.contains("googleusercontent.com") {
            s = s.replacing(/=w\d+-h\d+.*$/, with: "=w\(px)-h\(px)-l90-rj")
        }
        return URL(string: s)
    }
}
