package com.clio.hearon.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val coverUrl: String,
    val duration: Long = 0L,
    val videoId: String = ""
)

class NPDownloader : Downloader() {
    override fun execute(request: Request): Response {
        var urlStr = request.url() ?: ""

        if (urlStr.isBlank()) {
            return Response(400, "Bad Request", emptyMap(), "", "")
        }

        if (urlStr.startsWith("//")) {
            urlStr = "https:$urlStr"
        } else if (urlStr.startsWith("/")) {
            urlStr = "https://www.youtube.com$urlStr"
        } else if (!urlStr.startsWith("http")) {
            urlStr = "https://$urlStr"
        }

        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection

        conn.requestMethod = request.httpMethod()
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.instanceFollowRedirects = true

        request.headers()?.forEach { (key, values) ->
            if (!values.isNullOrEmpty()) {
                conn.setRequestProperty(key, values[0])
            }
        }

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9")
        conn.setRequestProperty("Accept-Encoding", "identity")
        conn.setRequestProperty("Cookie", "SOCS=CAI; PREF=f4=4000000&hl=en&gl=US")

        val data = request.dataToSend()
        if (data != null) {
            conn.doOutput = true
            conn.outputStream.write(data)
        }

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val body = stream?.bufferedReader()?.readText() ?: ""

        return Response(code, conn.responseMessage ?: "", conn.headerFields, body, urlStr)
    }
}

object YtMusicApi {
    private const val TAG = "YtMusicApi"
    private const val UA_WEB = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    init {
        try {
            org.schabi.newpipe.extractor.NewPipe.init(
                NPDownloader(),
                org.schabi.newpipe.extractor.localization.Localization("US", "en")
            )
        } catch (e: Exception) {
        }
    }

    suspend fun search(query: String): List<MusicTrack> = withContext(Dispatchers.IO) {
        val list = mutableListOf<MusicTrack>()
        try {
            val url = URL("https://music.youtube.com/youtubei/v1/search?prettyPrint=false")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("User-Agent", UA_WEB)
            conn.setRequestProperty("Origin", "https://music.youtube.com")
            conn.setRequestProperty("Cookie", "SOCS=CAI; PREF=f4=4000000&hl=en&gl=US")
            conn.doOutput = true

            val payload = JSONObject().apply {
                put("context", JSONObject().apply {
                    put("client", JSONObject().apply {
                        put("clientName", "WEB_REMIX")
                        put("clientVersion", "1.20260315.01.00")
                        put("hl", "en")
                        put("gl", "US")
                    })
                })
                put("query", query)
                put("params", "EgWKAQIIAWoMEAMQBBAJEA4QChAF")
            }

            conn.outputStream.write(payload.toString().toByteArray())

            if (conn.responseCode == 200) {
                val response = JSONObject(conn.inputStream.bufferedReader().readText())
                val contents = response.optJSONObject("contents")
                    ?.optJSONObject("tabbedSearchResultsRenderer")
                    ?.optJSONArray("tabs")?.optJSONObject(0)
                    ?.optJSONObject("tabRenderer")
                    ?.optJSONObject("content")
                    ?.optJSONObject("sectionListRenderer")
                    ?.optJSONArray("contents")

                if (contents != null) {
                    for (i in 0 until contents.length()) {
                        val shelf = contents.optJSONObject(i)?.optJSONObject("musicShelfRenderer")
                        val items = shelf?.optJSONArray("contents") ?: continue

                        for (j in 0 until items.length()) {
                            val item = items.optJSONObject(j)?.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                            val flex = item.optJSONArray("flexColumns") ?: continue

                            val titleData = flex.optJSONObject(0)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
                                ?.optJSONObject("text")?.optJSONArray("runs")?.optJSONObject(0)

                            val title = titleData?.optString("text") ?: continue

                            val artist = flex.optJSONObject(1)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
                                ?.optJSONObject("text")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: "Desconocido"

                            val id = item.optJSONObject("playlistItemData")?.optString("videoId")
                                ?: titleData?.optJSONObject("navigationEndpoint")?.optJSONObject("watchEndpoint")?.optString("videoId")
                                ?: continue

                            val thumbnails = item.optJSONObject("thumbnail")?.optJSONObject("musicThumbnailRenderer")
                                ?.optJSONObject("thumbnail")?.optJSONArray("thumbnails")

                            var cover = ""
                            if (thumbnails != null && thumbnails.length() > 0) {
                                cover = thumbnails.optJSONObject(thumbnails.length() - 1).optString("url")
                                cover = cover.replace(Regex("=w\\d+-h\\d+.*"), "=w1080-h1080-l90-rj")
                            }

                            list.add(MusicTrack(id, title, artist, "", cover, 0L, id))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Search error: ${e.message}")
        }
        list
    }

    suspend fun getHomeContent(): List<MusicTrack> = withContext(Dispatchers.IO) {
        val results = search("top music 2026")
        if (results.isEmpty()) search("exitos musica") else results
    }

    suspend fun getStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            val streamInfo = org.schabi.newpipe.extractor.stream.StreamInfo.getInfo(
                org.schabi.newpipe.extractor.ServiceList.YouTube,
                "https://www.youtube.com/watch?v=$videoId"
            )

            val audioStreams = streamInfo.audioStreams
            if (audioStreams != null && audioStreams.isNotEmpty()) {
                var bestBitrate = -1
                var bestUrl: String? = null

                for (stream in audioStreams) {
                    if (stream.bitrate > bestBitrate) {
                        bestBitrate = stream.bitrate
                        bestUrl = stream.content ?: stream.url
                    }
                }

                if (!bestUrl.isNullOrEmpty()) {
                    return@withContext bestUrl
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "NewPipe Error: ${e.message}")
        }
        null
    }
}