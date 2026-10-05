import AVFoundation
import MediaPlayer
import Network
import Observation
import SwiftUI
import UIKit

/// Port of the playback logic in `HearonApp` + `PlaybackService` (Android).
@MainActor
@Observable
final class PlayerController {
    static let shared = PlayerController()

    private(set) var current: Track?
    private(set) var isPlaying = false
    private(set) var isLoading = false
    private(set) var position: Double = 0
    private(set) var duration: Double = 0
    private(set) var queue: [Track] = []
    private(set) var index = -1
    private(set) var isShuffle = false
    var repeatMode: RepeatMode = .off
    private(set) var accentColor: Color?
    private(set) var isOnline = true
    /// Short message shown as a toast (e.g. no connection).
    var toast: String?

    private var originalQueue: [Track] = []
    private let player = AVPlayer()
    private var timeObserver: Any?
    private var endObserver: NSObjectProtocol?
    private let monitor = NWPathMonitor()
    private var loadTask: Task<Void, Never>?
    private var statusObservation: NSKeyValueObservation?

    var hasNext: Bool { index < queue.count - 1 || (repeatMode == .all && !queue.isEmpty) }
    var hasPrev: Bool { position > 3 || index > 0 }

    private init() {
        try? AVAudioSession.sharedInstance().setCategory(.playback, mode: .default, policy: .longFormAudio)
        player.automaticallyWaitsToMinimizeStalling = true

        timeObserver = player.addPeriodicTimeObserver(forInterval: CMTime(seconds: 0.25, preferredTimescale: 600), queue: .main) { [weak self] time in
            MainActor.assumeIsolated { self?.tick(time.seconds) }
        }
        endObserver = NotificationCenter.default.addObserver(forName: AVPlayerItem.didPlayToEndTimeNotification, object: nil, queue: .main) { [weak self] note in
            MainActor.assumeIsolated {
                guard let self, (note.object as? AVPlayerItem) === self.player.currentItem else { return }
                self.itemEnded()
            }
        }
        monitor.pathUpdateHandler = { [weak self] path in
            Task { @MainActor in self?.isOnline = path.status == .satisfied }
        }
        monitor.start(queue: DispatchQueue(label: "net"))
        setupRemoteCommands()
    }

    // MARK: - Starting playback

    /// Home / search: play the track and build the queue from its radio.
    func playRadio(_ track: Track) {
        guard canPlay(track) else { return }
        originalQueue = [track]
        queue = [track]
        index = 0
        load(track)
        Task {
            let related = isOnline ? await YTMusicClient.upNext(track.id).filter { $0.id != track.id } : DownloadManager.shared.tracks.filter { $0.id != track.id }
            guard current?.id == track.id else { return }
            originalQueue = [track] + related
            queue = isShuffle ? [track] + related.shuffled() : originalQueue
            index = 0
            prefetchNext()
        }
    }

    /// Library / playlists: play `track` inside `list`.
    func play(_ track: Track, in list: [Track]) {
        guard canPlay(track) else { return }
        originalQueue = list
        if isShuffle {
            queue = [track] + list.filter { $0.id != track.id }.shuffled()
            index = 0
        } else {
            queue = list
            index = list.firstIndex { $0.id == track.id } ?? 0
        }
        load(track)
    }

    /// Mix: the list shuffled, followed by radio tracks similar to it.
    func playMix(_ list: [Track]) {
        guard !list.isEmpty else { return }
        let shuffled = list.shuffled()
        guard let first = shuffled.first(where: canPlaySilently) else { return }
        originalQueue = shuffled
        queue = [first] + shuffled.filter { $0.id != first.id }
        index = 0
        load(first)
        guard isOnline else { return }
        Task {
            var seen = Set(queue.map(\.id))
            var extra: [Track] = []
            for seed in shuffled.prefix(3) {
                for t in await YTMusicClient.upNext(seed.id) where seen.insert(t.id).inserted {
                    extra.append(t)
                }
            }
            originalQueue += extra
            queue += extra.shuffled()
        }
    }

    func playFromQueue(_ i: Int) {
        guard queue.indices.contains(i), canPlay(queue[i]) else { return }
        index = i
        load(queue[i])
    }

