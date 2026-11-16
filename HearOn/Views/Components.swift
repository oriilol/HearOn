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
                            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                            .clipped()
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
    /// Off in reorderable lists, where long-press must start a drag.
    var longPressMenu = true
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
            .contextMenu(longPressMenu ? ContextMenu { TrackMenuItems(track: track, playlistContext: playlistContext) } : nil)

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
        let player = PlayerController.shared

        if player.current != nil, player.current?.id != track.id {
            Button(t("play_next"), systemImage: "text.line.first.and.arrowtriangle.forward") { player.playNext(track) }
            Button(t("add_to_queue"), systemImage: "text.line.last.and.arrowtriangle.forward") { player.addToQueue(track) }
            Divider()
        }
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

/// Track details: replaces the plain-text alert from 1.3.0.
struct TrackInfoSheet: View {
    let track: Track
    @Environment(\.dismiss) private var dismiss
    @State private var copied = false
    private let player = PlayerController.shared

    private var isLocal: Bool { DownloadManager.shared.isDownloaded(track.id) }

    private var fileSize: String? {
        guard isLocal, let bytes = (try? FileManager.default.attributesOfItem(atPath: DownloadManager.shared.fileURL(track.id).path))?[.size] as? Int64 else { return nil }
        return ByteCountFormatter.string(fromByteCount: bytes, countStyle: .file)
    }

    private var shareURL: URL { URL(string: "https://music.youtube.com/watch?v=\(track.id)")! }

    var body: some View {
        NavigationStack {
            List {
                Section {
                    VStack(spacing: 12) {
                        CoverImage(track: track, size: 544, radius: 20)
                            .frame(width: 160, height: 160)
                            .shadow(color: .black.opacity(0.2), radius: 12, y: 6)
                        VStack(spacing: 2) {
                            Text(track.title).appFont(.title3, .bold).multilineTextAlignment(.center)
                            Text(track.artist).appFont(.body).foregroundStyle(.secondary)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .listRowBackground(Color.clear)
                }
                Section {
                    if player.current?.id == track.id, player.duration > 0 {
                        LabeledContent(t("duration"), value: formatTime(player.duration))
                    }
                    LabeledContent(t("origin_label").trimmingCharacters(in: CharacterSet(charactersIn: ": ")), value: isLocal ? t("local_cache") : t("streaming"))
                    if let bitrate = StreamResolver.cachedBitrate(for: track.id) {
                        LabeledContent(t("bitrate"), value: "\(bitrate / 1000) kbps")
                    } else {
                        LabeledContent(t("bitrate"), value: t("quality_\(AppSettings.shared.audioQuality.rawValue)"))
                    }
                    if let fileSize { LabeledContent(t("file_size"), value: fileSize) }
                    LabeledContent(t("yt_id_label").trimmingCharacters(in: CharacterSet(charactersIn: ": "))) {
                        Text(track.id).monospaced()
                    }
                }
                Section {
                    Button(copied ? t("copied") : t("copy_id"), systemImage: copied ? "checkmark" : "doc.on.doc") {
                        UIPasteboard.general.string = track.id
                        withAnimation { copied = true }
                    }
                    ShareLink(item: shareURL) { Label(t("share"), systemImage: "square.and.arrow.up") }
                }
            }
            .navigationTitle(t("track_info_title"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button(role: .close) { dismiss() } }
            }
        }
        .presentationDetents([.medium, .large])
    }
}
