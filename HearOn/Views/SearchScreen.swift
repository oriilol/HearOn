import SwiftUI

struct SearchScreen: View {
    @State private var query = ""
    @State private var type: YTMusicClient.SearchType = .music
    @State private var results: [Track] = []
    @State private var searching = false
    @State private var hasSearched = false
    private let player = PlayerController.shared

    var body: some View {
        NavigationStack {
            List {
                Picker("", selection: $type) {
                    Text(t("yt_music")).tag(YTMusicClient.SearchType.music)
                    Text(t("youtube")).tag(YTMusicClient.SearchType.video)
                }
                .pickerStyle(.segmented)
                .listRowSeparator(.hidden)
                .listRowBackground(Color.clear)

                ForEach(results) { track in
                    TrackRow(track: track) { player.playRadio(track) }
                }
            }
            .listStyle(.plain)
            .overlay {
                if !player.isOnline {
                    EmptyState(text: t("offline_title"), systemImage: "wifi.slash")
                } else if searching {
                    ProgressView()
                } else if hasSearched && results.isEmpty {
                    EmptyState(text: t("no_results"), systemImage: "magnifyingglass")
                }
            }
            .navigationTitle(t("explore"))
            .searchable(text: $query, prompt: t("search_placeholder"))
            .onSubmit(of: .search) { Task { await search() } }
            .onChange(of: type) { if !query.isEmpty { Task { await search() } } }
        }
    }

    private func search() async {
        let q = query.trimmingCharacters(in: .whitespaces)
        guard !q.isEmpty else { return }
        searching = true
        hasSearched = true
        results = await YTMusicClient.search(q, type: type)
        searching = false
    }
}
