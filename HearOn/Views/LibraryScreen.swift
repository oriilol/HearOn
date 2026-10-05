import SwiftUI

struct LibraryScreen: View {
    enum Section: String, CaseIterable { case favorites, playlists, downloads }

    @State private var section: Section = .favorites
    @State private var showNew = false
    @State private var newName = ""
    @State private var renaming: Playlist?
    @State private var renameText = ""
    @State private var deleting: Playlist?
    private let library = LibraryStore.shared
    private let downloads = DownloadManager.shared
    private let player = PlayerController.shared
    private let columns = [GridItem(.adaptive(minimum: 160), spacing: 16)]

    var body: some View {
        NavigationStack {
            ScrollView {
                Picker("", selection: $section) {
                    ForEach(Section.allCases, id: \.self) { Text(t($0.rawValue)).tag($0) }
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)

                switch section {
                case .favorites: favorites
                case .playlists: playlists
                case .downloads: downloadsList
                }
            }
            .navigationTitle(t("your_library"))
            .refreshable { downloads.verify() }
            .navigationDestination(for: UUID.self) { PlaylistDetail(id: $0) }
            .alert(t("new_playlist"), isPresented: $showNew) {
                TextField(t("playlist_name"), text: $newName)
                Button(t("cancel"), role: .cancel) { newName = "" }
                Button(t("create")) {
                    let n = newName.trimmingCharacters(in: .whitespaces)
                    if !n.isEmpty { library.createPlaylist(n) }
                    newName = ""
                }
            }
            .alert(t("rename_playlist"), isPresented: Binding(get: { renaming != nil }, set: { if !$0 { renaming = nil } })) {
                TextField(t("new_name"), text: $renameText)
                Button(t("cancel"), role: .cancel) {}
                Button(t("save")) {
                    let n = renameText.trimmingCharacters(in: .whitespaces)
                    if let p = renaming, !n.isEmpty { library.rename(p.id, to: n) }
                }
            }
            .alert(t("delete_playlist"), isPresented: Binding(get: { deleting != nil }, set: { if !$0 { deleting = nil } })) {
                Button(t("cancel"), role: .cancel) {}
                Button(t("delete"), role: .destructive) { if let p = deleting { library.deletePlaylist(p.id) } }
            } message: {
                Text(t("delete_playlist_confirm"))
            }
        }
    }

    @ViewBuilder private var favorites: some View {
        if library.liked.isEmpty {
            EmptyState(text: t("no_favorites_yet"), systemImage: "heart").padding(.top, 80)
        } else {
            mixButton(library.liked)
            LazyVGrid(columns: columns, spacing: 16) {
                ForEach(library.liked) { track in
                    Button { player.play(track, in: library.liked) } label: {
                        VStack(alignment: .leading, spacing: 6) {
                            CoverImage(track: track, size: 544, radius: 16).aspectRatio(1, contentMode: .fit)
                            Text(track.title).appFont(.subheadline, .semibold).lineLimit(1)
                            Text(track.artist).appFont(.caption).foregroundStyle(.secondary).lineLimit(1)
                        }
                    }
                    .buttonStyle(.plain)
                    .contextMenu { TrackMenuItems(track: track) }
                }
            }
            .padding()
        }
    }

    private var playlists: some View {
        LazyVGrid(columns: columns, spacing: 16) {
            Button { showNew = true } label: {
                VStack(spacing: 6) {
                    RoundedRectangle(cornerRadius: 16).fill(.quaternary)
                        .aspectRatio(1, contentMode: .fit)
                        .overlay { Image(systemName: "plus").font(.largeTitle).foregroundStyle(.secondary) }
                    Text(t("new_playlist")).appFont(.subheadline, .semibold).frame(maxWidth: .infinity, alignment: .leading)
                    Text(" ").appFont(.caption)
                }
            }
            .buttonStyle(.plain)

            ForEach(library.playlists) { p in
                NavigationLink(value: p.id) {
                    VStack(alignment: .leading, spacing: 6) {
                        PlaylistCover(tracks: p.tracks)
                        Text(p.name).appFont(.subheadline, .semibold).lineLimit(1)
                        Text("\(p.tracks.count) \(t("tracks"))").appFont(.caption).foregroundStyle(.secondary)
                    }
                }
                .buttonStyle(.plain)
                .contextMenu {
                    Button(t("mix"), systemImage: "shuffle") { player.playMix(p.tracks) }.disabled(p.tracks.isEmpty)
                    Button(t("rename_playlist"), systemImage: "pencil") { renameText = p.name; renaming = p }
                    Button(t("delete_playlist"), systemImage: "trash", role: .destructive) { deleting = p }
                }
            }
        }
        .padding()
    }

    @ViewBuilder private var downloadsList: some View {
        if downloads.tracks.isEmpty {
            EmptyState(text: t("no_downloads_yet"), systemImage: "arrow.down.circle").padding(.top, 80)
        } else {
            mixButton(downloads.tracks)
            LazyVStack(spacing: 12) {
                ForEach(downloads.tracks) { track in
                    TrackRow(track: track) { player.play(track, in: downloads.tracks) }
                }
            }
            .padding()
        }
    }

    private func mixButton(_ tracks: [Track]) -> some View {
        Button(t("mix"), systemImage: "shuffle") { player.playMix(tracks) }
            .buttonStyle(.glassProminent)
            .controlSize(.large)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal)
            .padding(.top, 12)
    }
}

struct PlaylistDetail: View {
    let id: UUID
    private let library = LibraryStore.shared
    private let player = PlayerController.shared

    var body: some View {
        if let p = library.playlists.first(where: { $0.id == id }) {
            List {
                VStack(spacing: 16) {
                    PlaylistCover(tracks: p.tracks, radius: 24).frame(maxWidth: 260)
                    Text(p.name).appFont(.title2, .bold)
                    Text("\(p.tracks.count) \(t("tracks"))").appFont(.subheadline).foregroundStyle(.secondary)
                    GlassEffectContainer {
                        HStack(spacing: 12) {
                            Button(t("play_all"), systemImage: "play.fill") {
                                if let first = p.tracks.first { player.play(first, in: p.tracks) }
                            }
                            .buttonStyle(.glassProminent)
                            Button(t("mix"), systemImage: "shuffle") { player.playMix(p.tracks) }
                                .buttonStyle(.glass)
                        }
                        .controlSize(.large)
                        .disabled(p.tracks.isEmpty)
                    }
                }
                .frame(maxWidth: .infinity)
                .listRowSeparator(.hidden)
                .listRowBackground(Color.clear)

                ForEach(Array(p.tracks.enumerated()), id: \.offset) { i, track in
                    TrackRow(track: track, playlistContext: (p.id, i)) { player.play(track, in: p.tracks) }
                }
                .onMove { library.move(in: p.id, from: $0, to: $1) }
                .onDelete { $0.sorted(by: >).forEach { library.remove(at: $0, from: p.id) } }
            }
            .listStyle(.plain)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar { EditButton() }
        }
    }
}
