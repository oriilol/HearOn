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
    /// Real duration of the current track. YouTube's m4a files often declare
    /// twice their length in the header, which AVFoundation trusts.
    private var realDuration: Double?
    private var endHandled = false
    /// Position to start from when resuming the session saved on the last run.
    private var pendingResume: Double?
    private var lastSave = Date.distantPast

    enum SleepTimer: Equatable { case off, minutes(Int), endOfTrack }
    private(set) var sleepTimer: SleepTimer = .off
    private(set) var sleepEnd: Date?

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
                guard let self, (note.object as? AVPlayerItem) === self.player.currentItem, !self.endHandled else { return }
                self.endHandled = true
                self.itemEnded()
            }
        }
        monitor.pathUpdateHandler = { [weak self] path in
            Task { @MainActor in self?.isOnline = path.status == .satisfied }
        }
        monitor.start(queue: DispatchQueue(label: "net"))
        setupRemoteCommands()
        restoreState()
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
        // First play after launching: load the restored track where it was left.
        if !isPlaying, player.currentItem == nil, let current {
            load(current, startAt: pendingResume)
            pendingResume = nil
            return
        }
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
        if seconds < (realDuration ?? .infinity) - 1 { endHandled = false }
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

    // MARK: - Queue editing

    /// Inserts `track` right after the current one.
    func playNext(_ track: Track) {
        guard current != nil else { playRadio(track); return }
        queue.insert(track, at: min(index + 1, queue.count))
        originalQueue = queue
        toast = L.get("will_play_next", AppSettings.shared.lang)
        saveState()
    }

    func addToQueue(_ track: Track) {
        guard current != nil else { playRadio(track); return }
        queue.append(track)
        originalQueue = queue
        toast = L.get("added_to_queue", AppSettings.shared.lang)
        saveState()
    }

    func moveInQueue(from offsets: IndexSet, to destination: Int) {
        var order = Array(queue.indices)
        order.move(fromOffsets: offsets, toOffset: destination)
        queue = order.map { queue[$0] }
        index = order.firstIndex(of: index) ?? index
        originalQueue = queue
        saveState()
    }

    /// Removes tracks from the queue; the one playing is never removed.
    func removeFromQueue(at offsets: IndexSet) {
        let removable = offsets.filter { $0 != index }
        let before = removable.filter { $0 < index }.count
        queue.remove(atOffsets: IndexSet(removable))
        index -= before
        originalQueue = queue
        saveState()
    }

    // MARK: - Sleep timer

    func setSleepTimer(_ timer: SleepTimer) {
        sleepTimer = timer
        switch timer {
        case .minutes(let m):
            sleepEnd = .now.addingTimeInterval(Double(m) * 60)
            toast = String(format: L.get("sleep_in", AppSettings.shared.lang), "\(m) min")
        case .endOfTrack:
            sleepEnd = nil
            toast = L.get("sleep_end_of_track", AppSettings.shared.lang)
        case .off:
            sleepEnd = nil
        }
    }

    // MARK: - Resume where you left off

    private struct SavedSession: Codable {
        var queue: [Track]
        var index: Int
        var position: Double
        var isShuffle: Bool
        var repeatMode: RepeatMode
        var duration: Double?
    }

    private static let sessionURL = LibraryStore.supportDir.appending(path: "session.json")

    func saveState() {
        lastSave = .now
        guard current != nil, queue.indices.contains(index) else { return }
        let session = SavedSession(queue: queue, index: index, position: position, isShuffle: isShuffle, repeatMode: repeatMode, duration: duration)
        if let data = try? JSONEncoder().encode(session) { try? data.write(to: Self.sessionURL, options: .atomic) }
    }

    private func restoreState() {
        guard let data = try? Data(contentsOf: Self.sessionURL),
              let s = try? JSONDecoder().decode(SavedSession.self, from: data),
              s.queue.indices.contains(s.index) else { return }
        queue = s.queue
        originalQueue = s.queue
        index = s.index
        current = s.queue[s.index]
        position = s.position
        duration = s.duration ?? 0
        if duration == 0, DownloadManager.shared.isDownloaded(s.queue[s.index].id) {
            duration = AudioDuration.ofFile(DownloadManager.shared.fileURL(s.queue[s.index].id)) ?? 0
        }
        isShuffle = s.isShuffle
        repeatMode = s.repeatMode
        pendingResume = s.position
        if let t = current { updateAccentColor(t) }
        updateNowPlaying()
    }

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

    private func load(_ track: Track, startAt: Double? = nil) {
        current = track
        position = 0
        duration = 0
        realDuration = nil
        endHandled = false
        isLoading = true
        LibraryStore.shared.addRecent(track)
        updateNowPlaying()
        updateAccentColor(track)

        loadTask?.cancel()
        loadTask = Task {
            let local = DownloadManager.shared.isDownloaded(track.id)
            let resolved = local ? nil : await StreamResolver.audioURL(for: track.id)
            let url: URL? = local ? DownloadManager.shared.fileURL(track.id) : resolved?.url
            realDuration = local ? AudioDuration.ofFile(DownloadManager.shared.fileURL(track.id)) : resolved.flatMap { AudioDuration.fromStreamURL($0.url) }
            guard !Task.isCancelled, current?.id == track.id else { return }
            guard let url else {
                isLoading = false
                isPlaying = false
                toast = L.get("error_audio", AppSettings.shared.lang)
                return
            }
            try? AVAudioSession.sharedInstance().setActive(true)
            player.replaceCurrentItem(with: makeItem(url, track: track, local: local))
            if let startAt, startAt > 1 {
                await player.seek(to: CMTime(seconds: startAt, preferredTimescale: 600))
                position = startAt
            }
            player.volume = AppSettings.shared.crossfadeEnabled ? 0 : 1
            player.play()
            isPlaying = true
            isLoading = false
            if !local { DownloadManager.shared.download(track) }
            prefetchNext()
            updateNowPlaying()
            saveState()
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
        if sleepTimer == .endOfTrack {
            player.pause()
            isPlaying = false
            setSleepTimer(.off)
            updateNowPlaying()
            return
        }
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
        if let real = realDuration {
            duration = real
        } else if let d = player.currentItem?.duration.seconds, d.isFinite, d > 0 {
            duration = d
        }
        // The item doesn't end on its own until the (wrong) header duration, so
        // move on when the real audio is over.
        if let sleepEnd, Date.now >= sleepEnd, isPlaying {
            togglePlayPause()
            setSleepTimer(.off)
        }
        if Date.now.timeIntervalSince(lastSave) > 5 { saveState() }
        if let real = realDuration, !endHandled, position >= real - 0.3, isPlaying {
            endHandled = true
            itemEnded()
            return
        }
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

import AudioToolbox

enum AudioDuration {
    /// googlevideo URLs carry the real length in the `dur` query parameter.
    static func fromStreamURL(_ url: URL) -> Double? {
        URLComponents(url: url, resolvingAgainstBaseURL: false)?.queryItems?.first { $0.name == "dur" }?.value.flatMap(Double.init)
    }

    /// Duration measured from the audio packets, not the container header.
    static func ofFile(_ url: URL) -> Double? {
        var fileID: AudioFileID?
        guard AudioFileOpenURL(url as CFURL, .readPermission, 0, &fileID) == noErr, let fileID else { return nil }
        defer { AudioFileClose(fileID) }
        var seconds: Double = 0
        var size = UInt32(MemoryLayout<Double>.size)
        guard AudioFileGetProperty(fileID, kAudioFilePropertyEstimatedDuration, &size, &seconds) == noErr, seconds > 0 else { return nil }
        return seconds
    }
}
