import PhotosUI
import SwiftUI

enum AppTab: Hashable { case home, library, settings, search }

struct RootView: View {
    @State private var tab: AppTab = .home
    @State private var ui = UIState.shared
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
                    // Sheets opened from the player's menu must be presented from the
                    // player itself: the root view is underneath this cover.
                    .modifier(TrackActionPresenters(isActive: ui.showPlayer))
                    .overlay(alignment: .top) { ToastView() }
                    .tint(settings.dynamicColors ? (player.accentColor ?? .accentColor) : .accentColor)
                    .navigationTransition(.zoom(sourceID: "miniplayer", in: playerNS))
            }
            .modifier(TrackActionPresenters(isActive: !ui.showPlayer))
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

    private func checkForUpdate() async {
        guard settings.notifyUpdate, let latest = await YTMusicClient.latestVersion() else { return }
        let current = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0"
        if latest.compare(current, options: .numeric) == .orderedDescending {
            player.toast = t("update_available") + latest
        }
    }
}

/// Add to playlist, track info and cover picker, opened from any track menu.
/// Attached both to the root view and to the full-screen player; only the one
/// on top is active, because SwiftUI can't present over a full-screen cover
/// from underneath it (which is why these did nothing from the player in 1.3.0).
private struct TrackActionPresenters: ViewModifier {
    let isActive: Bool
    @State private var ui = UIState.shared
    @State private var photoItem: PhotosPickerItem?

    func body(content: Content) -> some View {
        content
            .sheet(item: Binding(get: { isActive ? ui.addToPlaylist : nil }, set: { ui.addToPlaylist = $0 })) { track in
                AddToPlaylistSheet(track: track)
            }
            .sheet(item: Binding(get: { isActive ? ui.infoFor : nil }, set: { ui.infoFor = $0 })) { track in
                TrackInfoSheet(track: track)
            }
            .photosPicker(
                isPresented: Binding(get: { isActive && ui.coverPickerFor != nil }, set: { if !$0 && photoItem == nil { ui.coverPickerFor = nil } }),
                selection: $photoItem, matching: .images
            )
            .onChange(of: photoItem) { _, item in
                guard let item, let track = ui.coverPickerFor else { return }
                Task {
                    if let data = try? await item.loadTransferable(type: Data.self) {
                        LibraryStore.shared.setCustomCover(data, for: track.id)
                        PlayerController.shared.toast = t("cover_saved")
                    }
                    photoItem = nil
                    ui.coverPickerFor = nil
                }
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

struct ToastView: View {
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
