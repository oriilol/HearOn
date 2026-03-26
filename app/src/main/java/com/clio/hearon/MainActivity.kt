@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.media3.common.util.UnstableApi::class)

package com.clio.hearon

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

val PixelFont = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.Setting("ROND", 100f)
        )
    )
)

val APP_VERSION = "1.0.0"

fun formatTime(ms: Long): String {
    val s = ms / 1000
    return "%02d:%02d".format(s / 60, s % 60)
}

fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
    return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

suspend fun getDominantColor(url: String): Color? = withContext(Dispatchers.IO) {
    try {
        val stream = URL(url).openStream()
        val bitmap = BitmapFactory.decodeStream(stream)
        val scaled = android.graphics.Bitmap.createScaledBitmap(bitmap, 1, 1, true)
        val colorInt = scaled.getPixel(0, 0)
        scaled.recycle()
        bitmap.recycle()
        val hsl = FloatArray(3)
        androidx.core.graphics.ColorUtils.colorToHSL(colorInt, hsl)
        if (hsl[2] < 0.25f) hsl[2] = 0.25f
        if (hsl[2] > 0.75f) hsl[2] = 0.75f
        Color(androidx.core.graphics.ColorUtils.HSLToColor(hsl))
    } catch (e: Exception) { null }
}

@Composable
fun OfflineScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.CloudOff,
            null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Sin conexión a internet",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Ve a tu Biblioteca para escuchar la música que tienes en caché.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            org.schabi.newpipe.extractor.NewPipe.init(com.clio.hearon.api.NPDownloader())
        } catch (e: Exception) {}

        setContent {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)

            var themeMode by remember { mutableStateOf(prefs.getString("theme", "Sistema") ?: "Sistema") }
            var isDynamicColorEnabled by remember { mutableStateOf(prefs.getBoolean("dynamic_colors", true)) }
            var dominantColor by remember { mutableStateOf<Color?>(null) }

            val isDark = when (themeMode) {
                "Oscuro" -> true
                "Claro" -> false
                else -> isSystemInDarkTheme()
            }

            val baseColorScheme = if (isDark) darkColorScheme() else lightColorScheme()
            val defaultText = if (isDark) Color.White else Color.Black
            val defaultTextVariant = if (isDark) Color.LightGray else Color.DarkGray

            val animPrimary by animateColorAsState(
                targetValue = if (isDynamicColorEnabled && dominantColor != null) dominantColor!! else baseColorScheme.primary,
                animationSpec = tween(1000),
                label = ""
            )
            val animPrimaryContainer by animateColorAsState(
                targetValue = if (isDynamicColorEnabled && dominantColor != null) dominantColor!!.copy(alpha = if (isDark) 0.4f else 0.3f) else baseColorScheme.primaryContainer,
                animationSpec = tween(1000),
                label = ""
            )
            val animSecondaryContainer by animateColorAsState(
                targetValue = if (isDynamicColorEnabled && dominantColor != null) dominantColor!!.copy(alpha = if (isDark) 0.2f else 0.15f) else baseColorScheme.secondaryContainer,
                animationSpec = tween(1000),
                label = ""
            )
            val animSurface by animateColorAsState(
                targetValue = if (isDynamicColorEnabled && dominantColor != null) Color(androidx.core.graphics.ColorUtils.blendARGB(baseColorScheme.surface.toArgb(), dominantColor!!.toArgb(), if (isDark) 0.08f else 0.1f)) else baseColorScheme.surface,
                animationSpec = tween(1000),
                label = ""
            )
            val animSurfaceHigh by animateColorAsState(
                targetValue = if (isDynamicColorEnabled && dominantColor != null) Color(androidx.core.graphics.ColorUtils.blendARGB(baseColorScheme.surfaceContainerHigh.toArgb(), dominantColor!!.toArgb(), if (isDark) 0.15f else 0.2f)) else baseColorScheme.surfaceContainerHigh,
                animationSpec = tween(1000),
                label = ""
            )

            val customColorScheme = baseColorScheme.copy(
                primary = animPrimary,
                primaryContainer = animPrimaryContainer,
                secondaryContainer = animSecondaryContainer,
                surface = animSurface,
                surfaceContainerHigh = animSurfaceHigh,
                surfaceContainer = animSurfaceHigh.copy(alpha = 0.5f),
                surfaceContainerHighest = animSurfaceHigh.copy(alpha = 0.8f),
                onPrimaryContainer = defaultText,
                onSecondaryContainer = defaultText,
                onSurface = defaultText,
                onSurfaceVariant = defaultTextVariant
            )

            val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            MaterialTheme(
                colorScheme = customColorScheme,
                typography = Typography(
                    displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = PixelFont),
                    displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = PixelFont),
                    displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = PixelFont),
                    headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = PixelFont),
                    headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = PixelFont),
                    headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = PixelFont),
                    titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = PixelFont),
                    titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = PixelFont),
                    titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = PixelFont),
                    bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = PixelFont),
                    bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = PixelFont),
                    bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = PixelFont),
                    labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = PixelFont),
                    labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = PixelFont),
                    labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = PixelFont)
                )
            ) {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                    HearonApp(
                        currentTheme = themeMode,
                        onThemeChange = {
                            themeMode = it
                            prefs.edit().putString("theme", it).apply()
                        },
                        isDynamicColorEnabled = isDynamicColorEnabled,
                        onDynamicColorToggle = {
                            isDynamicColorEnabled = it
                            prefs.edit().putBoolean("dynamic_colors", it).apply()
                        },
                        onColorExtracted = { dominantColor = it }
                    )
                }
            }
        }
    }
}

data class YtTrack(val id: String, val title: String, val artist: String, val coverUrl: String)
data class LyricLine(val timeMs: Long, val text: String)
data class Playlist(val name: String, val tracks: List<YtTrack>)
data class TrackMenuData(val track: YtTrack, val playlistIndex: Int? = null, val playlistTrackIndex: Int? = null)

@Composable
fun AppleMusicDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(400), repeatMode = RepeatMode.Reverse),
        label = "d1"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(400, delayMillis = 150), repeatMode = RepeatMode.Reverse),
        label = "d2"
    )
    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(400, delayMillis = 300), repeatMode = RepeatMode.Reverse),
        label = "d3"
    )

    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha1)))
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha2)))
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha3)))
    }
}

