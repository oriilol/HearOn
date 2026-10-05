import Foundation

/// Port of `HearonBackend` (Android). Talks to the YouTube Music internal API.
enum YTMusicClient {
    private static let clientVersion = "1.20260315.01.00"
    private static let userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    enum SearchType { case music, video }

    // MARK: - Requests

    private static func post(_ endpoint: String, body: [String: Any]) async throws -> [String: Any] {
        var req = URLRequest(url: URL(string: "https://music.youtube.com/youtubei/v1/\(endpoint)?prettyPrint=false")!)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.setValue(userAgent, forHTTPHeaderField: "User-Agent")
        req.setValue("https://music.youtube.com", forHTTPHeaderField: "Origin")
        req.setValue("SOCS=CAI", forHTTPHeaderField: "Cookie")
        req.timeoutInterval = 15

        let region = Locale.current.region?.identifier ?? "US"
        var payload = body
        payload["context"] = ["client": ["clientName": "WEB_REMIX", "clientVersion": clientVersion, "hl": "en", "gl": region]]
        req.httpBody = try JSONSerialization.data(withJSONObject: payload)

        let (data, resp) = try await URLSession.shared.data(for: req)
        guard (resp as? HTTPURLResponse)?.statusCode == 200 else { throw URLError(.badServerResponse) }
        return try JSONSerialization.jsonObject(with: data) as? [String: Any] ?? [:]
    }

    // MARK: - Public API

    static func search(_ query: String, type: SearchType = .music) async -> [Track] {
        let params = type == .music ? "EgWKAQIIAWoMEAMQBBAJEA4QChAF" : "EgWKAQIQAWoMEAMQBBAJEA4QChAF"
        guard let json = try? await post("search", body: ["query": query, "params": params]) else { return [] }
        return extractTracks(json)
    }

    /// Trending: the YouTube Music charts for the user's region. Falls back to a
    /// search for this year's hits (the Android version always searched
    /// "Éxitos Globales", which returns all-time hits — hence the old songs).
    static func trending() async -> [Track] {
        // The charts page links to playlists ("Trending 20 <country>",
        // "Daily Top Music Videos - <country>"); the songs are inside them.
        if let json = try? await post("browse", body: ["browseId": "FEmusic_charts"]) {
            var tracks: [Track] = []
            for id in chartPlaylistIds(json).prefix(2) {
                if let pl = try? await post("browse", body: ["browseId": id]) {
                    tracks += extractTracks(pl)
                }
            }
            tracks = dedupe(tracks)
            if tracks.count >= 10 { return tracks }
        }
        let year = Calendar.current.component(.year, from: .now)
        return await search("top hits \(year)")
    }

    /// Radio for a video (`RDAMVM` playlist), used for "up next", "for you" and mixes.
    static func upNext(_ videoId: String) async -> [Track] {
        guard let json = try? await post("next", body: ["videoId": videoId, "playlistId": "RDAMVM\(videoId)"]) else { return [] }
        var list: [Track] = []
        walk(json) { key, value in
            guard key == "playlistPanelVideoRenderer", let r = value as? [String: Any],
                  let id = r["videoId"] as? String, !id.isEmpty,
                  let title = runs(r["title"]).first else { return }
            let artist = runs(r["longBylineText"]).first ?? "Unknown"
            let thumbs = (r["thumbnail"] as? [String: Any])?["thumbnails"] as? [[String: Any]]
            list.append(Track(id: id, title: title, artist: artist, coverUrl: bestThumb(thumbs)))
        }
        return dedupe(list)
    }

    static func lyrics(title: String, artist: String) async -> (synced: String?, plain: String?) {
        var t = title.replacing(/\(.*\)|\[.*\]/, with: "").trimmingCharacters(in: .whitespaces)
        // Video titles often look like "Artist - Song"; keep only the song.
        if let dash = t.range(of: " - ") { t = String(t[dash.upperBound...]).trimmingCharacters(in: .whitespaces) }
        let a = artist.replacing(/,.*|&.*/, with: "").trimmingCharacters(in: .whitespaces)
        var comps = URLComponents(string: "https://lrclib.net/api/get")!
        comps.queryItems = [URLQueryItem(name: "track_name", value: t), URLQueryItem(name: "artist_name", value: a)]
        var req = URLRequest(url: comps.url!)
        req.setValue("HearonApp/1.0", forHTTPHeaderField: "User-Agent")
        guard let (data, resp) = try? await URLSession.shared.data(for: req),
              (resp as? HTTPURLResponse)?.statusCode == 200,
              let j = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else { return (nil, nil) }
        let synced = (j["syncedLyrics"] as? String).flatMap { $0.isEmpty ? nil : $0 }
        let plain = (j["plainLyrics"] as? String).flatMap { $0.isEmpty ? nil : $0 }
        return (synced, plain)
    }

