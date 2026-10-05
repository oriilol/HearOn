package com.clio.hearon

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * Library persistence as JSON. The old format (`putStringSet` with `|||` / `:::`
 * separators) lost the order of the lists and broke with those characters in
 * names, so it is migrated once on first read.
 */
object Storage {
    private fun YtTrack.toJson() = JSONObject().put("id", id).put("title", title).put("artist", artist).put("cover", coverUrl)

    private fun JSONObject.toTrack() = YtTrack(getString("id"), optString("title"), optString("artist"), optString("cover"))

    private fun tracksToJson(list: List<YtTrack>) = JSONArray().apply { list.forEach { put(it.toJson()) } }.toString()

    private fun tracksFromJson(raw: String): List<YtTrack> {
        val arr = JSONArray(raw)
        return (0 until arr.length()).map { arr.getJSONObject(it).toTrack() }
    }

    private fun legacyTrack(s: String): YtTrack? {
        val p = s.split("|||")
        return if (p.size == 4) YtTrack(p[0], p[1], p[2], p[3]) else null
    }

    /** Liked, recent and cached lists. `legacyKey` is the old string-set key. */
    fun loadTracks(prefs: SharedPreferences, key: String, legacyKey: String): List<YtTrack> {
        prefs.getString(key, null)?.let { raw -> return runCatching { tracksFromJson(raw) }.getOrDefault(emptyList()) }
        val legacy = prefs.getStringSet(legacyKey, null) ?: return emptyList()
        val migrated = legacy.mapNotNull(::legacyTrack)
        prefs.edit().putString(key, tracksToJson(migrated)).remove(legacyKey).apply()
        return migrated
    }

    fun saveTracks(prefs: SharedPreferences, key: String, list: List<YtTrack>) {
        prefs.edit().putString(key, tracksToJson(list)).apply()
    }

    fun loadPlaylists(prefs: SharedPreferences): List<Playlist> {
        prefs.getString("playlists_json", null)?.let { raw ->
            return runCatching {
                val arr = JSONArray(raw)
                (0 until arr.length()).map {
                    val o = arr.getJSONObject(it)
                    Playlist(o.getString("name"), tracksFromJson(o.getJSONArray("tracks").toString()))
                }
            }.getOrDefault(emptyList())
        }
        val legacy = prefs.getStringSet("playlists_data", null) ?: return emptyList()
        val migrated = legacy.mapNotNull {
            val parts = it.split(":::")
            if (parts.size == 2) Playlist(parts[0], parts[1].split(";;;").mapNotNull(::legacyTrack)) else null
        }
        savePlaylists(prefs, migrated)
        prefs.edit().remove("playlists_data").apply()
        return migrated
    }

    fun savePlaylists(prefs: SharedPreferences, list: List<Playlist>) {
        val arr = JSONArray()
        list.forEach { p -> arr.put(JSONObject().put("name", p.name).put("tracks", JSONArray(tracksToJson(p.tracks)))) }
        prefs.edit().putString("playlists_json", arr.toString()).apply()
    }
}

/**
 * Cover URL for a display size. YouTube Music covers (googleusercontent.com)
 * accept a `=wN-hN` suffix; video thumbnails (i.ytimg.com) don't, so those are
 * left as they are. Respects the custom cover and the data saver setting.
 */
fun coverFor(track: YtTrack, size: Int, prefs: SharedPreferences): String {
    prefs.getString("custom_cover_${track.id}", null)?.let { return it }
    val px = if (prefs.getBoolean("data_saver", false)) minOf(size, 226) else size
    return if (track.coverUrl.contains("googleusercontent.com")) {
        track.coverUrl.replace(Regex("=w\\d+-h\\d+.*$"), "=w$px-h$px-l90-rj")
    } else {
        track.coverUrl
    }
}
