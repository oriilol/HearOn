import Foundation

struct Track: Codable, Hashable, Identifiable, Sendable {
    let id: String
    var title: String
    var artist: String
    var coverUrl: String
}

struct Playlist: Codable, Hashable, Identifiable {
    var id = UUID()
    var name: String
    var tracks: [Track]
}

struct LyricLine: Identifiable {
    let id: Int
    let timeMs: Int
    let text: String

    var isInstrumental: Bool {
        text.trimmingCharacters(in: .whitespaces).isEmpty || text == "♪" || text.localizedCaseInsensitiveContains("instrumental")
    }

    /// Parses LRC lines such as `[01:23.45] text`.
    static func parse(_ raw: String) -> [LyricLine] {
        let regex = /\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)/
        var result: [LyricLine] = []
        for line in raw.split(separator: "\n", omittingEmptySubsequences: false) {
            guard let m = try? regex.firstMatch(in: String(line)) else { continue }
            let msStr = String(m.3)
            let ms = msStr.count == 2 ? Int(msStr)! * 10 : Int(msStr)!
            let time = Int(m.1)! * 60_000 + Int(m.2)! * 1000 + ms
            result.append(LyricLine(id: result.count, timeMs: time, text: String(m.4).trimmingCharacters(in: .whitespaces)))
        }
        return result
    }
}

enum RepeatMode: Int, Codable {
    case off, all, one

    var next: RepeatMode {
        switch self {
        case .off: .all
        case .all: .one
        case .one: .off
        }
    }
}

func formatTime(_ seconds: Double) -> String {
    guard seconds.isFinite, seconds > 0 else { return "00:00" }
    let s = Int(seconds)
    return String(format: "%02d:%02d", s / 60, s % 60)
}