    static func latestVersion() async -> String? {
        var req = URLRequest(url: URL(string: "https://api.github.com/repos/oriilol/hearon/releases/latest")!)
        req.setValue("HearonApp", forHTTPHeaderField: "User-Agent")
        guard let (data, _) = try? await URLSession.shared.data(for: req),
              let j = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
              let tag = j["tag_name"] as? String else { return nil }
        return tag.replacingOccurrences(of: "v", with: "")
    }

    // MARK: - Parsing

    /// Finds every song in a response, whatever shelf/carousel it sits in.
    private static func extractTracks(_ json: [String: Any]) -> [Track] {
        var list: [Track] = []
        walk(json) { key, value in
            guard let r = value as? [String: Any] else { return }
            switch key {
            case "musicResponsiveListItemRenderer":
                let flex = r["flexColumns"] as? [[String: Any]] ?? []
                func col(_ i: Int) -> [String: Any]? {
                    guard i < flex.count else { return nil }
                    return (flex[i]["musicResponsiveListItemFlexColumnRenderer"] as? [String: Any])?["text"] as? [String: Any]
                }
                guard let titleRun = (col(0)?["runs"] as? [[String: Any]])?.first,
                      let title = titleRun["text"] as? String else { return }
                let id = ((r["playlistItemData"] as? [String: Any])?["videoId"] as? String)
                    ?? (((titleRun["navigationEndpoint"] as? [String: Any])?["watchEndpoint"] as? [String: Any])?["videoId"] as? String)
                guard let id else { return }
                let artist = runs(col(1)).first ?? "Unknown"
                let thumbs = (((r["thumbnail"] as? [String: Any])?["musicThumbnailRenderer"] as? [String: Any])?["thumbnail"] as? [String: Any])?["thumbnails"] as? [[String: Any]]
                list.append(Track(id: id, title: title, artist: artist, coverUrl: bestThumb(thumbs)))

            case "musicTwoRowItemRenderer":
                guard let id = (((r["navigationEndpoint"] as? [String: Any])?["watchEndpoint"] as? [String: Any])?["videoId"] as? String),
                      let title = runs(r["title"]).first else { return }
                let artist = runs(r["subtitle"]).first { $0 != " • " && $0 != "Song" && $0 != "Single" } ?? "Unknown"
                let thumbs = (((r["thumbnailRenderer"] as? [String: Any])?["musicThumbnailRenderer"] as? [String: Any])?["thumbnail"] as? [String: Any])?["thumbnails"] as? [[String: Any]]
                list.append(Track(id: id, title: title, artist: artist, coverUrl: bestThumb(thumbs)))

            default: break
            }
        }
        return dedupe(list)
    }

    /// Playlist browse IDs on the charts page, "Trending" first.
    private static func chartPlaylistIds(_ json: [String: Any]) -> [String] {
        var found: [(title: String, id: String)] = []
        walk(json) { key, value in
            guard key == "musicTwoRowItemRenderer", let r = value as? [String: Any],
                  let id = ((r["navigationEndpoint"] as? [String: Any])?["browseEndpoint"] as? [String: Any])?["browseId"] as? String,
                  id.hasPrefix("VL") else { return }
            found.append((runs(r["title"]).joined(), id))
        }
        return found.sorted { $0.title.contains("Trending") && !$1.title.contains("Trending") }.map(\.id)
    }

    private static func runs(_ obj: Any?) -> [String] {
        ((obj as? [String: Any])?["runs"] as? [[String: Any]])?.compactMap { $0["text"] as? String } ?? []
    }

    private static func bestThumb(_ thumbs: [[String: Any]]?) -> String {
        (thumbs?.last?["url"] as? String) ?? ""
    }

    private static func dedupe(_ list: [Track]) -> [Track] {
        var seen = Set<String>()
        return list.filter { seen.insert($0.id).inserted }
    }

    /// Depth-first walk over a decoded JSON tree, in document order.
    private static func walk(_ node: Any, _ visit: (String, Any) -> Void) {
        if let dict = node as? [String: Any] {
            for (k, v) in dict.sorted(by: { $0.key < $1.key }) {
                visit(k, v)
                walk(v, visit)
            }
        } else if let arr = node as? [Any] {
            for v in arr { walk(v, visit) }
        }
    }
}
