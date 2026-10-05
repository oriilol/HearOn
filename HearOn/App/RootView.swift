import PhotosUI
import SwiftUI

enum AppTab: Hashable { case home, library, settings, search }

struct RootView: View {
    @State private var tab: AppTab = .home
    @State private var ui = UIState.shared
    @State private var photoItem: PhotosPickerItem?
    @Namespace private var playerNS
    private let player = PlayerController.shared
    private let settings = AppSettings.shared

    var body: some View {
        tabs
            .modifier(MiniPlayerAccessory(visible: player.current != nil, namespace: playerNS))
            .tabBarMinimizeBehavior(.onScrollDown)
            .tint(settings.dynamicColors ? (player.accentColor ?? .accentColor) : .accentColor)
            .fullScreenCover(isPresented: $ui.showPlayer) {
                PlayerScreen()
                    .tint(settings.dynamicColors ? (player.accentColor ?? .accentColor) : .accentColor)
                    .navigationTransition(.zoom(sourceID: "miniplayer", in: playerNS))
            }
            .sheet(item: $ui.addToPlaylist) { track in
                AddToPlaylistSheet(track: track)
            }
            .alert(t("track_info_title"), isPresented: Binding(get: { ui.infoFor != nil }, set: { if !$0 { ui.infoFor = nil } }), presenting: ui.infoFor) { _ in
                Button(t("close")) {}
            } message: { track in
                Text(trackInfo(track))
            }
            .photosPicker(isPresented: Binding(get: { ui.coverPickerFor != nil }, set: { if !$0 && photoItem == nil { ui.coverPickerFor = nil } }), selection: $photoItem, matching: .images)
            .onChange(of: photoItem) { _, item in
                guard let item, let track = ui.coverPickerFor else { return }
                Task {
                    if let data = try? await item.loadTransferable(type: Data.self) {
                        LibraryStore.shared.setCustomCover(data, for: track.id)
                        player.toast = t("cover_saved")
                    }
                    photoItem = nil
                    ui.coverPickerFor = nil
                }
            }
            .overlay(alignment: .top) { ToastView() }
            .environment(\.locale, Locale(identifier: L.resolve(settings.lang)))
            .environment(\.layoutDirection, L.isRTL(settings.lang) ? .rightToLeft : .leftToRight)
            .preferredColorScheme(settings.theme.colorScheme)
            .task { await checkForUpdate() }
    }

    private var tabs: some View {
        TabView(selection: $tab) {
            Tab(t("home"), systemImage: "house.fill", value: .home) { HomeScreen() }
            Tab(t("your_library"), systemImage: "music.note.square.stack.fill", value: .library) { LibraryScreen() }
            Tab(t("settings"), systemImage: "gearshape.fill", value: .settings) { SettingsScreen() }
            Tab(value: .search, role: .search) { SearchScreen() }
        }
    }

    private func trackInfo(_ track: Track) -> String {
        let local = DownloadManager.shared.isDownloaded(track.id)
        var lines = [
            t("title_label") + track.title,
            t("artist_label") + track.artist,
            t("yt_id_label") + track.id,
            "",
            t("origin_label") + (local ? t("local_cache") : t("streaming")),
        ]
        if !local { lines.append(t("network_quality") + t("quality_\(settings.audioQuality.rawValue)")) }
        return lines.joined(separator: "\n")
    }

    private func checkForUpdate() async {
        guard settings.notifyUpdate, let latest = await YTMusicClient.latestVersion() else { return }
        let current = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0"
        if latest.compare(current, options: .numeric) == .orderedDescending {
            player.toast = t("update_available") + latest
        }
    }
}

/// Shows the mini player above the tab bar (Liquid Glass accessory, like Apple Music).
private struct MiniPlayerAccessory: ViewModifier {
    let visible: Bool
    let namespace: Namespace.ID

    func body(content: Content) -> some View {
        if #available(iOS 26.1, *) {
            // Keeps the TabView identity stable, so screens don't reset when playback starts.
            content.tabViewBottomAccessory(isEnabled: visible) {
                MiniPlayer()
                    .matchedTransitionSource(id: "miniplayer", in: namespace)
            }
        } else if visible {
            content.tabViewBottomAccessory {
                MiniPlayer()
                    .matchedTransitionSource(id: "miniplayer", in: namespace)
            }
        } else {
            content
        }
    }
}

private struct ToastView: View {
    private let player = PlayerController.shared

    var body: some View {
        if let msg = player.toast {
            Text(msg)
                .appFont(.subheadline, .semibold)
                .padding(.horizontal, 18)
                .padding(.vertical, 12)
                .glassEffect(.regular, in: .capsule)
                .padding(.top, 8)
                .transition(.move(edge: .top).combined(with: .opacity))
                .task(id: msg) {
                    try? await Task.sleep(for: .seconds(2.5))
                    withAnimation { player.toast = nil }
                }
        }
    }
}
