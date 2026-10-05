import SwiftUI

struct MiniPlayer: View {
    @Environment(\.tabViewBottomAccessoryPlacement) private var placement
    private let player = PlayerController.shared

    var body: some View {
        if let track = player.current {
            HStack(spacing: 10) {
                Button { UIState.shared.showPlayer = true } label: {
                    HStack(spacing: 10) {
                        CoverImage(track: track, size: 120, radius: 8)
                            .frame(width: 32, height: 32)
                        VStack(alignment: .leading, spacing: 0) {
                            Text(track.title).appFont(.subheadline, .semibold).lineLimit(1)
                            if placement != .inline {
                                Text(track.artist).appFont(.caption).foregroundStyle(.secondary).lineLimit(1)
                            }
                        }
                        Spacer(minLength: 0)
                    }
                    .contentShape(.rect)
                }
                .buttonStyle(.plain)
                Button { player.togglePlayPause() } label: {
                    Image(systemName: player.isPlaying ? "pause.fill" : "play.fill")
                        .contentTransition(.symbolEffect(.replace))
                        .frame(width: 32, height: 32)
                }
                if placement != .inline {
                    Button { player.next() } label: {
                        Image(systemName: "forward.fill").frame(width: 32, height: 32)
                    }
                    .disabled(!player.hasNext)
                }
            }
            .foregroundStyle(.primary)
            .padding(.horizontal, 12)
        }
    }
}

struct PlayerScreen: View {
    enum Page { case player, lyrics, queue }

    @Environment(\.dismiss) private var dismiss
    @Environment(\.verticalSizeClass) private var vSize
    @State private var page: Page = .player
    @State private var scrubbing: Double?
    private let player = PlayerController.shared

    var body: some View {
        ZStack {
            background
            if let track = player.current {
                switch page {
                case .player:
                    if vSize == .compact {
                        HStack(spacing: 32) { artwork(track); controls(track) }.padding(24)
                    } else {
                        VStack(spacing: 0) { header; Spacer(); artwork(track); Spacer(); controls(track) }
                            .padding(.horizontal, 24).padding(.bottom, 12)
                    }
                case .lyrics:
                    LyricsView(track: track, onBack: { page = .player })
                case .queue:
                    QueueView(onBack: { page = .player })
                }
            }
        }
        .animation(.smooth, value: page)
        .gesture(DragGesture().onEnded { if $0.translation.height > 120 && page == .player { dismiss() } })
        .onChange(of: player.current == nil) { if $1 { dismiss() } }
    }

    private var background: some View {
        LinearGradient(colors: [(player.accentColor ?? .gray).opacity(0.55), Color(.systemBackground)], startPoint: .top, endPoint: .bottom)
            .ignoresSafeArea()
    }

    private var header: some View {
        HStack {
            Button { dismiss() } label: { Image(systemName: "chevron.down").frame(width: 44, height: 44) }
                .buttonStyle(.glass).buttonBorderShape(.circle).tint(.primary)
            Spacer()
            Text(t("now_playing")).appFont(.subheadline, .semibold).foregroundStyle(.secondary)
            Spacer()
            if let track = player.current {
                TrackMenu(track: track) { Image(systemName: "ellipsis").frame(width: 44, height: 44) }
                    .buttonStyle(.glass).buttonBorderShape(.circle).tint(.primary)
            }
        }
        .padding(.top, 8)
    }

    private func artwork(_ track: Track) -> some View {
        CoverImage(track: track, size: 1080, radius: player.isPlaying ? 28 : 20)
            .aspectRatio(1, contentMode: .fit)
            .scaleEffect(player.isPlaying ? 1 : 0.85)
            .shadow(color: .black.opacity(0.25), radius: 24, y: 12)
            .animation(.spring(response: 0.5, dampingFraction: 0.6), value: player.isPlaying)
            .id(track.id)
            .transition(.scale(scale: 0.8).combined(with: .opacity))
    }

