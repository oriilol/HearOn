package com.clio.hearon

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections

/**
 * Saves tracks for offline playback. YouTube throttles or cuts long un-ranged
 * requests to its stream URLs, which left the old cache with empty or partial
 * files, so tracks are fetched in ranged chunks (like NewPipe/yt-dlp do).
 */
object Downloads {
    private const val CHUNK = 9L * 1024 * 1024
    const val MIN_VALID_SIZE = 50_000L
    private val inProgress = Collections.synchronizedSet(mutableSetOf<String>())

    fun dir(context: Context) = File(context.filesDir, "hearon_downloads").apply { mkdirs() }

    fun file(context: Context, id: String) = File(dir(context), "$id.m4a")

    fun isDownloaded(context: Context, id: String) = file(context, id).let { it.exists() && it.length() >= MIN_VALID_SIZE }

    fun isDownloading(id: String) = inProgress.contains(id)

    /** Downloads [track] if needed. Returns true when the file is complete. */
    suspend fun download(context: Context, track: YtTrack): Boolean = withContext(Dispatchers.IO) {
        if (isDownloaded(context, track.id)) return@withContext true
        if (!inProgress.add(track.id)) return@withContext false
        try {
            val url = com.clio.hearon.api.YtMusicApi.getStreamUrl(track.id) ?: return@withContext false
            fetch(url, file(context, track.id))
        } finally {
            inProgress.remove(track.id)
        }
    }

    private fun fetch(streamUrl: String, target: File): Boolean {
        val tmp = File(target.parentFile, target.name + ".part")
        try {
            var start = 0L
            var total: Long? = null
            tmp.outputStream().use { out ->
                while (total == null || start < total!!) {
                    val conn = URL(streamUrl).openConnection() as HttpURLConnection
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                    conn.setRequestProperty("Range", "bytes=$start-${start + CHUNK - 1}")
                    conn.connectTimeout = 15000
                    conn.readTimeout = 15000
                    if (conn.responseCode !in 200..299) break
                    val read = conn.inputStream.use { it.copyTo(out) }
                    if (read == 0L) break
                    start += read
                    val range = conn.getHeaderField("Content-Range")
                    total = range?.substringAfterLast('/')?.toLongOrNull() ?: break // server sent the whole file
                }
            }
            val complete = tmp.length() >= MIN_VALID_SIZE && (total == null || tmp.length() >= total!!)
            if (!complete) { tmp.delete(); return false }
            target.delete()
            return tmp.renameTo(target)
        } catch (e: Exception) {
            tmp.delete()
            return false
        }
    }
}