@Composable
fun ExpressiveChip(
    selected: Boolean,
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        label = ""
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = ""
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        label = ""
    )

    Surface(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = bgColor,
        tonalElevation = if (selected) 8.dp else 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = contentColor)
            }
            Text(text, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = contentColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HearonApp(
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    isDynamicColorEnabled: Boolean,
    onDynamicColorToggle: (Boolean) -> Unit,
    onColorExtracted: (Color?) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val prefs = context.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    val scope = rememberCoroutineScope()

    var isOnline by remember { mutableStateOf(isOnline(context)) }

    LaunchedEffect(Unit) {
        while(true) {
            isOnline = isOnline(context)
            delay(3000)
        }
    }

    var crossfadeEnabled by remember { mutableStateOf(prefs.getBoolean("crossfade_enabled", false)) }
    var crossfadeDur by remember { mutableFloatStateOf(prefs.getFloat("crossfade_dur", 1f)) }

    var selectedTab by remember { mutableIntStateOf(0) }
    var isFullScreen by remember { mutableStateOf(false) }
    var fullScreenState by remember { mutableStateOf("PLAYER") }
    var tracks by remember { mutableStateOf<List<YtTrack>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var forYouTracks by remember { mutableStateOf<List<YtTrack>>(emptyList()) }
    var isLoadingForYou by remember { mutableStateOf(false) }

    var currentTrack by remember { mutableStateOf<YtTrack?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var pos by remember { mutableLongStateOf(0L) }
    var dur by remember { mutableLongStateOf(0L) }
    var player by remember { mutableStateOf<Player?>(null) }

    var originalQueue by remember { mutableStateOf<List<YtTrack>>(emptyList()) }
    var playQueue by remember { mutableStateOf<List<YtTrack>>(emptyList()) }
    var currentQueueIndex by remember { mutableIntStateOf(-1) }
    var isShuffle by remember { mutableStateOf(false) }
    var repeatMode by remember { mutableIntStateOf(Player.REPEAT_MODE_OFF) }

    var coverUpdateTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentTrack?.coverUrl) {
        if (currentTrack != null) {
            val extracted = getDominantColor(currentTrack!!.coverUrl)
            onColorExtracted(extracted)
        } else {
            onColorExtracted(null)
        }
    }

    val likedTracksRaw = prefs.getStringSet("liked_tracks_data", emptySet()) ?: emptySet()
    val likedTracks = remember {
        mutableStateListOf<YtTrack>().apply {
            addAll(likedTracksRaw.mapNotNull {
                val parts = it.split("|||")
                if (parts.size == 4) YtTrack(parts[0], parts[1], parts[2], parts[3]) else null
            })
        }
    }

    val recentlyPlayedRaw = prefs.getStringSet("recent_tracks_data", emptySet()) ?: emptySet()
    val recentlyPlayed = remember {
        mutableStateListOf<YtTrack>().apply {
            addAll(recentlyPlayedRaw.mapNotNull {
                val parts = it.split("|||")
                if (parts.size == 4) YtTrack(parts[0], parts[1], parts[2], parts[3]) else null
            })
        }
    }

    LaunchedEffect(recentlyPlayed.firstOrNull()?.id) {
        val lastId = recentlyPlayed.firstOrNull()?.id
        if (lastId != null && isOnline) {
            isLoadingForYou = true
            forYouTracks = HearonBackend.getUpNext(lastId).filter { it.id != lastId }
            isLoadingForYou = false
        }
    }

    val downloadedTracks = remember { mutableStateListOf<YtTrack>() }
    var cachedFilesCount by remember { mutableIntStateOf(0) }
    var cacheSizeMB by remember { mutableIntStateOf(0) }

    fun refreshCacheCount() {
        val dir = File(context.filesDir, "hearon_downloads")
        if (dir.exists()) {
            val files = dir.listFiles()?.filter { it.name.endsWith(".m4a") } ?: emptyList()
            cachedFilesCount = files.size
            cacheSizeMB = (files.sumOf { it.length() } / (1024 * 1024)).toInt()
        } else {
            cachedFilesCount = 0
            cacheSizeMB = 0
        }
    }

    fun saveRecentTrack(t: YtTrack) {
        recentlyPlayed.removeAll { it.id == t.id }
        recentlyPlayed.add(0, t)
        if (recentlyPlayed.size > 50) {
            recentlyPlayed.removeAt(recentlyPlayed.size - 1)
        }
        val data = recentlyPlayed.map { "${it.id}|||${it.title}|||${it.artist}|||${it.coverUrl}" }.toSet()
        prefs.edit().putStringSet("recent_tracks_data", data).apply()
    }

    val onRefresh: () -> Unit = {
        val downloadDir = File(context.filesDir, "hearon_downloads")
        if (!downloadDir.exists()) downloadDir.mkdirs()

        val raw = prefs.getStringSet("cached_tracks_data", emptySet()) ?: emptySet()
        val verified = raw.mapNotNull {
            val parts = it.split("|||")
            if (parts.size == 4) {
                val file = File(downloadDir, "${parts[0]}.m4a")
                if (file.exists() && file.length() >= 50_000) YtTrack(parts[0], parts[1], parts[2], parts[3]) else null
            } else null
        }

        downloadedTracks.clear()
        downloadedTracks.addAll(verified)

        val data = verified.map { "${it.id}|||${it.title}|||${it.artist}|||${it.coverUrl}" }.toSet()
        prefs.edit().putStringSet("cached_tracks_data", data).apply()

        refreshCacheCount()
    }

    LaunchedEffect(Unit) {
        onRefresh()
    }

    val playlistsRaw = prefs.getStringSet("playlists_data", emptySet()) ?: emptySet()
    val playlists = remember {
        mutableStateListOf<Playlist>().apply {
            addAll(playlistsRaw.mapNotNull {
                val parts = it.split(":::")
                if (parts.size == 2) {
                    val pTracks = parts[1].split(";;;").mapNotNull { tp ->
                        val p = tp.split("|||")
                        if (p.size == 4) YtTrack(p[0], p[1], p[2], p[3]) else null
                    }
                    Playlist(parts[0], pTracks)
                } else null
            })
        }
    }

    var trackOptionsMenu by remember { mutableStateOf<TrackMenuData?>(null) }
    var trackToAdd by remember { mutableStateOf<YtTrack?>(null) }
    var showCacheWarning by remember { mutableStateOf(false) }
    var showTrackInfo by remember { mutableStateOf<YtTrack?>(null) }

    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            trackOptionsMenu?.track?.id?.let { id ->
                val file = File(context.filesDir, "cover_$id.jpg")
                context.contentResolver.openInputStream(it)?.use { input ->
                    file.outputStream().use { out -> input.copyTo(out) }
                }
                prefs.edit().putString("custom_cover_$id", Uri.fromFile(file).toString()).apply()
                coverUpdateTrigger++
                Toast.makeText(context, "Portada guardada.", Toast.LENGTH_SHORT).show()
            }
        }
        trackOptionsMenu = null
    }

    fun savePlaylists() {
        val data = playlists.map { p ->
            "${p.name}:::${p.tracks.joinToString(";;;") { t -> "${t.id}|||${t.title}|||${t.artist}|||${t.coverUrl}" }}"
        }.toSet()
        prefs.edit().putStringSet("playlists_data", data).apply()
    }

    fun saveLikedTracks() {
        val data = likedTracks.map { it ->
            "${it.id}|||${it.title}|||${it.artist}|||${it.coverUrl}"
        }.toSet()
        prefs.edit().putStringSet("liked_tracks_data", data).apply()
    }

    LaunchedEffect(currentQueueIndex, playQueue.size, repeatMode) {
        val hasNext = currentQueueIndex < playQueue.size - 1 || repeatMode == Player.REPEAT_MODE_ALL
        val hasPrev = currentQueueIndex > 0
        context.sendBroadcast(Intent("com.clio.hearon.UPDATE_QUEUE").apply {
            setPackage(context.packageName)
            putExtra("hasNext", hasNext)
            putExtra("hasPrev", hasPrev)
        })
    }

    LaunchedEffect(activity?.intent) {
        if (activity?.intent?.getBooleanExtra("OPEN_PLAYER", false) == true) {
            isFullScreen = true
            fullScreenState = "PLAYER"
            activity.intent?.removeExtra("OPEN_PLAYER")
        }
    }

    DisposableEffect(activity) {
        val listener = androidx.core.util.Consumer<Intent> { intent ->
            if (intent.getBooleanExtra("OPEN_PLAYER", false)) {
                isFullScreen = true
                fullScreenState = "PLAYER"
                intent.removeExtra("OPEN_PLAYER")
            }
        }
        activity?.addOnNewIntentListener(listener)
        onDispose {
            activity?.removeOnNewIntentListener(listener)
        }
    }

    val prefetchNextTrack: (Int) -> Unit = { idx ->
        val nextIdx = if (idx < playQueue.size - 1) idx + 1 else if (repeatMode == Player.REPEAT_MODE_ALL) 0 else -1
        if (nextIdx != -1) {
            val nextTrack = playQueue[nextIdx]
            scope.launch(Dispatchers.IO) {
                val file = File(context.filesDir, "hearon_downloads/${nextTrack.id}.m4a")
                val isLocal = file.exists() && file.length() >= 50_000

                if (!isLocal && isOnline(context)) {
                    try {
                        val streamUrl = com.clio.hearon.api.YtMusicApi.getStreamUrl(nextTrack.id)
                        if (streamUrl != null) {
                            val dDir = File(context.filesDir, "hearon_downloads")
                            if (!dDir.exists()) dDir.mkdirs()

                            val conn = URL(streamUrl).openConnection() as HttpURLConnection
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                            conn.connect()

                            if (conn.responseCode in 200..299) {
                                val tmpFile = File(dDir, "${nextTrack.id}.tmp")
                                tmpFile.outputStream().use { out -> conn.inputStream.use { it.copyTo(out) } }

                                if (tmpFile.exists() && tmpFile.length() >= 50_000) {
                                    tmpFile.renameTo(file)
                                } else {
                                    tmpFile.delete()
                                }
                            }
                        }
                    } catch (e: Exception) { }
                }
            }
        }
    }

    val handleTrackSelect = { track: YtTrack ->
        val isLocal = File(context.filesDir, "hearon_downloads/${track.id}.m4a").let { it.exists() && it.length() >= 50_000 }
        if (!isOnline && !isLocal) {
            Toast.makeText(context, "No hay conexión para reproducir esta pista.", Toast.LENGTH_SHORT).show()
        } else {
            currentTrack = track
            scope.launch {
                playTrack(track, player, context, prefs)
                saveRecentTrack(track)
                if (isOnline) {
                    val related = HearonBackend.getUpNext(track.id).filter { it.id != track.id }
                    originalQueue = listOf(track) + related
                } else {
                    originalQueue = downloadedTracks
                }
                playQueue = if (isShuffle) listOf(track) + originalQueue.filter { it.id != track.id }.shuffled() else originalQueue
                currentQueueIndex = 0
                prefetchNextTrack(currentQueueIndex)
                refreshCacheCount()
            }
        }
    }

    fun playNext() {
        if (playQueue.isNotEmpty() && currentQueueIndex < playQueue.size - 1) {
            currentQueueIndex++
            currentTrack = playQueue[currentQueueIndex]
            scope.launch {
                playTrack(currentTrack!!, player, context, prefs)
                saveRecentTrack(currentTrack!!)
                prefetchNextTrack(currentQueueIndex)
                onRefresh()
            }
        } else if (repeatMode == Player.REPEAT_MODE_ALL && playQueue.isNotEmpty()) {
            currentQueueIndex = 0
            currentTrack = playQueue[currentQueueIndex]
            scope.launch {
                playTrack(currentTrack!!, player, context, prefs)
                saveRecentTrack(currentTrack!!)
                prefetchNextTrack(currentQueueIndex)
                onRefresh()
            }
        }
    }

    fun playPrev() {
        if (pos > 3000) {
            player?.seekTo(0)
            return
        }
        if (currentQueueIndex > 0) {
            currentQueueIndex--
            currentTrack = playQueue[currentQueueIndex]
            scope.launch {
                playTrack(currentTrack!!, player, context, prefs)
                saveRecentTrack(currentTrack!!)
                prefetchNextTrack(currentQueueIndex)
                onRefresh()
            }
        }
    }

    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    "com.clio.hearon.NEXT_TRACK" -> playNext()
                    "com.clio.hearon.PREV_TRACK" -> playPrev()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        ContextCompat.registerReceiver(context, receiver, IntentFilter().apply {
            addAction("com.clio.hearon.NEXT_TRACK")
            addAction("com.clio.hearon.PREV_TRACK")
        }, ContextCompat.RECEIVER_NOT_EXPORTED)

        if (isOnline) {
            tracks = HearonBackend.search("Éxitos Globales")
        }
        isLoading = false

        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, token).buildAsync()
        controllerFuture.addListener({
            player = controllerFuture.get()
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                        mediaItem?.mediaId?.let { id ->
                            val newIdx = playQueue.indexOfFirst { it.id == id }
                            if (newIdx != -1) {
                                currentQueueIndex = newIdx
                                currentTrack = playQueue[newIdx]
                                saveRecentTrack(currentTrack!!)
                                prefetchNextTrack(newIdx)
                                onRefresh()
                            }
                        }
                    }
                }
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        if (repeatMode == Player.REPEAT_MODE_ONE) {
                            player?.seekTo(0)
                            player?.play()
                        } else {
                            playNext()
                        }
                    }
                }
            })
            player?.repeatMode = repeatMode
        }, ContextCompat.getMainExecutor(context))
    }

    LaunchedEffect(repeatMode) { player?.repeatMode = repeatMode }

    LaunchedEffect(isPlaying, crossfadeEnabled, crossfadeDur) {
        while (isPlaying) {
            pos = player?.currentPosition?.coerceAtLeast(0L) ?: 0L
            dur = player?.duration?.coerceAtLeast(0L) ?: 0L

            if (crossfadeEnabled && crossfadeDur > 0f && dur > 0) {
                val remainingMs = dur - pos
                val fadeMs = (crossfadeDur * 1000).toLong()

                if (remainingMs <= fadeMs) {
                    player?.volume = (remainingMs.toFloat() / fadeMs.toFloat()).coerceIn(0f, 1f)
                    if (remainingMs <= 200L && repeatMode != Player.REPEAT_MODE_ONE) {
                        playNext()
                    }
                } else if (pos <= fadeMs) {
                    player?.volume = (pos.toFloat() / fadeMs.toFloat()).coerceIn(0f, 1f)
                } else {
                    player?.volume = 1f
                }
            } else {
                player?.volume = 1f
            }
            delay(50)
        }
    }

    if (trackOptionsMenu != null) {
        val menuData = trackOptionsMenu!!
        val t = menuData.track
        val isLiked = likedTracks.any { it.id == t.id }

        ModalBottomSheet(
            onDismissRequest = { trackOptionsMenu = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val customCover = remember(t.id, coverUpdateTrigger) { prefs.getString("custom_cover_${t.id}", t.coverUrl.replace("w1080-h1080", "w226-h226")) }
                    AsyncImage(
                        model = customCover,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = t.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = t.artist,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(if (isLiked) "Quitar de favoritos" else "Añadir a favoritos") },
                    leadingContent = {
                        Icon(
                            if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            null,
                            tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.clickable {
                        if (isLiked) likedTracks.removeAll { it.id == t.id } else likedTracks.add(t)
                        saveLikedTracks()
                        trackOptionsMenu = null
                    }
                )

                if (menuData.playlistIndex != null && menuData.playlistTrackIndex != null) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text("Quitar de esta playlist") },
                        leadingContent = { Icon(Icons.Default.RemoveCircleOutline, null) },
                        modifier = Modifier.clickable {
                            val p = playlists[menuData.playlistIndex]
                            val newList = p.tracks.toMutableList().apply { removeAt(menuData.playlistTrackIndex) }
                            playlists[menuData.playlistIndex] = p.copy(tracks = newList)
                            savePlaylists()
                            trackOptionsMenu = null
                        }
                    )
                }

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Añadir a playlist") },
                    leadingContent = { Icon(Icons.Default.PlaylistAdd, null) },
                    modifier = Modifier.clickable {
                        trackToAdd = t
                        trackOptionsMenu = null
                    }
                )

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Cambiar portada") },
                    leadingContent = { Icon(Icons.Default.ImageSearch, null) },
                    modifier = Modifier.clickable {
                        coverPicker.launch("image/*")
                    }
                )

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Información de la pista") },
                    leadingContent = { Icon(Icons.Default.Info, null) },
                    modifier = Modifier.clickable {
                        showTrackInfo = t
                        trackOptionsMenu = null
                    }
                )
            }
        }
    }

    if (showTrackInfo != null) {
        val t = showTrackInfo!!
        val isLocal = File(context.filesDir, "hearon_downloads/${t.id}.m4a").let { it.exists() && it.length() >= 50_000 }
        val quality = prefs.getString("audio_quality", "Alta")

        AlertDialog(
            onDismissRequest = { showTrackInfo = null },
            title = { Text("Info de pista", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text("Título: ${t.title}", style = MaterialTheme.typography.bodyMedium)
                    Text("Artista: ${t.artist}", style = MaterialTheme.typography.bodyMedium)
                    Text("ID YouTube: ${t.id}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Origen: ${if (isLocal) "Caché Local (Sin conexión)" else "Streaming"}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (!isLocal) {
                        Text("Calidad de red: $quality", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showTrackInfo = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (trackToAdd != null) {
        ModalBottomSheet(
            onDismissRequest = { trackToAdd = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(
                    text = "Añadir a Playlist",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
                if (playlists.isEmpty()) {
                    Text(
                        text = "No tienes ninguna playlist. Ve a tu Biblioteca para crear una.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(playlists) { index, p ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                onClick = {
                                    val updatedTracks = p.tracks + trackToAdd!!
                                    playlists[index] = p.copy(tracks = updatedTracks)
                                    savePlaylists()
                                    trackToAdd = null
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val cover = p.tracks.firstOrNull()?.coverUrl
                                    if (cover != null) {
                                        AsyncImage(
                                            model = cover,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.QueueMusic, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = p.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCacheWarning) {
        AlertDialog(
            onDismissRequest = { showCacheWarning = false },
            title = { Text("Vaciar caché", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    "Las pistas de audio guardadas se borrarán y volverán a gastar datos al reproducirse. ¿Continuar?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cacheDir = java.io.File(context.filesDir, "hearon_downloads")
                        cacheDir.deleteRecursively()
                        downloadedTracks.clear()
                        prefs.edit().putStringSet("cached_tracks_data", emptySet()).apply()
                        refreshCacheCount()
                        showCacheWarning = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCacheWarning = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(
                targetState = selectedTab,
                label = "MainCrossfade"
            ) { tab ->
                when (tab) {
                    0 -> {
                        HomeScreen(
                            tracks = tracks,
                            forYouTracks = forYouTracks,
                            recentTracks = recentlyPlayed,
                            loading = isLoading && isOnline,
                            loadingForYou = isLoadingForYou && isOnline,
                            currentId = currentTrack?.id,
                            coverUpdateTrigger = coverUpdateTrigger,
                            onSelect = handleTrackSelect as (YtTrack) -> Unit,
                            onOptionsClick = {
                                trackOptionsMenu = TrackMenuData(it)
                                Unit
                            },
                            onRefresh = {
                                scope.launch {
                                    isLoading = true
                                    tracks = HearonBackend.search("Éxitos Globales")
                                    isLoading = false

                                    val lastId = recentlyPlayed.firstOrNull()?.id
                                    if (lastId != null && isOnline) {
                                        isLoadingForYou = true
                                        forYouTracks = HearonBackend.getUpNext(lastId).filter { it.id != lastId }
                                        isLoadingForYou = false
                                    }
                                }
                            }
                        )
                    }
                    1 -> {
                        SearchScreen(
                            currentId = currentTrack?.id,
                            coverUpdateTrigger = coverUpdateTrigger,
                            onSelect = handleTrackSelect as (YtTrack) -> Unit,
                            onOptionsClick = {
                                trackOptionsMenu = TrackMenuData(it)
                                Unit
                            }
                        )
                    }
                    2 -> {
                        LibraryScreen(
                            likedTracks = likedTracks,
                            playlists = playlists,
                            downloadedTracks = downloadedTracks,
                            currentId = currentTrack?.id,
                            coverUpdateTrigger = coverUpdateTrigger,
                            onCreatePlaylist = { name ->
                                playlists.add(Playlist(name, emptyList()))
                                savePlaylists()
                            },
                            onDeletePlaylist = { pIndex ->
                                playlists.removeAt(pIndex)
                                savePlaylists()
                            },
                            onRenamePlaylist = { pIndex, newName ->
                                val p = playlists[pIndex]
                                playlists[pIndex] = p.copy(name = newName)
                                savePlaylists()
                            },
                            onSelectTrack = { track, queue, index ->
                                val isLocal = File(context.filesDir, "hearon_downloads/${track.id}.m4a").let { it.exists() && it.length() >= 50_000 }
                                if (!isOnline && !isLocal) {
                                    Toast.makeText(context, "No hay conexión para reproducir esta pista.", Toast.LENGTH_SHORT).show()
                                } else {
                                    currentTrack = track
                                    originalQueue = queue
                                    playQueue = if (isShuffle) listOf(track) + queue.filter { it.id != track.id }.shuffled() else queue
                                    currentQueueIndex = playQueue.indexOf(track)
                                    scope.launch {
                                        playTrack(track, player, context, prefs)
                                        saveRecentTrack(track)
                                        prefetchNextTrack(currentQueueIndex)
                                        refreshCacheCount()
                                    }
                                }
                            },
                            onOptionsClick = { track, pIdx, tIdx ->
                                trackOptionsMenu = TrackMenuData(track, pIdx, tIdx)
                            },
                            onClearCache = { showCacheWarning = true },
                            onRefresh = onRefresh
                        )
                    }
                    3 -> {
                        SettingsScreen(
                            currentTheme = currentTheme,
                            onThemeChange = onThemeChange,
                            prefs = prefs,
                            context = context,
                            cachedCount = cachedFilesCount,
                            cacheSizeMB = cacheSizeMB,
                            isDynamicColorEnabled = isDynamicColorEnabled,
                            onDynamicColorToggle = onDynamicColorToggle,
                            onClearCache = { showCacheWarning = true }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = currentTrack != null && !isFullScreen,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ) + fadeOut()
            ) {
                ExpressiveMiniPlayer(
                    track = currentTrack!!,
                    isPlaying = isPlaying,
                    coverUpdateTrigger = coverUpdateTrigger,
                    onPlayPause = { if (isPlaying) player?.pause() else player?.play() },
                    onNext = { playNext() },
                    onExpand = {
                        isFullScreen = true
                        fullScreenState = "PLAYER"
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(visible = !isFullScreen) {
                FloatingNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onTabReselected = { tabIndex ->
                        scope.launch {
                            when (tabIndex) {
                                0 -> {
                                    isLoading = true
                                    tracks = HearonBackend.search("Éxitos Globales")
                                    isLoading = false

                                    val lastId = recentlyPlayed.firstOrNull()?.id
                                    if (lastId != null && isOnline) {
                                        isLoadingForYou = true
                                        forYouTracks = HearonBackend.getUpNext(lastId).filter { it.id != lastId }
                                        isLoadingForYou = false
                                    }
                                }
                                2 -> {
                                    onRefresh()
                                }
                            }
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = isFullScreen,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(400)
            ) + fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                AnimatedContent(
                    targetState = fullScreenState,
                    transitionSpec = {
                        fadeIn(tween(300)) + scaleIn(initialScale = 0.95f) togetherWith fadeOut(tween(300)) + scaleOut(targetScale = 1.05f)
                    },
                    label = "PlayerStateTransition"
                ) { state ->
                    when (state) {
                        "LYRICS" -> currentTrack?.let {
                            LyricsScreen(
                                track = it,
                                isPlaying = isPlaying,
                                pos = pos,
                                dur = dur,
                                onBack = { fullScreenState = "PLAYER" },
                                onPP = { if (isPlaying) player?.pause() else player?.play() },
                                onSeek = { p ->
                                    player?.seekTo(p)
                                    player?.playWhenReady = true
                                }
                            )
                        }
                        "QUEUE" -> currentTrack?.let {
                            QueueScreen(
                                queue = playQueue,
                                currentIndex = currentQueueIndex,
                                coverUpdateTrigger = coverUpdateTrigger,
                                onBack = { fullScreenState = "PLAYER" },
                                onSelect = { idx ->
                                    currentQueueIndex = idx
                                    currentTrack = playQueue[idx]
                                    scope.launch {
                                        playTrack(currentTrack!!, player, context, prefs)
                                        saveRecentTrack(currentTrack!!)
                                        prefetchNextTrack(currentQueueIndex)
                                        refreshCacheCount()
                                    }
                                }
                            )
                        }
                        else -> currentTrack?.let { trackState ->
                            ExpressiveFullScreenPlayer(
                                track = trackState,
                                isPlaying = isPlaying,
                                pos = pos,
                                dur = dur,
                                isLiked = likedTracks.any { t -> t.id == trackState.id },
                                isShuffle = isShuffle,
                                repeatMode = repeatMode,
                                hasPrev = pos > 3000 || currentQueueIndex > 0,
                                hasNext = currentQueueIndex < playQueue.size - 1 || repeatMode == Player.REPEAT_MODE_ALL,
                                coverUpdateTrigger = coverUpdateTrigger,
                                onLike = {
                                    if (likedTracks.any { t -> t.id == trackState.id }) {
                                        likedTracks.removeAll { t -> t.id == trackState.id }
                                    } else {
                                        likedTracks.add(trackState)
                                    }
                                    saveLikedTracks()
                                },
                                onLyrics = { fullScreenState = "LYRICS" },
                                onQueue = { fullScreenState = "QUEUE" },
                                onMenuClick = { trackOptionsMenu = TrackMenuData(trackState) },
                                onShuffle = {
                                    isShuffle = !isShuffle
                                    val current = trackState
                                    if (isShuffle) {
                                        playQueue = listOf(current) + originalQueue.filter { t -> t.id != current.id }.shuffled()
                                        currentQueueIndex = 0
                                    } else {
                                        playQueue = originalQueue
                                        currentQueueIndex = playQueue.indexOf(trackState)
                                    }
                                },
                                onRepeat = {
                                    repeatMode = when(repeatMode) {
                                        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                                        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                                        else -> Player.REPEAT_MODE_OFF
                                    }
                                    player?.repeatMode = repeatMode
                                },
                                onPP = { if (isPlaying) player?.pause() else player?.play() },
                                onSeek = { p ->
                                    player?.seekTo(p)
                                    player?.playWhenReady = true
                                },
                                onPrev = { playPrev() },
                                onNext = { playNext() },
                                onClose = {
                                    isFullScreen = false
                                    fullScreenState = "PLAYER"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private suspend fun playTrack(
    track: YtTrack,
    player: Player?,
    context: Context,
    prefs: SharedPreferences
) {
    if (player == null) return

    if (player.currentMediaItem?.mediaId == track.id && (player.playbackState == Player.STATE_READY || player.playbackState == Player.STATE_BUFFERING)) {
        return
    }

    val downloadDir = File(context.filesDir, "hearon_downloads")
    if (!downloadDir.exists()) downloadDir.mkdirs()
    val file = File(downloadDir, "${track.id}.m4a")

    if (file.exists() && file.length() < 50_000) {
        file.delete()
    }

    val isLocal = file.exists() && file.length() >= 50_000
    val streamUrl = if (isLocal) Uri.fromFile(file).toString() else com.clio.hearon.api.YtMusicApi.getStreamUrl(track.id)

    if (streamUrl != null) {
        val mime = if (isLocal) MimeTypes.AUDIO_MP4 else if (streamUrl.contains("webm")) MimeTypes.AUDIO_WEBM else MimeTypes.AUDIO_MP4
        val mediaItem = MediaItem.Builder()
            .setUri(streamUrl)
            .setMediaId(track.id)
            .setMimeType(mime)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artist)
                    .setArtworkUri(Uri.parse(track.coverUrl))
                    .build()
            )
            .build()

        withContext(Dispatchers.Main) {
            player.stop()
            player.clearMediaItems()
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }

        if (!isLocal && isOnline(context)) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val conn = URL(streamUrl).openConnection() as HttpURLConnection
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                    conn.connect()

                    if (conn.responseCode in 200..299) {
                        val tmpFile = File(downloadDir, "${track.id}.tmp")
                        tmpFile.outputStream().use { out -> conn.inputStream.use { it.copyTo(out) } }

                        if (tmpFile.exists() && tmpFile.length() >= 50_000) {
                            tmpFile.renameTo(file)
                            val raw = prefs.getStringSet("cached_tracks_data", emptySet())?.toMutableSet() ?: mutableSetOf()
                            raw.add("${track.id}|||${track.title}|||${track.artist}|||${track.coverUrl}")
                            prefs.edit().putStringSet("cached_tracks_data", raw).apply()
                        } else {
                            tmpFile.delete()
                        }
                    }
                } catch (e: Exception) { }
            }
        }
    } else {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error al cargar el audio", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ExpressiveFullScreenPlayer(
    track: YtTrack,
    isPlaying: Boolean,
    pos: Long,
    dur: Long,
    isLiked: Boolean,
    isShuffle: Boolean,
    repeatMode: Int,
    hasPrev: Boolean,
    hasNext: Boolean,
    coverUpdateTrigger: Int,
    onLike: () -> Unit,
    onLyrics: () -> Unit,
    onQueue: () -> Unit,
    onMenuClick: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onPP: () -> Unit,
    onSeek: (Long) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit
) {
    BackHandler { onClose() }
    val prefs = LocalContext.current.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    var sliderPos by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val coverScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "CoverScale"
    )

    val coverRadius by animateDpAsState(
        targetValue = if (isPlaying) 48.dp else 24.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "CoverRadius"
    )

    val ppWidth by animateDpAsState(
        targetValue = if (isPlaying) 100.dp else 84.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "PPWidth"
    )

    val ppRadius by animateDpAsState(
        targetValue = if (isPlaying) 32.dp else 40.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "PPRadius"
    )

    val isDark = isSystemInDarkTheme()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.15f else 0.4f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
            MaterialTheme.colorScheme.background
        )
    )

    val repeatIcon = when(repeatMode) {
        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
        Player.REPEAT_MODE_ALL -> Icons.Default.RepeatOn
        else -> Icons.Default.Repeat
    }

    val repeatBg = if (repeatMode != Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    val customCover = remember(track.id, coverUpdateTrigger) { prefs.getString("custom_cover_${track.id}", track.coverUrl) }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val offsetY by animateFloatAsState(targetValue = dragOffset, label = "")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { androidx.compose.ui.unit.IntOffset(0, offsetY.toInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (dragOffset > 300f) {
                            onClose()
                        }
                        dragOffset = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    if (dragOffset + dragAmount >= 0) {
                        dragOffset += dragAmount
                    }
                }
            }
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .safeDrawingPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = customCover,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight(0.8f)
                            .aspectRatio(1f)
                            .scale(coverScale)
                            .clip(RoundedCornerShape(coverRadius)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onClose, modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)) {
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = onMenuClick, modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)) {
                            Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = if (isDragging) sliderPos else (if (dur > 0) pos.toFloat() / dur else 0f).coerceIn(0f, 1f),
                        onValueChange = { isDragging = true; sliderPos = it },
                        onValueChangeFinished = { isDragging = false; onSeek((sliderPos * dur).toLong()) },
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTime(if (isDragging) (sliderPos * dur).toLong() else pos), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatTime(dur), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onPrev, enabled = hasPrev, modifier = Modifier.size(64.dp).background(if (hasPrev) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent, CircleShape)) {
                            Icon(Icons.Default.SkipPrevious, null, tint = if (hasPrev) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(0.3f))
                        }
                        IconButton(onClick = onPP, modifier = Modifier.width(ppWidth).height(72.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(ppRadius))) {
                            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        IconButton(onClick = onNext, enabled = hasNext, modifier = Modifier.size(64.dp).background(if (hasNext) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent, CircleShape)) {
                            Icon(Icons.Default.SkipNext, null, tint = if (hasNext) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(0.3f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = onLyrics, modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))) { Icon(Icons.Default.Lyrics, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                        IconButton(onClick = onShuffle, modifier = Modifier.size(56.dp).background(if (isShuffle) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))) { Icon(Icons.Default.Shuffle, null, tint = if (isShuffle) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant) }
                        IconButton(onClick = onRepeat, modifier = Modifier.size(56.dp).background(repeatBg, RoundedCornerShape(16.dp))) { Icon(repeatIcon, null, tint = if (repeatMode != Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant) }
                        IconButton(onClick = onQueue, modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))) { Icon(Icons.Default.QueueMusic, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Text(
                        text = "Reproduciendo",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
                    ) {
                        Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                AnimatedContent(
                    targetState = track,
                    transitionSpec = {
                        fadeIn(tween(500)) + scaleIn(initialScale = 0.8f) togetherWith fadeOut(tween(500)) + scaleOut(targetScale = 1.2f)
                    },
                    label = "TrackAnimation"
                ) { currentTrack ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = customCover,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .aspectRatio(1f)
                                .scale(coverScale)
                                .clip(RoundedCornerShape(coverRadius)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(48.dp))
                        Text(
                            text = currentTrack.title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentTrack.artist,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Slider(
                    value = if (isDragging) sliderPos else (if (dur > 0) pos.toFloat() / dur else 0f).coerceIn(0f, 1f),
                    onValueChange = {
                        isDragging = true
                        sliderPos = it
                    },
                    onValueChangeFinished = {
                        isDragging = false
                        onSeek((sliderPos * dur).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(if (isDragging) (sliderPos * dur).toLong() else pos),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(dur),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPrev,
                        enabled = hasPrev,
                        modifier = Modifier
                            .size(72.dp)
                            .background(if (hasPrev) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (hasPrev) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(0.3f)
                        )
                    }

                    IconButton(
                        onClick = onPP,
                        modifier = Modifier
                            .width(ppWidth)
                            .height(80.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(ppRadius))
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    IconButton(
                        onClick = onNext,
                        enabled = hasNext,
                        modifier = Modifier
                            .size(72.dp)
                            .background(if (hasNext) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (hasNext) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onLyrics,
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))
                    ) {
                        Icon(Icons.Default.Lyrics, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    IconButton(
                        onClick = onShuffle,
                        modifier = Modifier
                            .size(64.dp)
                            .background(if (isShuffle) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            Icons.Default.Shuffle,
                            null,
                            tint = if (isShuffle) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onRepeat,
                        modifier = Modifier
                            .size(64.dp)
                            .background(repeatBg, RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            imageVector = repeatIcon,
                            contentDescription = null,
                            tint = if (repeatMode != Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onQueue,
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))
                    ) {
                        Icon(Icons.Default.QueueMusic, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun LyricsScreen(track: YtTrack, isPlaying: Boolean, pos: Long, dur: Long, onBack: () -> Unit, onPP: () -> Unit, onSeek: (Long) -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    BackHandler { onBack() }

    var tab by remember { mutableStateOf("Synced") }
    var syncedLyricsRaw by remember { mutableStateOf<String?>(null) }
    var plainLyrics by remember { mutableStateOf<String?>(null) }
    var isLoadingLyrics by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var userLyricsInput by remember { mutableStateOf("") }
    var sliderPos by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val playPauseShapeRadius by animateDpAsState(
        targetValue = if (isPlaying) 16.dp else 28.dp,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "PPShape"
    )

    val playPauseColor by animateColorAsState(
        targetValue = if (isPlaying) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
        animationSpec = tween(300),
        label = "PPColor"
    )

    LaunchedEffect(track.id) {
        isLoadingLyrics = true
        val manual = prefs.getString("manual_lyrics_${track.id}", null)
        if (manual != null) {
            if (manual.contains("[")) syncedLyricsRaw = manual else plainLyrics = manual
        } else {
            if (isOnline(context)) {
                val (s, p) = HearonBackend.getLyrics(track.title, track.artist)
                syncedLyricsRaw = s; plainLyrics = p
            }
        }
        isLoadingLyrics = false
        if (syncedLyricsRaw == null && plainLyrics != null) tab = "Static"
    }

    val parsedLyrics = remember(syncedLyricsRaw) {
        if (syncedLyricsRaw == null) emptyList()
        else {
            val regex = Regex("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)")
            syncedLyricsRaw!!.lines().mapNotNull { line ->
                val match = regex.find(line)
                if (match != null) {
                    val (m, s, msStr, text) = match.destructured
                    val ms = if (msStr.length == 2) msStr.toLong() * 10 else msStr.toLong()
                    LyricLine(m.toLong() * 60000 + s.toLong() * 1000 + ms, text.trim())
                } else null
            }
        }
    }

    val activeLineIndex by remember(pos, parsedLyrics) {
        derivedStateOf {
            if (parsedLyrics.isEmpty()) -1
            else parsedLyrics.indexOfLast { it.timeMs <= pos }.coerceAtLeast(0)
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(activeLineIndex) {
        if (tab == "Synced" && activeLineIndex >= 0 && !listState.isScrollInProgress) {
            listState.animateScrollToItem(activeLineIndex, scrollOffset = 150)
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Añadir letras manualmente") },
            text = {
                OutlinedTextField(
                    value = userLyricsInput,
                    onValueChange = { userLyricsInput = it },
                    placeholder = { Text("Pega aquí las letras (puedes incluir [00:00.00] para sincronizarlas)") },
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    prefs.edit().putString("manual_lyrics_${track.id}", userLyricsInput).apply()
                    if (userLyricsInput.contains("[")) { syncedLyricsRaw = userLyricsInput; tab = "Synced" }
                    else { plainLyrics = userLyricsInput; tab = "Static" }
                    showAddDialog = false
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).statusBarsPadding().navigationBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp).height(48.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)) {
                Icon(Icons.Default.ArrowBack, null)
            }
            Text(text = "Letras", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

            val isManual = prefs.getString("manual_lyrics_${track.id}", null) != null
            val noOfficialLyrics = syncedLyricsRaw == null && plainLyrics == null

            if (noOfficialLyrics || isManual) {
                IconButton(onClick = {
                    userLyricsInput = prefs.getString("manual_lyrics_${track.id}", "") ?: ""
                    showAddDialog = true
                }, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(Icons.Default.Edit, null)
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (isLoadingLyrics) { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) }
            else if (syncedLyricsRaw == null && plainLyrics == null) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No se han encontrado letras", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showAddDialog = true }) { Text("Añadir letras") }
                }
            } else {
                if (tab == "Synced" && parsedLyrics.isNotEmpty()) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 300.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
                        itemsIndexed(parsedLyrics) { i, line ->
                            val isActive = i == activeLineIndex
                            val isInstrumental = line.text.isBlank() || line.text == "♪" || line.text.contains("Instrumental", ignoreCase = true)

                            val bgColor by animateColorAsState(targetValue = if (isActive && !isInstrumental) MaterialTheme.colorScheme.primaryContainer else Color.Transparent, label = "")
                            val shapeRadius by animateDpAsState(targetValue = if (isActive) 32.dp else 8.dp, label = "")
                            val scale by animateFloatAsState(targetValue = if (isActive) 1.05f else 1f, label = "")

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .scale(scale)
                                    .clip(RoundedCornerShape(shapeRadius))
                                    .background(bgColor)
                                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onSeek(line.timeMs) }
                                    .padding(vertical = if (isInstrumental) 8.dp else 16.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isInstrumental) {
                                    if (isActive) {
                                        AppleMusicDots()
                                    } else {
                                        Text("•••", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                                    }
                                } else {
                                    Text(
                                        text = line.text,
                                        textAlign = TextAlign.Center,
                                        style = if (isActive) MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.titleLarge,
                                        color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(32.dp)) {
                        itemsIndexed(plainLyrics?.split("\n") ?: emptyList()) { _, line ->
                            Text(text = line, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }

        Surface(modifier = Modifier.fillMaxWidth().padding(24.dp), shape = RoundedCornerShape(32.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = formatTime(if (isDragging) (sliderPos * dur).toLong() else pos), style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = if (isDragging) sliderPos else (if (dur > 0) pos.toFloat() / dur else 0f).coerceIn(0f, 1f),
                    onValueChange = { isDragging = true; sliderPos = it },
                    onValueChangeFinished = { isDragging = false; onSeek((sliderPos * dur).toLong()) },
                    modifier = Modifier.weight(1f)
                )
                Text(text = formatTime(dur), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(12.dp))

                AnimatedContent(
                    targetState = isPlaying,
                    label = "PlayPauseIcon",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(playPauseShapeRadius))
                        .background(playPauseColor)
                        .clickable { onPP() }
                ) { playing ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (playing) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QueueScreen(
    queue: List<YtTrack>,
    currentIndex: Int,
    coverUpdateTrigger: Int,
    onBack: () -> Unit,
    onSelect: (Int) -> Unit
) {
    BackHandler { onBack() }
    val prefs = LocalContext.current.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp).height(48.dp)) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface)
            }
            Text(
                text = "Cola de Reproducción",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
            itemsIndexed(queue) { index, track ->
                val active = index == currentIndex
                val customCover = remember(track.id, coverUpdateTrigger) { prefs.getString("custom_cover_${track.id}", track.coverUrl.replace("w1080-h1080", "w226-h226")) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .background(if (active) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent)
                        .clickable { onSelect(index) }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = customCover,
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    if (active) {
                        Icon(Icons.Default.GraphicEq, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(
    likedTracks: List<YtTrack>,
    playlists: MutableList<Playlist>,
    downloadedTracks: List<YtTrack>,
    currentId: String?,
    coverUpdateTrigger: Int,
    onCreatePlaylist: (String) -> Unit,
    onDeletePlaylist: (Int) -> Unit,
    onRenamePlaylist: (Int, String) -> Unit,
    onSelectTrack: (YtTrack, List<YtTrack>, Int) -> Unit,
    onOptionsClick: (YtTrack, Int?, Int?) -> Unit,
    onClearCache: () -> Unit,
    onRefresh: () -> Unit
) {
    val prefs = LocalContext.current.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    var libraryTab by remember { mutableStateOf("Favoritos") }
    var playlistToOpenName by remember { mutableStateOf<String?>(null) }
    val playlistToOpen = playlists.find { it.name == playlistToOpenName }

    var showPlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var showDeleteWarning by remember { mutableStateOf<Int?>(null) }
    var renamePlaylistIndex by remember { mutableStateOf<Int?>(null) }
    var renamePlaylistName by remember { mutableStateOf("") }

    if (showPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showPlaylistDialog = false },
            title = { Text("Nueva Playlist", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    placeholder = { Text("Nombre de la playlist") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newPlaylistName.isNotBlank()) {
                        onCreatePlaylist(newPlaylistName)
                        showPlaylistDialog = false
                        newPlaylistName = ""
                    }
                }) { Text("Crear") }
            },
            dismissButton = { TextButton(onClick = { showPlaylistDialog = false }) { Text("Cancelar") } }
        )
    }

    if (renamePlaylistIndex != null) {
        AlertDialog(
            onDismissRequest = { renamePlaylistIndex = null },
            title = { Text("Renombrar Playlist", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                OutlinedTextField(
                    value = renamePlaylistName,
                    onValueChange = { renamePlaylistName = it },
                    placeholder = { Text("Nuevo nombre") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (renamePlaylistName.isNotBlank()) {
                        onRenamePlaylist(renamePlaylistIndex!!, renamePlaylistName)
                        renamePlaylistIndex = null
                        renamePlaylistName = ""
                    }
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { renamePlaylistIndex = null }) { Text("Cancelar") } }
        )
    }

    if (showDeleteWarning != null) {
        AlertDialog(
            onDismissRequest = { showDeleteWarning = null },
            title = { Text("Eliminar Playlist", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("¿Estás seguro de que quieres eliminar la playlist?", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePlaylist(showDeleteWarning!!)
                        showDeleteWarning = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { showDeleteWarning = null }) { Text("Cancelar") } }
        )
    }

    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = {
            scope.launch {
                refreshing = true
                onRefresh()
                delay(1000)
                refreshing = false
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedContent(
            targetState = playlistToOpenName,
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                }
            },
            label = "LibraryTransition"
        ) { openedName ->
            if (openedName != null && playlistToOpen != null) {
                BackHandler { playlistToOpenName = null }
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp).height(48.dp)) {
                        IconButton(
                            onClick = { playlistToOpenName = null },
                            modifier = Modifier.align(Alignment.CenterStart).size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)
                        ) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                        Text(
                            text = playlistToOpen.name,
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 320.dp), contentPadding = PaddingValues(bottom = 200.dp), modifier = Modifier.fillMaxSize()) {
                        val pIndex = playlists.indexOf(playlistToOpen)
                        itemsIndexed(playlistToOpen.tracks) { i, track ->
                            ExpressiveTrackRow(
                                t = track,
                                active = track.id == currentId,
                                coverUpdateTrigger = coverUpdateTrigger,
                                onClick = { onSelectTrack(track, playlistToOpen.tracks, i) },
                                onOptionsClick = { onOptionsClick(track, pIndex, i) }
                            )
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Text(
                        text = "Tu Biblioteca",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(24.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ExpressiveChip(
                            selected = libraryTab == "Favoritos",
                            text = "Favoritos",
                            onClick = { libraryTab = "Favoritos" }
                        )
                        ExpressiveChip(
                            selected = libraryTab == "Playlists",
                            text = "Playlists",
                            onClick = { libraryTab = "Playlists" }
                        )
                    }

                    when (libraryTab) {
                        "Favoritos" -> {
                            if (likedTracks.isEmpty()) {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    item {
                                        Box(modifier = Modifier.fillParentMaxSize().padding(vertical = 100.dp), contentAlignment = Alignment.Center) {
                                            Text("Aún no tienes favoritos", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(160.dp),
                                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 200.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    itemsIndexed(likedTracks) { i, track ->
                                        val isActive = track.id == currentId
                                        val customCover = remember(track.id, coverUpdateTrigger) { prefs.getString("custom_cover_${track.id}", track.coverUrl.replace("w1080-h1080", "w226-h226")) }
                                        Surface(
                                            modifier = Modifier.fillMaxWidth().aspectRatio(0.8f),
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isActive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                                            onClick = { onSelectTrack(track, likedTracks, i) }
                                        ) {
                                            Column(modifier = Modifier.fillMaxSize()) {
                                                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                                    AsyncImage(
                                                        model = customCover,
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                    IconButton(
                                                        onClick = { onOptionsClick(track, null, null) },
                                                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape).size(32.dp)
                                                    ) { Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface) }
                                                }
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Text(text = track.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                                    Text(text = track.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Playlists" -> {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(160.dp),
                                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 200.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().aspectRatio(0.8f),
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        onClick = { showPlaylistDialog = true }
                                    ) {
                                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                            Box(modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text("Nueva Playlist", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }

                                itemsIndexed(playlists) { index, playlist ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().aspectRatio(0.8f),
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        onClick = { playlistToOpenName = playlist.name }
                                    ) {
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                                val firstTrack = playlist.tracks.firstOrNull()
                                                val customCover = remember(firstTrack?.id, coverUpdateTrigger) { firstTrack?.let { prefs.getString("custom_cover_${it.id}", it.coverUrl.replace("w1080-h1080", "w226-h226")) } }
                                                if (customCover != null) {
                                                    AsyncImage(model = customCover, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                                } else {
                                                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.QueueMusic, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                                    }
                                                }
                                                Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                                    IconButton(
                                                        onClick = { renamePlaylistIndex = index; renamePlaylistName = playlist.name },
                                                        modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape).size(32.dp)
                                                    ) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface) }
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    IconButton(
                                                        onClick = { showDeleteWarning = index },
                                                        modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape).size(32.dp)
                                                    ) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error) }
                                                }
                                            }
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(text = playlist.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                                Text(text = "${playlist.tracks.size} pistas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit, onTabReselected: (Int) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(0.9f).height(72.dp),
        shape = RoundedCornerShape(40.dp),
        tonalElevation = 10.dp,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icons = listOf(Icons.Default.Home to 0, Icons.Default.Search to 1, Icons.Default.LibraryMusic to 2, Icons.Default.Settings to 3)
            val scope = rememberCoroutineScope()

            icons.forEach { (icon, index) ->
                val scale = remember { androidx.compose.animation.core.Animatable(1f) }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (selectedTab == index) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                        .scale(scale.value)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            scope.launch {
                                scale.animateTo(0.6f, tween(100))
                                scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                            }
                            if (selectedTab == index) onTabReselected(index) else onTabSelected(index)
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selectedTab == index) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ExpressiveMiniPlayer(
    track: YtTrack,
    isPlaying: Boolean,
    coverUpdateTrigger: Int,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onExpand: () -> Unit
) {
    val prefs = LocalContext.current.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    val customCover = remember(track.id, coverUpdateTrigger) { prefs.getString("custom_cover_${track.id}", track.coverUrl.replace("w1080-h1080", "w226-h226")) }

    val ppRadius by animateDpAsState(
        targetValue = if (isPlaying) 16.dp else 26.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "MiniPPRadius"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .height(72.dp)
            .clip(RoundedCornerShape(36.dp))
            .clickable { onExpand() },
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = customCover,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(width = 52.dp, height = 52.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(ppRadius))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ExpressiveTrackRow(
    t: YtTrack,
    active: Boolean,
    coverUpdateTrigger: Int,
    onClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    val prefs = LocalContext.current.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    val customCover = remember(t.id, coverUpdateTrigger) { prefs.getString("custom_cover_${t.id}", t.coverUrl.replace("w1080-h1080", "w226-h226")) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .background(if (active) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = customCover,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = t.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = t.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        IconButton(onClick = onOptionsClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HomeScreen(
    tracks: List<YtTrack>,
    forYouTracks: List<YtTrack>,
    recentTracks: List<YtTrack>,
    loading: Boolean,
    loadingForYou: Boolean,
    currentId: String?,
    coverUpdateTrigger: Int,
    onSelect: (YtTrack) -> Unit,
    onOptionsClick: (YtTrack) -> Unit,
    onRefresh: () -> Unit
) {
    var homeTab by remember { mutableStateOf("Tendencias") }

    val currentList = when (homeTab) {
        "Tendencias" -> tracks
        "Para ti" -> forYouTracks
        "Recientes" -> recentTracks
        else -> tracks
    }

    val isListLoading = when(homeTab) {
        "Tendencias" -> loading
        "Para ti" -> loadingForYou
        else -> false
    }

    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isListLoading,
        onRefresh = onRefresh,
        state = state,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Text(
                text = "Inicio",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExpressiveChip(selected = homeTab == "Tendencias", text = "Tendencias", onClick = { homeTab = "Tendencias" })
                ExpressiveChip(selected = homeTab == "Para ti", text = "Para ti", onClick = { homeTab = "Para ti" })
                ExpressiveChip(selected = homeTab == "Recientes", text = "Recientes", onClick = { homeTab = "Recientes" })
            }

            if (currentList.isEmpty() && !isListLoading) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize().padding(vertical = 100.dp), contentAlignment = Alignment.Center) {
                            Text(if (homeTab == "Tendencias") "No hay nada que mostrar. Desliza para recargar." else "Aún no hay canciones aquí.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 320.dp),
                    contentPadding = PaddingValues(bottom = 200.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(currentList) { _, track ->
                        ExpressiveTrackRow(
                            t = track,
                            active = track.id == currentId,
                            coverUpdateTrigger = coverUpdateTrigger,
                            onClick = { onSelect(track) },
                            onOptionsClick = { onOptionsClick(track) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchScreen(
    currentId: String?,
    coverUpdateTrigger: Int,
    onSelect: (YtTrack) -> Unit,
    onOptionsClick: (YtTrack) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<YtTrack>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    var searchType by remember { mutableStateOf("MUSICA") }
    var hasSearched by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current

    fun performSearch() {
        if (query.isBlank()) return
        focus.clearFocus()
        searching = true
        hasSearched = true
        scope.launch {
            results = HearonBackend.search(query, searchType)
            searching = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Text(
            text = "Explorar",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(24.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { performSearch() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExpressiveChip(
                selected = searchType == "MUSICA",
                text = "YT Music",
                onClick = { searchType = "MUSICA"; if (query.isNotBlank()) performSearch() }
            )
            ExpressiveChip(
                selected = searchType == "VIDEO",
                text = "YouTube",
                onClick = { searchType = "VIDEO"; if (query.isNotBlank()) performSearch() }
            )
        }

        if (searching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (hasSearched && results.isEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize().padding(vertical = 100.dp), contentAlignment = Alignment.Center) {
                        Text("No se han encontrado resultados", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 320.dp), contentPadding = PaddingValues(bottom = 200.dp), modifier = Modifier.fillMaxSize()) {
                itemsIndexed(results) { _, track ->
                    ExpressiveTrackRow(
                        t = track,
                        active = track.id == currentId,
                        coverUpdateTrigger = coverUpdateTrigger,
                        onClick = { onSelect(track) },
                        onOptionsClick = { onOptionsClick(track) }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingItemRow(
    icon: ImageVector?,
    title: String,
    subtitle: String = "",
    onClick: () -> Unit = {},
    trailing: @Composable (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (title == "Versión actual") {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                } else if (icon != null) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (trailing != null) {
                Spacer(modifier = Modifier.width(16.dp))
                trailing()
            }
        }
    }
}

@Composable
fun SettingSliderRow(
    icon: ImageVector,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueText: String,
    enabled: Boolean = true,
    onValueChange: (Float) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    prefs: SharedPreferences,
    context: Context,
    cachedCount: Int,
    cacheSizeMB: Int,
    isDynamicColorEnabled: Boolean,
    onDynamicColorToggle: (Boolean) -> Unit,
    onClearCache: () -> Unit
) {
    var currentSettingsPage by remember { mutableStateOf("MAIN") }
    BackHandler(enabled = currentSettingsPage != "MAIN") { currentSettingsPage = "MAIN" }

    var audioQuality by remember { mutableStateOf(prefs.getString("audio_quality", "Alta") ?: "Alta") }
    var showQuality by remember { mutableStateOf(false) }

    var crossfadeEnabled by remember { mutableStateOf(prefs.getBoolean("crossfade_enabled", false)) }
    var crossfadeDur by remember { mutableFloatStateOf(prefs.getFloat("crossfade_dur", 1f).coerceAtLeast(1f)) }
    var normAudio by remember { mutableStateOf(prefs.getBoolean("norm_audio", true)) }
    var maxImgCache by remember { mutableFloatStateOf(prefs.getFloat("max_img_cache", 512f)) }
    var dataSaver by remember { mutableStateOf(prefs.getBoolean("data_saver", false)) }
    var autoUpdate by remember { mutableStateOf(prefs.getBoolean("auto_update", false)) }
    var notifyUpdate by remember { mutableStateOf(prefs.getBoolean("notify_update", false)) }
    var checkUpdateState by remember { mutableStateOf("Buscar actualizaciones") }

    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    if (showQuality) {
        ModalBottomSheet(onDismissRequest = { showQuality = false }) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text("Calidad de sonido", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(24.dp))
                listOf("Alta", "Normal", "Baja").forEach { q ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = if (audioQuality == q) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                        onClick = { audioQuality = q; prefs.edit().putString("audio_quality", q).apply(); showQuality = false }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = q, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.weight(1f))
                            if (audioQuality == q) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }
        }
    }

    Crossfade(targetState = currentSettingsPage, label = "SettingsNav") { page ->
        when (page) {
            "MAIN" -> {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Text("Ajustes", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(24.dp))
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        item {
                            SettingItemRow(Icons.Default.Palette, "Apariencia", "Tema claro/oscuro y colores adaptativos", { currentSettingsPage = "APARIENCIA" })
                            SettingItemRow(Icons.Default.GraphicEq, "Reproductor y sonido", "Calidad, crossfade, volumen", { currentSettingsPage = "REPRODUCTOR" })
                            SettingItemRow(Icons.Default.Storage, "Almacenamiento y Datos", "Caché de audio, imágenes y red", { currentSettingsPage = "ALMACENAMIENTO" })
                            SettingItemRow(Icons.Default.SystemUpdate, "Acerca de HearOn", "Versión $APP_VERSION", { currentSettingsPage = "ACTUALIZADOR" })
                        }
                    }
                }
            }
            "APARIENCIA" -> {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentSettingsPage = "MAIN" }) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apariencia", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        item {
                            SettingsHeader("Colores adaptativos")
                            SettingItemRow(Icons.Default.ColorLens, "Tema dinámico", "Extraer colores de la portada de la canción", { onDynamicColorToggle(!isDynamicColorEnabled) }) { Switch(checked = isDynamicColorEnabled, onCheckedChange = null) }
                            SettingsHeader("Tema de la aplicación")
                            listOf("Sistema", "Claro", "Oscuro").forEach { t ->
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.elevatedCardColors(containerColor = if (currentTheme == t) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh),
                                    onClick = { onThemeChange(t) }
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = t, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.weight(1f))
                                        if (currentTheme == t) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "REPRODUCTOR" -> {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentSettingsPage = "MAIN" }) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reproductor y sonido", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        item {
                            SettingsHeader("Reproductor")
                            SettingItemRow(Icons.Default.GraphicEq, "Calidad de sonido", audioQuality, { showQuality = true })
                            SettingItemRow(Icons.Default.CompareArrows, "Crossfade", "Transición suave entre canciones", { crossfadeEnabled = !crossfadeEnabled; prefs.edit().putBoolean("crossfade_enabled", crossfadeEnabled).apply() }) { Switch(checked = crossfadeEnabled, onCheckedChange = null) }
                            SettingSliderRow(Icons.Default.WrapText, "Duración del crossfade", crossfadeDur, 1f..10f, if (crossfadeEnabled) "${crossfadeDur.toInt()} s" else "Desactivado", crossfadeEnabled) { crossfadeDur = it; prefs.edit().putFloat("crossfade_dur", it).apply() }
                            SettingItemRow(Icons.Default.VolumeUp, "Normalización del audio", "Iguala el volumen entre canciones", { normAudio = !normAudio; prefs.edit().putBoolean("norm_audio", normAudio).apply() }) { Switch(checked = normAudio, onCheckedChange = null) }
                        }
                    }
                }
            }
            "ALMACENAMIENTO" -> {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentSettingsPage = "MAIN" }) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Almacenamiento", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        item {
                            SettingsHeader("Caché de audio invisible")
                            SettingItemRow(Icons.Default.LibraryMusic, "Música guardada automáticamente", if(cachedCount > 0) "$cachedCount pistas ($cacheSizeMB MB)" else "0 MB", {})
                            SettingItemRow(Icons.Default.DeleteSweep, "Vaciar caché de audio", "Libera espacio borrando los archivos de música", { onClearCache() })
                            SettingsHeader("Imágenes y Datos")
                            SettingSliderRow(Icons.Default.ImageSearch, "Límite de portadas", maxImgCache, 128f..1024f, java.lang.String.format(java.util.Locale.US, "%.0f MB", maxImgCache), true) { maxImgCache = it; prefs.edit().putFloat("max_img_cache", it).apply() }
                            SettingItemRow(Icons.Default.DeleteOutline, "Limpiar portadas", "Borra las imágenes guardadas en caché", { })
                            SettingItemRow(Icons.Default.Cloud, "Ahorro de datos", "Carga imágenes en menor resolución", { dataSaver = !dataSaver; prefs.edit().putBoolean("data_saver", dataSaver).apply() }) { Switch(checked = dataSaver, onCheckedChange = null) }
                        }
                    }
                }
            }
            "ACTUALIZADOR" -> {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentSettingsPage = "MAIN" }) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Acerca de", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.size(72.dp), tint = Color.Unspecified)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "HearOn", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "made w/ love from \uD83C\uDDEA\uD83C\uDDF8 <3", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "github.com/oriilol", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            SettingsHeader("Versión y Actualizaciones")
                            SettingItemRow(null, "Versión actual", "v$APP_VERSION", {})
                            SettingItemRow(Icons.Default.Update, "Actualizaciones automáticas", "", { autoUpdate = !autoUpdate; prefs.edit().putBoolean("auto_update", autoUpdate).apply() }) { Switch(checked = autoUpdate, onCheckedChange = null) }
                            SettingItemRow(Icons.Default.Notifications, "Avisarme si hay nueva versión", "", { notifyUpdate = !notifyUpdate; prefs.edit().putBoolean("notify_update", notifyUpdate).apply() }) { Switch(checked = notifyUpdate, onCheckedChange = null) }

                            Spacer(modifier = Modifier.height(16.dp))
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(20.dp),
                                onClick = {
                                    if (checkUpdateState.startsWith("Actualización disponible")) {
                                        uriHandler.openUri("https://github.com/oriilol/hearon/releases/latest")
                                    } else {
                                        checkUpdateState = "Buscando..."
                                        scope.launch {
                                            val latest = HearonBackend.checkUpdate()
                                            checkUpdateState = if (latest != null && latest != APP_VERSION) "Actualización disponible: $latest" else "Estás en la última versión"
                                            delay(3000)
                                            if (!checkUpdateState.startsWith("Actualización disponible")) checkUpdateState = "Buscar actualizaciones"
                                        }
                                    }
                                },
                                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = checkUpdateState, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

object HearonBackend {
    private fun findPanel(json: JSONObject): JSONArray? {
        val keys = json.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            if (k == "playlistPanelRenderer") return json.optJSONObject(k)?.optJSONArray("contents")
            val o = json.optJSONObject(k)
            if (o != null) { val r = findPanel(o); if (r != null) return r }
            val a = json.optJSONArray(k)
            if (a != null) {
                for (i in 0 until a.length()) {
                    val ao = a.optJSONObject(i)
                    if (ao != null) { val r = findPanel(ao); if (r != null) return r }
                }
            }
        }
        return null
    }

    suspend fun search(q: String, type: String = "MUSICA"): List<YtTrack> = withContext(Dispatchers.IO) {
        val list = mutableListOf<YtTrack>()
        try {
            val conn = URL("https://music.youtube.com/youtubei/v1/search?prettyPrint=false").openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("User-Agent", "Mozilla/5.0")
            conn.doOutput = true

            val params = if (type == "MUSICA") "EgWKAQIIAWoMEAMQBBAJEA4QChAF" else "EgWKAQIQAWoMEAMQBBAJEA4QChAF"
            val body = """{"context":{"client":{"clientName":"WEB_REMIX","clientVersion":"1.20260315.01.00"}},"query":"$q","params":"$params"}"""
            conn.outputStream.write(body.toByteArray())

            if (conn.responseCode == 200) {
                val json = JSONObject(conn.inputStream.bufferedReader().readText())
                val its = json.optJSONObject("contents")?.optJSONObject("tabbedSearchResultsRenderer")?.optJSONArray("tabs")?.optJSONObject(0)?.optJSONObject("tabRenderer")?.optJSONObject("content")?.optJSONObject("sectionListRenderer")?.optJSONArray("contents")
                if (its != null) {
                    for (i in 0 until its.length()) {
                        val sh = its.optJSONObject(i)?.optJSONObject("musicShelfRenderer")?.optJSONArray("contents") ?: continue
                        for (j in 0 until sh.length()) {
                            val item = sh.optJSONObject(j)?.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                            val flex = item.optJSONArray("flexColumns")
                            val title = flex?.optJSONObject(0)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")?.optJSONObject("text")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: continue
                            val artist = flex?.optJSONObject(1)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")?.optJSONObject("text")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: "Unknown"
                            val id = item.optJSONObject("playlistItemData")?.optString("videoId") ?: continue
                            val th = item.optJSONObject("thumbnail")?.optJSONObject("musicThumbnailRenderer")?.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
                            val cover = th?.optJSONObject(th.length() - 1)?.optString("url")?.replace(Regex("=w\\d+-h\\d+.*"), "=w1080-h1080-l90-rj") ?: ""
                            list.add(YtTrack(id, title, artist, cover))
                        }
                    }
                }
            }
        } catch (e: Exception) {}
        list
    }

    suspend fun getUpNext(vId: String): List<YtTrack> = withContext(Dispatchers.IO) {
        val list = mutableListOf<YtTrack>()
        try {
            val conn = URL("https://music.youtube.com/youtubei/v1/next?prettyPrint=false").openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("User-Agent", "Mozilla/5.0")
            conn.doOutput = true

            val body = """{"context":{"client":{"clientName":"WEB_REMIX","clientVersion":"1.20260315.01.00"}},"videoId":"$vId","playlistId":"RDAMVM$vId"}"""
            conn.outputStream.write(body.toByteArray())

            if (conn.responseCode == 200) {
                val j = JSONObject(conn.inputStream.bufferedReader().readText())
                val p = findPanel(j)
                if (p != null) {
                    for (i in 0 until p.length()) {
                        val it = p.optJSONObject(i)?.optJSONObject("playlistPanelVideoRenderer") ?: continue
                        val id = it.optString("videoId")
                        val title = it.optJSONObject("title")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: continue
                        val artist = it.optJSONObject("longBylineText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: "Unknown"
                        val th = it.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
                        val cover = th?.optJSONObject(th.length() - 1)?.optString("url")?.replace(Regex("=w\\d+-h\\d+.*"), "=w1080-h1080-l90-rj") ?: ""
                        if (id.isNotEmpty()) {
                            list.add(YtTrack(id, title, artist, cover))
                        }
                    }
                }
            }
        } catch (e: Exception) {}
        list
    }

    suspend fun getLyrics(ti: String, ar: String): Pair<String?, String?> = withContext(Dispatchers.IO) {
        try {
            val cT = URLEncoder.encode(ti.replace(Regex("\\(.*\\)|\\[.*\\]"), "").trim(), "UTF-8")
            val cA = URLEncoder.encode(ar.replace(Regex(",.*|&.*"), "").trim(), "UTF-8")
            val conn = URL("https://lrclib.net/api/get?track_name=$cT&artist_name=$cA").openConnection() as HttpURLConnection
            conn.setRequestProperty("User-Agent", "HearonApp/1.0")

            if (conn.responseCode == 200) {
                val j = JSONObject(conn.inputStream.bufferedReader().readText())
                return@withContext Pair(
                    j.optString("syncedLyrics", "").takeIf { it.isNotBlank() },
                    j.optString("plainLyrics", "").takeIf { it.isNotBlank() }
                )
            }
        } catch (e: Exception) {}
        Pair(null, null)
    }

    suspend fun checkUpdate(): String? = withContext(Dispatchers.IO) {
        try {
            val c = URL("https://api.github.com/repos/oriilol/hearon/releases/latest").openConnection() as HttpURLConnection
            c.setRequestProperty("User-Agent", "HearonApp")
            if (c.responseCode == 200) {
                val json = JSONObject(c.inputStream.bufferedReader().readText())
                json.optString("tag_name").replace("v", "")
            } else null
        } catch (e: Exception) { null }
    }
}