    private func controls(_ track: Track) -> some View {
        VStack(spacing: 22) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text(track.title).appFont(.title2, .bold).lineLimit(1)
                    Text(track.artist).appFont(.title3).foregroundStyle(.secondary).lineLimit(1)
                }
                Spacer()
                Button { LibraryStore.shared.toggleLike(track) } label: {
                    Image(systemName: LibraryStore.shared.isLiked(track) ? "heart.fill" : "heart")
                        .contentTransition(.symbolEffect(.replace))
                        .frame(width: 44, height: 44)
                }
                .buttonStyle(.glass).buttonBorderShape(.circle)
                .tint(LibraryStore.shared.isLiked(track) ? .red : .primary)
            }

            ProgressSlider(scrubbing: $scrubbing)

            GlassEffectContainer(spacing: 24) {
                HStack(spacing: 28) {
                    Button { player.previous() } label: { Image(systemName: "backward.fill").font(.title).frame(width: 64, height: 64) }
                        .buttonStyle(.glass).buttonBorderShape(.circle).tint(.primary)
                        .disabled(!player.hasPrev)
                    Button { player.togglePlayPause() } label: {
                        Group {
                            if player.isLoading { ProgressView() } else {
                                Image(systemName: player.isPlaying ? "pause.fill" : "play.fill")
                                    .contentTransition(.symbolEffect(.replace))
                            }
                        }
                        .font(.largeTitle).frame(width: 84, height: 84)
                    }
                    .buttonStyle(.glassProminent).buttonBorderShape(.circle)
                    Button { player.next() } label: { Image(systemName: "forward.fill").font(.title).frame(width: 64, height: 64) }
                        .buttonStyle(.glass).buttonBorderShape(.circle).tint(.primary)
                        .disabled(!player.hasNext)
                }
            }

            GlassEffectContainer(spacing: 16) {
                HStack(spacing: 16) {
                    toggle("quote.bubble", active: page == .lyrics) { page = .lyrics }
                    toggle("shuffle", active: player.isShuffle) { player.toggleShuffle() }
                    toggle(player.repeatMode == .one ? "repeat.1" : "repeat", active: player.repeatMode != .off) { player.cycleRepeat() }
                    toggle("list.bullet", active: page == .queue) { page = .queue }
                }
            }
        }
    }

    private func toggle(_ icon: String, active: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Image(systemName: icon).font(.title3).frame(width: 52, height: 44)
        }
        .buttonStyle(.glass)
        .tint(active ? (player.accentColor ?? .accentColor) : .secondary)
    }
}

struct ProgressSlider: View {
    @Binding var scrubbing: Double?
    private let player = PlayerController.shared

    var body: some View {
        VStack(spacing: 4) {
            Slider(
                value: Binding(get: { scrubbing ?? player.position }, set: { scrubbing = $0 }),
                in: 0...max(player.duration, 1),
                onEditingChanged: { editing in
                    if !editing, let s = scrubbing { player.seek(to: s); scrubbing = nil }
                }
            )
            HStack {
                Text(formatTime(scrubbing ?? player.position))
                Spacer()
                Text(formatTime(player.duration))
            }
            .appFont(.caption).monospacedDigit().foregroundStyle(.secondary)
        }
    }
}

struct LyricsView: View {
    let track: Track
    let onBack: () -> Void
    @State private var synced: [LyricLine] = []
    @State private var plain: String?
    @State private var loading = true
    @State private var editing = false
    @State private var input = ""
    @State private var scrubbing: Double?
    private let player = PlayerController.shared

