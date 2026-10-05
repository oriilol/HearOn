import SwiftUI

@main
struct HearOnApp: App {
    init() {
        ImageCache.configure(maxMB: AppSettings.shared.maxImageCacheMB)
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}

/// Translates `key` into the language chosen in settings.
func t(_ key: String) -> String { L.get(key, AppSettings.shared.lang) }

/// UI state shared by screens that open the same sheets/alerts.
@Observable
final class UIState {
    static let shared = UIState()
    var coverPickerFor: Track?
    var infoFor: Track?
    var addToPlaylist: Track?
    var showPlayer = false
}
