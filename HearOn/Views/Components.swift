import SwiftUI

struct CoverImage: View {
    let track: Track?
    var size: Int = 226
    var radius: CGFloat = 12

    var body: some View {
        let _ = LibraryStore.shared.coverVersion
        // The placeholder sets the size; the image fills it and is cropped, so
        // 16:9 video thumbnails don't spill outside the square.
        Rectangle().fill(.quaternary)
            .overlay {
                AsyncImage(url: track.flatMap { Covers.url($0, size: size) }) { phase in
                    if let image = phase.image {
                        image.resizable().scaledToFill()
                    } else {
                        Image(systemName: "music.note").foregroundStyle(.secondary)
                    }
                }
            }
            .clipShape(.rect(cornerRadius: radius))
    }
}

/// 2×2 collage of the first four covers, or the single cover for short lists.
struct PlaylistCover: View {
    let tracks: [Track]
    var radius: CGFloat = 16

    var body: some View {
        let unique = Array(Dictionary(grouping: tracks, by: \.coverUrl).values.compactMap(\.first).prefix(4))
        Group {
            if tracks.isEmpty {
                Rectangle().fill(.tint.opacity(0.25))
                    .overlay { Image(systemName: "music.note.list").font(.largeTitle).foregroundStyle(.tint) }
            } else if unique.count < 4 {
                CoverImage(track: tracks.first, size: 544, radius: 0)
            } else {
                Grid(horizontalSpacing: 0, verticalSpacing: 0) {
                    GridRow { CoverImage(track: unique[0], radius: 0); CoverImage(track: unique[1], radius: 0) }
                    GridRow { CoverImage(track: unique[2], radius: 0); CoverImage(track: unique[3], radius: 0) }
                }
            }
        }
        .aspectRatio(1, contentMode: .fit)
        .clipShape(.rect(cornerRadius: radius))
    }
}

struct TrackRow: View {
    let track: Track
    var playlistContext: (id: UUID, index: Int)? = nil
    let onTap: () -> Void

    private var active: Bool { PlayerController.shared.current?.id == track.id }

    var body: some View {
        HStack(spacing: 4) {
            Button(action: onTap) {
                HStack(spacing: 14) {
                    CoverImage(track: track)
                        .frame(width: 56, height: 56)
                    VStack(alignment: .leading, spacing: 2) {
                        Text(track.title)
                            .appFont(.body, .semibold)
                            .foregroundStyle(active ? AnyShapeStyle(.tint) : AnyShapeStyle(.primary))
                            .lineLimit(1)
                        HStack(spacing: 4) {
                            if DownloadManager.shared.isDownloaded(track.id) {
                                Image(systemName: "arrow.down.circle.fill").font(.caption2).foregroundStyle(.secondary)
                            }
                            Text(track.artist).appFont(.subheadline).foregroundStyle(.secondary).lineLimit(1)
                        }
                    }
                    Spacer(minLength: 0)
                    if active {
                        Image(systemName: "waveform").symbolEffect(.variableColor.iterative, isActive: PlayerController.shared.isPlaying).foregroundStyle(.tint)
                    }
                }
                .contentShape(.rect)
            }
            .buttonStyle(.plain)
            .contextMenu { TrackMenuItems(track: track, playlistContext: playlistContext) }

            TrackMenu(track: track, playlistContext: playlistContext) {
                Image(systemName: "ellipsis").frame(width: 32, height: 44).contentShape(.rect)
            }
            .foregroundStyle(.secondary)
        }
    }
}

struct TrackMenu<Label: View>: View {
    let track: Track
    var playlistContext: (id: UUID, index: Int)? = nil
    @ViewBuilder let label: () -> Label

    var body: some View {
        Menu { TrackMenuItems(track: track, playlistContext: playlistContext) } label: { label() }
    }
}

/// Same options as the Android track bottom sheet, plus download.
struct TrackMenuItems: View {
    let track: Track
    var playlistContext: (id: UUID, index: Int)? = nil

    var body: some View {
        let library = LibraryStore.shared
        let downloads = DownloadManager.shared
        let liked = library.isLiked(track)

        Button(liked ? t("remove_favorite") : t("add_favorite"), systemImage: liked ? "heart.slash" : "heart") {
            library.toggleLike(track)
        }
        Button(t("add_to_playlist"), systemImage: "text.badge.plus") {
            UIState.shared.addToPlaylist = track
        }
        if let ctx = playlistContext {
            Button(t("remove_playlist"), systemImage: "minus.circle", role: .destructive) {
                library.remove(at: ctx.index, from: ctx.id)
            }
        }
        if downloads.isDownloaded(track.id) {
            Button(t("remove_download"), systemImage: "trash") { downloads.remove(track.id) }
        } else {
            Button(t("download"), systemImage: "arrow.down.circle") { downloads.download(track) }
                .disabled(downloads.inProgress.contains(track.id))
        }
        Divider()
        Button(t("change_cover"), systemImage: "photo") { UIState.shared.coverPickerFor = track }
        Button(t("track_info"), systemImage: "info.circle") { UIState.shared.infoFor = track }
    }
}

struct EmptyState: View {
    let text: String
    var systemImage = "music.note"

    var body: some View {
        ContentUnavailableView(text, systemImage: systemImage)
    }
}

struct AddToPlaylistSheet: View {
    let track: Track
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            List {
                if LibraryStore.shared.playlists.isEmpty {
                    Text(t("no_playlists_yet")).foregroundStyle(.secondary)
                }
                ForEach(LibraryStore.shared.playlists) { p in
                    Button {
                        LibraryStore.shared.add(track, to: p.id)
                        dismiss()
                    } label: {
                        HStack(spacing: 14) {
                            PlaylistCover(tracks: p.tracks, radius: 10).frame(width: 48, height: 48)
                            Text(p.name).appFont(.body, .semibold).foregroundStyle(.primary)
                        }
                    }
                }
            }
            .navigationTitle(t("add_to_playlist"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(role: .close) { dismiss() }
                }
            }
        }
        .presentationDetents([.medium, .large])
    }
}