    private var activeIndex: Int {
        let ms = Int(player.position * 1000)
        return synced.lastIndex { $0.timeMs <= ms } ?? 0
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: onBack) { Image(systemName: "chevron.backward").frame(width: 44, height: 44) }
                    .buttonStyle(.glass).buttonBorderShape(.circle).tint(.primary)
                Spacer()
                Text(t("lyrics")).appFont(.headline)
                Spacer()
                Button {
                    input = LibraryStore.shared.manualLyrics[track.id] ?? ""
                    editing = true
                } label: { Image(systemName: "pencil").frame(width: 44, height: 44) }
                    .buttonStyle(.glass).buttonBorderShape(.circle).tint(.primary)
            }
            .padding()

            Group {
                if loading {
                    ProgressView().frame(maxHeight: .infinity)
                } else if !synced.isEmpty {
                    syncedView
                } else if let plain {
                    ScrollView {
                        Text(plain).appFont(.title3, .semibold).multilineTextAlignment(.center).padding(32)
                    }
                } else {
                    VStack(spacing: 16) {
                        Text(t("no_lyrics_found")).foregroundStyle(.secondary)
                        Button(t("add_lyrics")) { editing = true }.buttonStyle(.glassProminent)
                    }
                    .frame(maxHeight: .infinity)
                }
            }

            HStack(spacing: 16) {
                ProgressSlider(scrubbing: $scrubbing)
                Button { player.togglePlayPause() } label: {
                    Image(systemName: player.isPlaying ? "pause.fill" : "play.fill").font(.title2).frame(width: 52, height: 52)
                }
                .buttonStyle(.glassProminent).buttonBorderShape(.circle)
            }
            .padding()
            .glassEffect(.regular, in: .rect(cornerRadius: 32))
            .padding()
        }
        .task(id: track.id) { await load() }
        .sheet(isPresented: $editing) {
            NavigationStack {
                TextEditor(text: $input)
                    .padding()
                    .overlay(alignment: .topLeading) {
                        if input.isEmpty { Text(t("paste_lyrics_here")).foregroundStyle(.tertiary).padding(24).allowsHitTesting(false) }
                    }
                    .navigationTitle(t("add_lyrics_manual"))
                    .navigationBarTitleDisplayMode(.inline)
                    .toolbar {
                        ToolbarItem(placement: .cancellationAction) { Button(t("cancel")) { editing = false } }
                        ToolbarItem(placement: .confirmationAction) {
                            Button(t("save")) {
                                LibraryStore.shared.setManualLyrics(input, for: track.id)
                                apply(input)
                                editing = false
                            }
                        }
                    }
            }
        }
    }

    private var syncedView: some View {
        ScrollViewReader { proxy in
            ScrollView {
                LazyVStack(spacing: 28) {
                    ForEach(synced) { line in
                        let active = line.id == activeIndex
                        Group {
                            if line.isInstrumental {
                                Text("•••").appFont(.title, .bold)
                                    .symbolEffect(.pulse, isActive: active)
                            } else {
                                Text(line.text).appFont(active ? .title : .title2, .bold)
                            }
                        }
                        .multilineTextAlignment(.center)
                        .foregroundStyle(active ? .primary : .tertiary)
                        .scaleEffect(active ? 1.04 : 1)
                        .animation(.spring, value: active)
                        .frame(maxWidth: .infinity)
                        .padding(.horizontal, 24)
                        .id(line.id)
                        .onTapGesture { player.seek(to: Double(line.timeMs) / 1000) }
                    }
                }
                .padding(.vertical, 240)
            }
            .scrollIndicators(.hidden)
            .onChange(of: activeIndex) { _, i in
                withAnimation(.smooth) { proxy.scrollTo(i, anchor: .center) }
            }
        }
    }

    private func load() async {
        loading = true
        synced = []
        plain = nil
        if let manual = LibraryStore.shared.manualLyrics[track.id] {
            apply(manual)
        } else if player.isOnline {
            let (s, p) = await YTMusicClient.lyrics(title: track.title, artist: track.artist)
            if let s { synced = LyricLine.parse(s) }
            plain = p
        }
        loading = false
    }

    private func apply(_ text: String) {
        if text.contains("[") { synced = LyricLine.parse(text); plain = nil } else { synced = []; plain = text }
    }
}

struct QueueView: View {
    let onBack: () -> Void
    private let player = PlayerController.shared

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: onBack) { Image(systemName: "chevron.backward").frame(width: 44, height: 44) }
                    .buttonStyle(.glass).buttonBorderShape(.circle).tint(.primary)
                Spacer()
                Text(t("play_queue")).appFont(.headline)
                Spacer()
                Color.clear.frame(width: 44, height: 44)
            }
            .padding()

            ScrollViewReader { proxy in
                List {
                    ForEach(Array(player.queue.enumerated()), id: \.offset) { i, track in
                        TrackRow(track: track) { player.playFromQueue(i) }
                            .listRowBackground(i == player.index ? Color.accentColor.opacity(0.15) : Color.clear)
                            .id(i)
                    }
                }
                .listStyle(.plain)
                .scrollContentBackground(.hidden)
                .onAppear { proxy.scrollTo(player.index, anchor: .top) }
            }
        }
    }
}
