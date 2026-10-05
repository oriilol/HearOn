import SwiftUI

struct HomeScreen: View {
    enum Section: String, CaseIterable { case trending, forYou = "for_you", recent }

    @State private var section: Section = .trending
    @State private var trending: [Track] = []
    @State private var forYou: [Track] = []
    @State private var loading = false
    @State private var forYouSeed: String?
    private let player = PlayerController.shared
    private let library = LibraryStore.shared

    private var list: [Track] {
        switch section {
        case .trending: trending
        case .forYou: forYou
        case .recent: library.recent
        }
    }

    var body: some View {
        NavigationStack {
            List {
                Picker("", selection: $section) {
                    ForEach(Section.allCases, id: \.self) { Text(t($0.rawValue)).tag($0) }
                }
                .pickerStyle(.segmented)
                .listRowSeparator(.hidden)
                .listRowBackground(Color.clear)

                ForEach(list) { track in
                    TrackRow(track: track) {
                        if section == .recent { player.play(track, in: library.recent) } else { player.playRadio(track) }
                    }
                }
            }
            .listStyle(.plain)
            .overlay {
                if loading && list.isEmpty {
                    ProgressView()
                } else if list.isEmpty {
                    if !player.isOnline && section != .recent {
                        EmptyState(text: t("offline_title"), systemImage: "wifi.slash")
                    } else {
                        EmptyState(text: section == .trending ? t("nothing_to_show") : t("no_recent_songs"))
                    }
                }
            }
            .refreshable { await reload() }
            .navigationTitle(t("home"))
            .task { if trending.isEmpty { await reload() } }
            .task(id: library.recent.first?.id) { await loadForYou() }
        }
    }

    private func reload() async {
        guard player.isOnline else { return }
        loading = true
        trending = await YTMusicClient.trending()
        loading = false
        forYouSeed = nil
        await loadForYou()
    }

    /// "For you": radio of the last played track (same as Android).
    private func loadForYou() async {
        guard let seed = library.recent.first?.id, seed != forYouSeed, player.isOnline else { return }
        forYouSeed = seed
        forYou = await YTMusicClient.upNext(seed).filter { $0.id != seed }
    }
}