    // MARK: - Controls

    func togglePlayPause() {
        if isPlaying { player.pause() } else { player.play() }
        isPlaying.toggle()
        updateNowPlaying()
    }

    func next() {
        if index < queue.count - 1 {
            move(to: index + 1)
        } else if repeatMode == .all, !queue.isEmpty {
            move(to: 0)
        }
    }

    func previous() {
        if position > 3 { seek(to: 0); return }
        if index > 0 { move(to: index - 1) }
    }

    func seek(to seconds: Double) {
        player.seek(to: CMTime(seconds: seconds, preferredTimescale: 600))
        position = seconds
        if !isPlaying { player.play(); isPlaying = true }
        updateNowPlaying()
    }

    func toggleShuffle() {
        isShuffle.toggle()
        guard let current else { return }
        if isShuffle {
            queue = [current] + originalQueue.filter { $0.id != current.id }.shuffled()
            index = 0
        } else {
            queue = originalQueue
            index = queue.firstIndex { $0.id == current.id } ?? 0
        }
    }

    func cycleRepeat() { repeatMode = repeatMode.next }

    // MARK: - Internals

    private func move(to i: Int) {
        let t = queue[i]
        guard canPlay(t) else { player.pause(); isPlaying = false; return }
        index = i
        load(t)
    }

    private func canPlaySilently(_ t: Track) -> Bool { isOnline || DownloadManager.shared.isDownloaded(t.id) }

    private func canPlay(_ t: Track) -> Bool {
        if canPlaySilently(t) { return true }
        toast = L.get("no_connection_track", AppSettings.shared.lang)
        return false
    }

    private func load(_ track: Track) {
        current = track
        position = 0
        duration = 0
        isLoading = true
        LibraryStore.shared.addRecent(track)
        updateNowPlaying()
        updateAccentColor(track)

        loadTask?.cancel()
        loadTask = Task {
            let local = DownloadManager.shared.isDownloaded(track.id)
            let url: URL? = local ? DownloadManager.shared.fileURL(track.id) : await StreamResolver.audioURL(for: track.id)?.url
            guard !Task.isCancelled, current?.id == track.id else { return }
            guard let url else {
                isLoading = false
                isPlaying = false
                toast = L.get("error_audio", AppSettings.shared.lang)
                return
            }
            try? AVAudioSession.sharedInstance().setActive(true)
            player.replaceCurrentItem(with: makeItem(url, track: track, local: local))
            player.volume = AppSettings.shared.crossfadeEnabled ? 0 : 1
            player.play()
            isPlaying = true
            isLoading = false
            if !local { DownloadManager.shared.download(track) }
            prefetchNext()
            updateNowPlaying()
        }
    }

    /// YouTube answers 403 to AVPlayer's default request headers; the same
    /// User-Agent the downloader uses is accepted.
    private func makeItem(_ url: URL, track: Track, local: Bool) -> AVPlayerItem {
        let asset = AVURLAsset(url: url, options: local ? nil : ["AVURLAssetHTTPHeaderFieldsKey": ["User-Agent": "Mozilla/5.0"]])
        let item = AVPlayerItem(asset: asset)
        statusObservation = item.observe(\.status) { [weak self] item, _ in
            guard item.status == .failed else { return }
            Task { @MainActor in self?.itemFailed(track) }
        }
        return item
    }

    private func itemFailed(_ track: Track) {
        guard current?.id == track.id else { return }
        print("Playback failed for \(track.id): \(String(describing: player.currentItem?.error))")
        isPlaying = false
        isLoading = false
        toast = L.get("error_audio", AppSettings.shared.lang)
        updateNowPlaying()
    }

    private func prefetchNext() {
        let i = index < queue.count - 1 ? index + 1 : (repeatMode == .all ? 0 : -1)
        guard i >= 0, queue.indices.contains(i), isOnline else { return }
        DownloadManager.shared.download(queue[i])
    }

    private func itemEnded() {
        if repeatMode == .one {
            seek(to: 0)
        } else if hasNext {
            next()
        } else {
            isPlaying = false
            updateNowPlaying()
        }
    }

    private func tick(_ seconds: Double) {
        guard seconds.isFinite else { return }
        position = max(0, seconds)
        if let d = player.currentItem?.duration.seconds, d.isFinite, d > 0 { duration = d }
        applyCrossfade()
    }

    /// Same behavior as Android: fade in at the start, fade out at the end and
    /// jump to the next track ~200 ms before the end.
    private func applyCrossfade() {
        let s = AppSettings.shared
        guard s.crossfadeEnabled, duration > 0 else { player.volume = 1; return }
        let fade = s.crossfadeDuration
        let remaining = duration - position
        if remaining <= fade {
            player.volume = Float(max(0, min(1, remaining / fade)))
            if remaining <= 0.2, repeatMode != .one, hasNext { next() }
        } else if position <= fade {
            player.volume = Float(max(0, min(1, position / fade)))
        } else {
            player.volume = 1
        }
    }

    // MARK: - Dynamic color

    private func updateAccentColor(_ track: Track) {
        guard let url = Covers.url(track, size: 120) else { accentColor = nil; return }
        Task {
            let color = await DominantColor.from(url)
            if current?.id == track.id { withAnimation(.easeInOut(duration: 1)) { accentColor = color } }
        }
    }

    // MARK: - Lock screen / Control Center

    private func setupRemoteCommands() {
        let c = MPRemoteCommandCenter.shared()
        c.playCommand.addTarget { [weak self] _ in
            MainActor.assumeIsolated { if self?.isPlaying == false { self?.togglePlayPause() } }
            return .success
        }
        c.pauseCommand.addTarget { [weak self] _ in
            MainActor.assumeIsolated { if self?.isPlaying == true { self?.togglePlayPause() } }
            return .success
        }
        c.togglePlayPauseCommand.addTarget { [weak self] _ in
            MainActor.assumeIsolated { self?.togglePlayPause() }
            return .success
        }
        c.nextTrackCommand.addTarget { [weak self] _ in
            MainActor.assumeIsolated { self?.next() }
            return .success
        }
        c.previousTrackCommand.addTarget { [weak self] _ in
            MainActor.assumeIsolated { self?.previous() }
            return .success
        }
        c.changePlaybackPositionCommand.addTarget { [weak self] e in
            guard let e = e as? MPChangePlaybackPositionCommandEvent else { return .commandFailed }
            MainActor.assumeIsolated { self?.seek(to: e.positionTime) }
            return .success
        }
    }

    /// Built outside the main actor: MediaPlayer calls the handler on a background queue.
    nonisolated private static func makeArtwork(_ img: UIImage) -> MPMediaItemArtwork {
        MPMediaItemArtwork(boundsSize: img.size) { _ in img }
    }

    private var artworkFor: String?
    private var artwork: MPMediaItemArtwork?

    private func updateNowPlaying() {
        guard let t = current else {
            MPNowPlayingInfoCenter.default().nowPlayingInfo = nil
            return
        }
        var info: [String: Any] = [
            MPMediaItemPropertyTitle: t.title,
            MPMediaItemPropertyArtist: t.artist,
            MPNowPlayingInfoPropertyElapsedPlaybackTime: position,
            MPMediaItemPropertyPlaybackDuration: duration,
            MPNowPlayingInfoPropertyPlaybackRate: isPlaying ? 1.0 : 0.0,
        ]
        if artworkFor == t.id, let artwork { info[MPMediaItemPropertyArtwork] = artwork }
        MPNowPlayingInfoCenter.default().nowPlayingInfo = info
        MPRemoteCommandCenter.shared().nextTrackCommand.isEnabled = hasNext
        MPRemoteCommandCenter.shared().previousTrackCommand.isEnabled = true

        if artworkFor != t.id, let url = Covers.url(t, size: 544) {
            artworkFor = t.id
            artwork = nil
            Task {
                guard let (data, _) = try? await URLSession.shared.data(from: url), let img = UIImage(data: data) else { return }
                guard current?.id == t.id else { return }
                artwork = Self.makeArtwork(img)
                updateNowPlaying()
            }
        }
    }
}
