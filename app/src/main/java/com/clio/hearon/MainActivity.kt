@file:OptIn(
    androidx.compose.ui.text.ExperimentalTextApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.media3.common.util.UnstableApi::class
)

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
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
import java.util.Locale

val PixelFont = FontFamily(
    Font(resId = R.font.google_sans_flex, variationSettings = FontVariation.Settings(FontVariation.Setting("ROND", 100f)))
)

val APP_VERSION = "1.1.0"

object L {
    private val es = mapOf(
        "lang_system" to "Sistema (Predeterminado)",
        "offline_title" to "Sin conexión a internet",
        "offline_desc" to "Ve a tu Biblioteca para escuchar la música que tienes en caché.",
        "theme_system" to "Sistema",
        "theme_dark" to "Oscuro",
        "theme_light" to "Claro",
        "error_audio" to "Error al cargar el audio",
        "now_playing" to "Reproduciendo",
        "lyrics" to "Letras",
        "add_lyrics_manual" to "Añadir letras manualmente",
        "paste_lyrics_here" to "Pega aquí las letras (puedes incluir [00:00.00] para sincronizarlas)",
        "save" to "Guardar",
        "cancel" to "Cancelar",
        "no_lyrics_found" to "No se han encontrado letras",
        "add_lyrics" to "Añadir letras",
        "play_queue" to "Cola de Reproducción",
        "new_playlist" to "Nueva Playlist",
        "playlist_name" to "Nombre de la playlist",
        "create" to "Crear",
        "rename_playlist" to "Renombrar Playlist",
        "new_name" to "Nuevo nombre",
        "delete_playlist" to "Eliminar Playlist",
        "delete_playlist_confirm" to "¿Estás seguro de que quieres eliminar la playlist?",
        "delete" to "Eliminar",
        "your_library" to "Tu Biblioteca",
        "favorites" to "Favoritos",
        "playlists" to "Playlists",
        "no_favorites_yet" to "Aún no tienes favoritos",
        "tracks" to "pistas",
        "home" to "Inicio",
        "trending" to "Tendencias",
        "for_you" to "Para ti",
        "recent" to "Recientes",
        "downloads" to "Descargas",
        "nothing_to_show" to "No hay nada que mostrar. Desliza para recargar.",
        "no_recent_songs" to "Aún no hay canciones aquí.",
        "explore" to "Explorar",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "No se han encontrado resultados",
        "settings" to "Ajustes",
        "appearance" to "Apariencia",
        "appearance_desc" to "Tema claro/oscuro y colores adaptativos",
        "player_and_sound" to "Reproductor y sonido",
        "player_desc" to "Calidad, crossfade, volumen",
        "storage_and_data" to "Almacenamiento y Datos",
        "storage_desc" to "Caché de audio, imágenes y red",
        "about_hearon" to "Acerca de HearOn",
        "adaptive_colors" to "Colores adaptativos",
        "dynamic_theme" to "Tema dinámico",
        "dynamic_theme_desc" to "Extraer colores de la portada de la canción",
        "app_theme" to "Tema de la aplicación",
        "player" to "Reproductor",
        "sound_quality" to "Calidad de sonido",
        "quality_high" to "Alta",
        "quality_normal" to "Normal",
        "quality_low" to "Baja",
        "crossfade" to "Crossfade",
        "crossfade_desc" to "Transición suave entre canciones",
        "crossfade_duration" to "Duración del crossfade",
        "disabled" to "Desactivado",
        "audio_normalization" to "Normalización del audio",
        "audio_normalization_desc" to "Iguala el volumen entre canciones",
        "invisible_audio_cache" to "Caché de audio invisible",
        "auto_saved_music" to "Música guardada automáticamente",
        "clear_audio_cache" to "Vaciar caché de audio",
        "clear_audio_cache_desc" to "Libera espacio borrando los archivos de música",
        "images_and_data" to "Imágenes y Datos",
        "cover_limit" to "Límite de portadas",
        "clear_covers" to "Limpiar portadas",
        "clear_covers_desc" to "Borra las imágenes guardadas en caché",
        "data_saver" to "Ahorro de datos",
        "data_saver_desc" to "Carga imágenes en menor resolución",
        "about" to "Acerca de",
        "version_and_updates" to "Versión y Actualizaciones",
        "current_version" to "Versión actual",
        "auto_updates" to "Actualizaciones automáticas",
        "notify_updates" to "Avisarme si hay nueva versión",
        "check_updates" to "Buscar actualizaciones",
        "update_available" to "Actualización disponible: ",
        "latest_version" to "Estás en la última versión",
        "searching" to "Buscando...",
        "remove_favorite" to "Quitar de favoritos",
        "add_favorite" to "Añadir a favoritos",
        "remove_playlist" to "Quitar de esta playlist",
        "add_to_playlist" to "Añadir a playlist",
        "change_cover" to "Cambiar portada",
        "track_info" to "Información de la pista",
        "track_info_title" to "Info de pista",
        "title_label" to "Título: ",
        "artist_label" to "Artista: ",
        "yt_id_label" to "ID YouTube: ",
        "origin_label" to "Origen: ",
        "local_cache" to "Caché Local (Sin conexión)",
        "streaming" to "Streaming",
        "network_quality" to "Calidad de red: ",
        "close" to "Cerrar",
        "cover_saved" to "Portada guardada.",
        "no_playlists_yet" to "No tienes ninguna playlist. Ve a tu Biblioteca para crear una.",
        "clear_cache_title" to "Vaciar caché",
        "clear_cache_warning" to "Las pistas de audio guardadas se borrarán y volverán a gastar datos al reproducirse. ¿Continuar?",
        "no_connection_track" to "No hay conexión para reproducir esta pista.",
        "clear_history" to "Borrar historial",
        "clear_history_desc" to "Elimina las canciones recientes",
        "history_cleared" to "Historial borrado",
        "downloading_update" to "Abriendo navegador para descargar...",
        "language" to "Idioma",
        "language_desc" to "Cambiar el idioma de la aplicación"
    )

    private val en = mapOf(
        "lang_system" to "System (Default)",
        "offline_title" to "No internet connection",
        "offline_desc" to "Go to your Library to listen to your cached music.",
        "theme_system" to "System",
        "theme_dark" to "Dark",
        "theme_light" to "Light",
        "error_audio" to "Error loading audio",
        "now_playing" to "Now Playing",
        "lyrics" to "Lyrics",
        "add_lyrics_manual" to "Add lyrics manually",
        "paste_lyrics_here" to "Paste lyrics here (you can include [00:00.00] to sync them)",
        "save" to "Save",
        "cancel" to "Cancel",
        "no_lyrics_found" to "No lyrics found",
        "add_lyrics" to "Add lyrics",
        "play_queue" to "Play Queue",
        "new_playlist" to "New Playlist",
        "playlist_name" to "Playlist name",
        "create" to "Create",
        "rename_playlist" to "Rename Playlist",
        "new_name" to "New name",
        "delete_playlist" to "Delete Playlist",
        "delete_playlist_confirm" to "Are you sure you want to delete this playlist?",
        "delete" to "Delete",
        "your_library" to "Your Library",
        "favorites" to "Favorites",
        "playlists" to "Playlists",
        "no_favorites_yet" to "You don't have any favorites yet",
        "tracks" to "tracks",
        "home" to "Home",
        "trending" to "Trending",
        "for_you" to "For you",
        "recent" to "Recent",
        "downloads" to "Downloads",
        "nothing_to_show" to "Nothing to show. Swipe down to refresh.",
        "no_recent_songs" to "No songs here yet.",
        "explore" to "Explore",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "No results found",
        "settings" to "Settings",
        "appearance" to "Appearance",
        "appearance_desc" to "Light/dark theme and adaptive colors",
        "player_and_sound" to "Player and sound",
        "player_desc" to "Quality, crossfade, volume",
        "storage_and_data" to "Storage and Data",
        "storage_desc" to "Audio cache, images, and network",
        "about_hearon" to "About HearOn",
        "adaptive_colors" to "Adaptive colors",
        "dynamic_theme" to "Dynamic theme",
        "dynamic_theme_desc" to "Extract colors from the song cover",
        "app_theme" to "App theme",
        "player" to "Player",
        "sound_quality" to "Sound quality",
        "quality_high" to "High",
        "quality_normal" to "Normal",
        "quality_low" to "Low",
        "crossfade" to "Crossfade",
        "crossfade_desc" to "Smooth transition between songs",
        "crossfade_duration" to "Crossfade duration",
        "disabled" to "Disabled",
        "audio_normalization" to "Audio normalization",
        "audio_normalization_desc" to "Equalize volume between songs",
        "invisible_audio_cache" to "Invisible audio cache",
        "auto_saved_music" to "Automatically saved music",
        "clear_audio_cache" to "Clear audio cache",
        "clear_audio_cache_desc" to "Free up space by deleting music files",
        "images_and_data" to "Images and Data",
        "cover_limit" to "Cover limit",
        "clear_covers" to "Clear covers",
        "clear_covers_desc" to "Delete cached images",
        "data_saver" to "Data saver",
        "data_saver_desc" to "Load images in lower resolution",
        "about" to "About",
        "version_and_updates" to "Version and Updates",
        "current_version" to "Current version",
        "auto_updates" to "Automatic updates",
        "notify_updates" to "Notify me of new versions",
        "check_updates" to "Check for updates",
        "update_available" to "Update available: ",
        "latest_version" to "You are on the latest version",
        "searching" to "Searching...",
        "remove_favorite" to "Remove from favorites",
        "add_favorite" to "Add to favorites",
        "remove_playlist" to "Remove from this playlist",
        "add_to_playlist" to "Add to playlist",
        "change_cover" to "Change cover",
        "track_info" to "Track information",
        "track_info_title" to "Track info",
        "title_label" to "Title: ",
        "artist_label" to "Artist: ",
        "yt_id_label" to "YouTube ID: ",
        "origin_label" to "Origin: ",
        "local_cache" to "Local Cache (Offline)",
        "streaming" to "Streaming",
        "network_quality" to "Network quality: ",
        "close" to "Close",
        "cover_saved" to "Cover saved.",
        "no_playlists_yet" to "You have no playlists. Go to your Library to create one.",
        "clear_cache_title" to "Clear cache",
        "clear_cache_warning" to "Saved audio tracks will be deleted and will consume data when played again. Continue?",
        "no_connection_track" to "No connection to play this track.",
        "clear_history" to "Clear history",
        "clear_history_desc" to "Delete recent songs",
        "history_cleared" to "History cleared",
        "downloading_update" to "Opening browser to download...",
        "language" to "Language",
        "language_desc" to "Change application language"
    )

    private val fr = mapOf(
        "lang_system" to "Système (Par défaut)",
        "offline_title" to "Pas de connexion internet",
        "offline_desc" to "Allez dans votre Bibliothèque pour écouter la musique en cache.",
        "theme_system" to "Système",
        "theme_dark" to "Sombre",
        "theme_light" to "Clair",
        "error_audio" to "Erreur de chargement audio",
        "now_playing" to "En lecture",
        "lyrics" to "Paroles",
        "add_lyrics_manual" to "Ajouter des paroles manuellement",
        "paste_lyrics_here" to "Collez les paroles ici (vous pouvez inclure [00:00.00] pour synchroniser)",
        "save" to "Enregistrer",
        "cancel" to "Annuler",
        "no_lyrics_found" to "Aucune parole trouvée",
        "add_lyrics" to "Ajouter des paroles",
        "play_queue" to "File d'attente",
        "new_playlist" to "Nouvelle Playlist",
        "playlist_name" to "Nom de la playlist",
        "create" to "Créer",
        "rename_playlist" to "Renommer la playlist",
        "new_name" to "Nouveau nom",
        "delete_playlist" to "Supprimer la playlist",
        "delete_playlist_confirm" to "Voulez-vous vraiment supprimer cette playlist?",
        "delete" to "Supprimer",
        "your_library" to "Votre Bibliothèque",
        "favorites" to "Favoris",
        "playlists" to "Playlists",
        "no_favorites_yet" to "Aucun favori pour le moment",
        "tracks" to "pistes",
        "home" to "Accueil",
        "trending" to "Tendances",
        "for_you" to "Pour vous",
        "recent" to "Récents",
        "downloads" to "Téléchargements",
        "nothing_to_show" to "Rien à afficher. Glissez vers le bas pour actualiser.",
        "no_recent_songs" to "Aucune chanson récente.",
        "explore" to "Explorer",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "Aucun résultat",
        "settings" to "Paramètres",
        "appearance" to "Apparence",
        "appearance_desc" to "Thème clair/sombre et couleurs adaptatives",
        "player_and_sound" to "Lecteur et son",
        "player_desc" to "Qualité, fondu enchaîné, volume",
        "storage_and_data" to "Stockage et données",
        "storage_desc" to "Cache audio, images et réseau",
        "about_hearon" to "À propos de HearOn",
        "adaptive_colors" to "Couleurs adaptatives",
        "dynamic_theme" to "Thème dynamique",
        "dynamic_theme_desc" to "Extraire les couleurs de la pochette",
        "app_theme" to "Thème de l'application",
        "player" to "Lecteur",
        "sound_quality" to "Qualité du son",
        "quality_high" to "Haute",
        "quality_normal" to "Normale",
        "quality_low" to "Basse",
        "crossfade" to "Fondu enchaîné",
        "crossfade_desc" to "Transition douce entre les chansons",
        "crossfade_duration" to "Durée du fondu",
        "disabled" to "Désactivé",
        "audio_normalization" to "Normalisation audio",
        "audio_normalization_desc" to "Égaliser le volume entre les chansons",
        "invisible_audio_cache" to "Cache audio invisible",
        "auto_saved_music" to "Musique enregistrée automatiquement",
        "clear_audio_cache" to "Vider le cache audio",
        "clear_audio_cache_desc" to "Libérer de l'espace en supprimant les fichiers",
        "images_and_data" to "Images et Données",
        "cover_limit" to "Limite de pochettes",
        "clear_covers" to "Effacer les pochettes",
        "clear_covers_desc" to "Supprimer les images en cache",
        "data_saver" to "Économiseur de données",
        "data_saver_desc" to "Charger les images en basse résolution",
        "about" to "À propos",
        "version_and_updates" to "Version et mises à jour",
        "current_version" to "Version actuelle",
        "auto_updates" to "Mises à jour automatiques",
        "notify_updates" to "M'avertir des nouvelles versions",
        "check_updates" to "Vérifier les mises à jour",
        "update_available" to "Mise à jour disponible: ",
        "latest_version" to "Vous avez la dernière version",
        "searching" to "Recherche...",
        "remove_favorite" to "Retirer des favoris",
        "add_favorite" to "Ajouter aux favoris",
        "remove_playlist" to "Retirer de cette playlist",
        "add_to_playlist" to "Ajouter à la playlist",
        "change_cover" to "Changer la pochette",
        "track_info" to "Informations sur la piste",
        "track_info_title" to "Info de la piste",
        "title_label" to "Titre: ",
        "artist_label" to "Artiste: ",
        "yt_id_label" to "ID YouTube: ",
        "origin_label" to "Origine: ",
        "local_cache" to "Cache local (Hors ligne)",
        "streaming" to "Streaming",
        "network_quality" to "Qualité du réseau: ",
        "close" to "Fermer",
        "cover_saved" to "Pochette enregistrée.",
        "no_playlists_yet" to "Vous n'avez pas de playlist. Allez dans la Bibliothèque pour en créer une.",
        "clear_cache_title" to "Vider le cache",
        "clear_cache_warning" to "Les pistes seront supprimées et consommeront des données lors de la prochaine lecture. Continuer?",
        "no_connection_track" to "Pas de connexion pour lire cette piste.",
        "clear_history" to "Effacer l'historique",
        "clear_history_desc" to "Supprimer les chansons récentes",
        "history_cleared" to "Historique effacé",
        "downloading_update" to "Ouverture du navigateur pour télécharger...",
        "language" to "Langue",
        "language_desc" to "Changer la langue de l'application"
    )

    private val de = mapOf(
        "lang_system" to "System (Standard)",
        "offline_title" to "Keine Internetverbindung",
        "offline_desc" to "Gehe zur Bibliothek, um gespeicherte Musik zu hören.",
        "theme_system" to "System",
        "theme_dark" to "Dunkel",
        "theme_light" to "Hell",
        "error_audio" to "Fehler beim Laden des Audios",
        "now_playing" to "Wird abgespielt",
        "lyrics" to "Songtexte",
        "add_lyrics_manual" to "Songtext manuell hinzufügen",
        "paste_lyrics_here" to "Füge den Text hier ein (mit [00:00.00] zur Synchronisation)",
        "save" to "Speichern",
        "cancel" to "Abbrechen",
        "no_lyrics_found" to "Keine Songtexte gefunden",
        "add_lyrics" to "Text hinzufügen",
        "play_queue" to "Warteschlange",
        "new_playlist" to "Neue Playlist",
        "playlist_name" to "Name der Playlist",
        "create" to "Erstellen",
        "rename_playlist" to "Playlist umbenennen",
        "new_name" to "Neuer Name",
        "delete_playlist" to "Playlist löschen",
        "delete_playlist_confirm" to "Bist du sicher, dass du die Playlist löschen möchtest?",
        "delete" to "Löschen",
        "your_library" to "Deine Bibliothek",
        "favorites" to "Favoriten",
        "playlists" to "Playlists",
        "no_favorites_yet" to "Noch keine Favoriten vorhanden",
        "tracks" to "Titel",
        "home" to "Start",
        "trending" to "Trends",
        "for_you" to "Für dich",
        "recent" to "Zuletzt gehört",
        "downloads" to "Downloads",
        "nothing_to_show" to "Nichts zu sehen. Wische zum Aktualisieren.",
        "no_recent_songs" to "Noch keine kürzlich gespielten Songs.",
        "explore" to "Entdecken",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "Keine Ergebnisse",
        "settings" to "Einstellungen",
        "appearance" to "Erscheinungsbild",
        "appearance_desc" to "Helles/Dunkles Design und adaptive Farben",
        "player_and_sound" to "Player und Sound",
        "player_desc" to "Qualität, Crossfade, Lautstärke",
        "storage_and_data" to "Speicher und Daten",
        "storage_desc" to "Audio-Cache, Bilder und Netzwerk",
        "about_hearon" to "Über HearOn",
        "adaptive_colors" to "Adaptive Farben",
        "dynamic_theme" to "Dynamisches Design",
        "dynamic_theme_desc" to "Farben aus dem Cover extrahieren",
        "app_theme" to "App-Design",
        "player" to "Player",
        "sound_quality" to "Audioqualität",
        "quality_high" to "Hoch",
        "quality_normal" to "Normal",
        "quality_low" to "Niedrig",
        "crossfade" to "Crossfade",
        "crossfade_desc" to "Weicher Übergang zwischen Songs",
        "crossfade_duration" to "Dauer des Crossfades",
        "disabled" to "Deaktiviert",
        "audio_normalization" to "Audio-Normalisierung",
        "audio_normalization_desc" to "Lautstärke angleichen",
        "invisible_audio_cache" to "Unsichtbarer Audio-Cache",
        "auto_saved_music" to "Automatisch gespeicherte Musik",
        "clear_audio_cache" to "Audio-Cache leeren",
        "clear_audio_cache_desc" to "Speicherplatz freigeben",
        "images_and_data" to "Bilder und Daten",
        "cover_limit" to "Cover-Limit",
        "clear_covers" to "Cover löschen",
        "clear_covers_desc" to "Zwischengespeicherte Bilder löschen",
        "data_saver" to "Datensparmodus",
        "data_saver_desc" to "Bilder in niedrigerer Auflösung laden",
        "about" to "Über",
        "version_and_updates" to "Version und Updates",
        "current_version" to "Aktuelle Version",
        "auto_updates" to "Automatische Updates",
        "notify_updates" to "Bei neuen Versionen benachrichtigen",
        "check_updates" to "Nach Updates suchen",
        "update_available" to "Update verfügbar: ",
        "latest_version" to "Du bist auf dem neuesten Stand",
        "searching" to "Suchen...",
        "remove_favorite" to "Aus Favoriten entfernen",
        "add_favorite" to "Zu Favoriten hinzufügen",
        "remove_playlist" to "Aus dieser Playlist entfernen",
        "add_to_playlist" to "Zur Playlist hinzufügen",
        "change_cover" to "Cover ändern",
        "track_info" to "Titelinformationen",
        "track_info_title" to "Titel-Info",
        "title_label" to "Titel: ",
        "artist_label" to "Künstler: ",
        "yt_id_label" to "YouTube ID: ",
        "origin_label" to "Quelle: ",
        "local_cache" to "Lokaler Cache (Offline)",
        "streaming" to "Streaming",
        "network_quality" to "Netzwerkqualität: ",
        "close" to "Schließen",
        "cover_saved" to "Cover gespeichert.",
        "no_playlists_yet" to "Du hast keine Playlists.",
        "clear_cache_title" to "Cache leeren",
        "clear_cache_warning" to "Gespeicherte Tracks werden gelöscht. Fortfahren?",
        "no_connection_track" to "Keine Verbindung zum Abspielen.",
        "clear_history" to "Verlauf löschen",
        "clear_history_desc" to "Letzte Songs löschen",
        "history_cleared" to "Verlauf gelöscht",
        "downloading_update" to "Browser wird zum Herunterladen geöffnet...",
        "language" to "Sprache",
        "language_desc" to "App-Sprache ändern"
    )

    private val it = mapOf(
        "lang_system" to "Sistema (Predefinito)",
        "offline_title" to "Nessuna connessione internet",
        "offline_desc" to "Vai alla tua Libreria per ascoltare la musica in cache.",
        "theme_system" to "Sistema",
        "theme_dark" to "Scuro",
        "theme_light" to "Chiaro",
        "error_audio" to "Errore di caricamento audio",
        "now_playing" to "In riproduzione",
        "lyrics" to "Testi",
        "add_lyrics_manual" to "Aggiungi testi manualmente",
        "paste_lyrics_here" to "Incolla qui i testi (usa [00:00.00] per sincronizzare)",
        "save" to "Salva",
        "cancel" to "Annulla",
        "no_lyrics_found" to "Nessun testo trovato",
        "add_lyrics" to "Aggiungi testi",
        "play_queue" to "Coda di riproduzione",
        "new_playlist" to "Nuova Playlist",
        "playlist_name" to "Nome della playlist",
        "create" to "Crea",
        "rename_playlist" to "Rinomina Playlist",
        "new_name" to "Nuovo nome",
        "delete_playlist" to "Elimina Playlist",
        "delete_playlist_confirm" to "Sei sicuro di voler eliminare questa playlist?",
        "delete" to "Elimina",
        "your_library" to "La tua Libreria",
        "favorites" to "Preferiti",
        "playlists" to "Playlist",
        "no_favorites_yet" to "Non hai ancora preferiti",
        "tracks" to "tracce",
        "home" to "Home",
        "trending" to "Tendenze",
        "for_you" to "Per te",
        "recent" to "Recenti",
        "downloads" to "Download",
        "nothing_to_show" to "Niente da mostrare. Scorri verso il basso per aggiornare.",
        "no_recent_songs" to "Nessun brano recente.",
        "explore" to "Esplora",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "Nessun risultato",
        "settings" to "Impostazioni",
        "appearance" to "Aspetto",
        "appearance_desc" to "Tema chiaro/scuro e colori adattivi",
        "player_and_sound" to "Lettore e suono",
        "player_desc" to "Qualità, crossfade, volume",
        "storage_and_data" to "Archiviazione e dati",
        "storage_desc" to "Cache audio, immagini e rete",
        "about_hearon" to "Informazioni su HearOn",
        "adaptive_colors" to "Colori adattivi",
        "dynamic_theme" to "Tema dinamico",
        "dynamic_theme_desc" to "Estrai colori dalla copertina",
        "app_theme" to "Tema dell'app",
        "player" to "Lettore",
        "sound_quality" to "Qualità del suono",
        "quality_high" to "Alta",
        "quality_normal" to "Normale",
        "quality_low" to "Bassa",
        "crossfade" to "Crossfade",
        "crossfade_desc" to "Transizione fluida tra i brani",
        "crossfade_duration" to "Durata crossfade",
        "disabled" to "Disabilitato",
        "audio_normalization" to "Normalizzazione audio",
        "audio_normalization_desc" to "Uniforma il volume",
        "invisible_audio_cache" to "Cache audio invisibile",
        "auto_saved_music" to "Musica salvata automaticamente",
        "clear_audio_cache" to "Svuota cache audio",
        "clear_audio_cache_desc" to "Libera spazio eliminando file",
        "images_and_data" to "Immagini e Dati",
        "cover_limit" to "Limite copertine",
        "clear_covers" to "Cancella copertine",
        "clear_covers_desc" to "Elimina immagini in cache",
        "data_saver" to "Risparmio dati",
        "data_saver_desc" to "Carica immagini in bassa risoluzione",
        "about" to "Informazioni",
        "version_and_updates" to "Versione e aggiornamenti",
        "current_version" to "Versione attuale",
        "auto_updates" to "Aggiornamenti automatici",
        "notify_updates" to "Notificami le nuove versioni",
        "check_updates" to "Cerca aggiornamenti",
        "update_available" to "Aggiornamento disponibile: ",
        "latest_version" to "Sei all'ultima versione",
        "searching" to "Ricerca...",
        "remove_favorite" to "Rimuovi dai preferiti",
        "add_favorite" to "Aggiungi ai preferiti",
        "remove_playlist" to "Rimuovi da questa playlist",
        "add_to_playlist" to "Aggiungi alla playlist",
        "change_cover" to "Cambia copertina",
        "track_info" to "Informazioni traccia",
        "track_info_title" to "Info traccia",
        "title_label" to "Titolo: ",
        "artist_label" to "Artista: ",
        "yt_id_label" to "ID YouTube: ",
        "origin_label" to "Origine: ",
        "local_cache" to "Cache locale (Offline)",
        "streaming" to "Streaming",
        "network_quality" to "Qualità rete: ",
        "close" to "Chiudi",
        "cover_saved" to "Copertina salvata.",
        "no_playlists_yet" to "Non hai playlist.",
        "clear_cache_title" to "Svuota cache",
        "clear_cache_warning" to "Le tracce verranno eliminate e consumeranno dati alla prossima riproduzione. Continuare?",
        "no_connection_track" to "Nessuna connessione per riprodurre questa traccia.",
        "clear_history" to "Cancella cronologia",
        "clear_history_desc" to "Elimina brani recenti",
        "history_cleared" to "Cronologia cancellata",
        "downloading_update" to "Apertura del browser per il download...",
        "language" to "Lingua",
        "language_desc" to "Cambia la lingua dell'app"
    )

    private val pt = mapOf(
        "lang_system" to "Sistema (Padrão)",
        "offline_title" to "Sem conexão à internet",
        "offline_desc" to "Vá para sua Biblioteca para ouvir música em cache.",
        "theme_system" to "Sistema",
        "theme_dark" to "Escuro",
        "theme_light" to "Claro",
        "error_audio" to "Erro ao carregar o áudio",
        "now_playing" to "A reproduzir",
        "lyrics" to "Letras",
        "add_lyrics_manual" to "Adicionar letras manualmente",
        "paste_lyrics_here" to "Cole as letras aqui (pode incluir [00:00.00] para sincronizar)",
        "save" to "Guardar",
        "cancel" to "Cancelar",
        "no_lyrics_found" to "Nenhum texto encontrado",
        "add_lyrics" to "Adicionar letras",
        "play_queue" to "Fila de Reprodução",
        "new_playlist" to "Nova Playlist",
        "playlist_name" to "Nome da playlist",
        "create" to "Criar",
        "rename_playlist" to "Mudar o nome",
        "new_name" to "Novo nome",
        "delete_playlist" to "Eliminar Playlist",
        "delete_playlist_confirm" to "Tem a certeza que quer eliminar a playlist?",
        "delete" to "Eliminar",
        "your_library" to "A sua Biblioteca",
        "favorites" to "Favoritos",
        "playlists" to "Playlists",
        "no_favorites_yet" to "Ainda não tem favoritos",
        "tracks" to "faixas",
        "home" to "Início",
        "trending" to "Tendências",
        "for_you" to "Para ti",
        "recent" to "Recentes",
        "downloads" to "Downloads",
        "nothing_to_show" to "Nada a mostrar. Deslize para atualizar.",
        "no_recent_songs" to "Nenhuma música recente.",
        "explore" to "Explorar",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "Nenhum resultado",
        "settings" to "Definições",
        "appearance" to "Aparência",
        "appearance_desc" to "Tema claro/escuro e cores adaptativas",
        "player_and_sound" to "Leitor e som",
        "player_desc" to "Qualidade, crossfade, volume",
        "storage_and_data" to "Armazenamento e Dados",
        "storage_desc" to "Cache de áudio, imagens e rede",
        "about_hearon" to "Sobre HearOn",
        "adaptive_colors" to "Cores adaptativas",
        "dynamic_theme" to "Tema dinâmico",
        "dynamic_theme_desc" to "Extrair cores da capa da música",
        "app_theme" to "Tema da aplicação",
        "player" to "Leitor",
        "sound_quality" to "Qualidade de som",
        "quality_high" to "Alta",
        "quality_normal" to "Normal",
        "quality_low" to "Baixa",
        "crossfade" to "Crossfade",
        "crossfade_desc" to "Transição suave entre canções",
        "crossfade_duration" to "Duração do crossfade",
        "disabled" to "Desativado",
        "audio_normalization" to "Normalização de áudio",
        "audio_normalization_desc" to "Igualar o volume",
        "invisible_audio_cache" to "Cache de áudio invisível",
        "auto_saved_music" to "Música guardada automaticamente",
        "clear_audio_cache" to "Limpar cache de áudio",
        "clear_audio_cache_desc" to "Libertar espaço apagando ficheiros",
        "images_and_data" to "Imagens e Dados",
        "cover_limit" to "Limite de capas",
        "clear_covers" to "Limpar capas",
        "clear_covers_desc" to "Apagar imagens em cache",
        "data_saver" to "Poupança de dados",
        "data_saver_desc" to "Carregar imagens em baixa resolução",
        "about" to "Sobre",
        "version_and_updates" to "Versão e Atualizações",
        "current_version" to "Versão atual",
        "auto_updates" to "Atualizações automáticas",
        "notify_updates" to "Notificar-me de novas versões",
        "check_updates" to "Procurar atualizações",
        "update_available" to "Atualização disponível: ",
        "latest_version" to "Tem a versão mais recente",
        "searching" to "A pesquisar...",
        "remove_favorite" to "Remover dos favoritos",
        "add_favorite" to "Adicionar aos favoritos",
        "remove_playlist" to "Remover desta playlist",
        "add_to_playlist" to "Adicionar à playlist",
        "change_cover" to "Alterar capa",
        "track_info" to "Informações da faixa",
        "track_info_title" to "Info da faixa",
        "title_label" to "Título: ",
        "artist_label" to "Artista: ",
        "yt_id_label" to "ID YouTube: ",
        "origin_label" to "Origem: ",
        "local_cache" to "Cache Local (Offline)",
        "streaming" to "Streaming",
        "network_quality" to "Qualidade da rede: ",
        "close" to "Fechar",
        "cover_saved" to "Capa guardada.",
        "no_playlists_yet" to "Não tem playlists.",
        "clear_cache_title" to "Limpar cache",
        "clear_cache_warning" to "As faixas áudio serão apagadas. Continuar?",
        "no_connection_track" to "Sem ligação para reproduzir.",
        "clear_history" to "Limpar histórico",
        "clear_history_desc" to "Apagar músicas recentes",
        "history_cleared" to "Histórico apagado",
        "downloading_update" to "A abrir o navegador para transferir...",
        "language" to "Idioma",
        "language_desc" to "Alterar o idioma da aplicação"
    )

    private val ru = mapOf(
        "lang_system" to "Системный (По умолчанию)",
        "offline_title" to "Нет подключения к интернету",
        "offline_desc" to "Перейдите в медиатеку для прослушивания кэшированной музыки.",
        "theme_system" to "Системная",
        "theme_dark" to "Тёмная",
        "theme_light" to "Светлая",
        "error_audio" to "Ошибка загрузки аудио",
        "now_playing" to "Сейчас играет",
        "lyrics" to "Текст",
        "add_lyrics_manual" to "Добавить текст вручную",
        "paste_lyrics_here" to "Вставьте текст (с [00:00.00] для синхронизации)",
        "save" to "Сохранить",
        "cancel" to "Отмена",
        "no_lyrics_found" to "Текст не найден",
        "add_lyrics" to "Добавить текст",
        "play_queue" to "Очередь",
        "new_playlist" to "Новый плейлист",
        "playlist_name" to "Название плейлиста",
        "create" to "Создать",
        "rename_playlist" to "Переименовать",
        "new_name" to "Новое имя",
        "delete_playlist" to "Удалить плейлист",
        "delete_playlist_confirm" to "Вы уверены, что хотите удалить?",
        "delete" to "Удалить",
        "your_library" to "Медиатека",
        "favorites" to "Избранное",
        "playlists" to "Плейлисты",
        "no_favorites_yet" to "Нет избранных треков",
        "tracks" to "треков",
        "home" to "Главная",
        "trending" to "В тренде",
        "for_you" to "Для вас",
        "recent" to "Недавние",
        "downloads" to "Загрузки",
        "nothing_to_show" to "Нечего показать. Потяните вниз для обновления.",
        "no_recent_songs" to "Здесь пока пусто.",
        "explore" to "Обзор",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "Нет результатов",
        "settings" to "Настройки",
        "appearance" to "Внешний вид",
        "appearance_desc" to "Светлая/тёмная тема и цвета",
        "player_and_sound" to "Плеер и звук",
        "player_desc" to "Качество, плавный переход, громкость",
        "storage_and_data" to "Память и данные",
        "storage_desc" to "Кэш аудио и сеть",
        "about_hearon" to "О HearOn",
        "adaptive_colors" to "Адаптивные цвета",
        "dynamic_theme" to "Динамическая тема",
        "dynamic_theme_desc" to "Цвета на основе обложки",
        "app_theme" to "Тема приложения",
        "player" to "Плеер",
        "sound_quality" to "Качество звука",
        "quality_high" to "Высокое",
        "quality_normal" to "Обычное",
        "quality_low" to "Низкое",
        "crossfade" to "Плавный переход",
        "crossfade_desc" to "Плавная смена треков",
        "crossfade_duration" to "Длительность перехода",
        "disabled" to "Отключено",
        "audio_normalization" to "Нормализация звука",
        "audio_normalization_desc" to "Выравнивание громкости",
        "invisible_audio_cache" to "Невидимый кэш",
        "auto_saved_music" to "Автосохраненная музыка",
        "clear_audio_cache" to "Очистить кэш аудио",
        "clear_audio_cache_desc" to "Освободить место",
        "images_and_data" to "Изображения и данные",
        "cover_limit" to "Лимит обложек",
        "clear_covers" to "Очистить обложки",
        "clear_covers_desc" to "Удалить кэш изображений",
        "data_saver" to "Экономия данных",
        "data_saver_desc" to "Низкое качество изображений",
        "about" to "О приложении",
        "version_and_updates" to "Версия и обновления",
        "current_version" to "Текущая версия",
        "auto_updates" to "Автообновление",
        "notify_updates" to "Уведомлять о новых версиях",
        "check_updates" to "Проверить обновления",
        "update_available" to "Доступно обновление: ",
        "latest_version" to "У вас последняя версия",
        "searching" to "Поиск...",
        "remove_favorite" to "Удалить из избранного",
        "add_favorite" to "В избранное",
        "remove_playlist" to "Удалить из плейлиста",
        "add_to_playlist" to "Добавить в плейлист",
        "change_cover" to "Изменить обложку",
        "track_info" to "Информация о треке",
        "track_info_title" to "Инфо о треке",
        "title_label" to "Название: ",
        "artist_label" to "Исполнитель: ",
        "yt_id_label" to "YouTube ID: ",
        "origin_label" to "Источник: ",
        "local_cache" to "Локальный кэш (Офлайн)",
        "streaming" to "Стриминг",
        "network_quality" to "Качество сети: ",
        "close" to "Закрыть",
        "cover_saved" to "Обложка сохранена.",
        "no_playlists_yet" to "У вас нет плейлистов.",
        "clear_cache_title" to "Очистить кэш",
        "clear_cache_warning" to "Сохраненные треки будут удалены. Продолжить?",
        "no_connection_track" to "Нет сети для воспроизведения.",
        "clear_history" to "Очистить историю",
        "clear_history_desc" to "Удалить недавние треки",
        "history_cleared" to "История очищена",
        "downloading_update" to "Открытие браузера для скачивания...",
        "language" to "Язык",
        "language_desc" to "Изменить язык приложения"
    )

    private val ja = mapOf(
        "lang_system" to "システム (デフォルト)",
        "offline_title" to "インターネット接続がありません",
        "offline_desc" to "キャッシュされた音楽を聴くにはライブラリへ移動してください。",
        "theme_system" to "システム",
        "theme_dark" to "ダーク",
        "theme_light" to "ライト",
        "error_audio" to "オーディオの読み込みエラー",
        "now_playing" to "再生中",
        "lyrics" to "歌詞",
        "add_lyrics_manual" to "手動で歌詞を追加",
        "paste_lyrics_here" to "ここに歌詞を貼り付け（[00:00.00]で同期可能）",
        "save" to "保存",
        "cancel" to "キャンセル",
        "no_lyrics_found" to "歌詞が見つかりません",
        "add_lyrics" to "歌詞を追加",
        "play_queue" to "再生キュー",
        "new_playlist" to "新しいプレイリスト",
        "playlist_name" to "プレイリスト名",
        "create" to "作成",
        "rename_playlist" to "名前を変更",
        "new_name" to "新しい名前",
        "delete_playlist" to "プレイリストを削除",
        "delete_playlist_confirm" to "本当に削除しますか？",
        "delete" to "削除",
        "your_library" to "マイライブラリ",
        "favorites" to "お気に入り",
        "playlists" to "プレイリスト",
        "no_favorites_yet" to "まだお気に入りがありません",
        "tracks" to "曲",
        "home" to "ホーム",
        "trending" to "トレンド",
        "for_you" to "おすすめ",
        "recent" to "最近",
        "downloads" to "ダウンロード",
        "nothing_to_show" to "表示するものがありません。下にスワイプして更新。",
        "no_recent_songs" to "最近の曲はありません。",
        "explore" to "探索",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "結果がありません",
        "settings" to "設定",
        "appearance" to "外観",
        "appearance_desc" to "テーマとカラー",
        "player_and_sound" to "プレイヤーとサウンド",
        "player_desc" to "品質、クロスフェード、音量",
        "storage_and_data" to "ストレージとデータ",
        "storage_desc" to "キャッシュとネットワーク",
        "about_hearon" to "HearOnについて",
        "adaptive_colors" to "アダプティブカラー",
        "dynamic_theme" to "ダイナミックテーマ",
        "dynamic_theme_desc" to "カバーから色を抽出",
        "app_theme" to "アプリのテーマ",
        "player" to "プレイヤー",
        "sound_quality" to "音質",
        "quality_high" to "高",
        "quality_normal" to "標準",
        "quality_low" to "低",
        "crossfade" to "クロスフェード",
        "crossfade_desc" to "曲間のスムーズな移行",
        "crossfade_duration" to "クロスフェードの時間",
        "disabled" to "無効",
        "audio_normalization" to "オーディオの正規化",
        "audio_normalization_desc" to "音量を均一にする",
        "invisible_audio_cache" to "オーディオキャッシュ",
        "auto_saved_music" to "自動保存された音楽",
        "clear_audio_cache" to "キャッシュをクリア",
        "clear_audio_cache_desc" to "ファイルを削除して空き容量を増やす",
        "images_and_data" to "画像とデータ",
        "cover_limit" to "カバーの制限",
        "clear_covers" to "カバーをクリア",
        "clear_covers_desc" to "キャッシュされた画像を削除",
        "data_saver" to "データセーバー",
        "data_saver_desc" to "低解像度で画像を読み込む",
        "about" to "情報",
        "version_and_updates" to "バージョンと更新",
        "current_version" to "現在のバージョン",
        "auto_updates" to "自動更新",
        "notify_updates" to "新しいバージョンを通知する",
        "check_updates" to "更新を確認",
        "update_available" to "利用可能な更新: ",
        "latest_version" to "最新バージョンです",
        "searching" to "検索中...",
        "remove_favorite" to "お気に入りから削除",
        "add_favorite" to "お気に入りに追加",
        "remove_playlist" to "プレイリストから削除",
        "add_to_playlist" to "プレイリストに追加",
        "change_cover" to "カバーを変更",
        "track_info" to "曲の情報",
        "track_info_title" to "曲情報",
        "title_label" to "タイトル: ",
        "artist_label" to "アーティスト: ",
        "yt_id_label" to "YouTube ID: ",
        "origin_label" to "ソース: ",
        "local_cache" to "ローカルキャッシュ",
        "streaming" to "ストリーミング",
        "network_quality" to "ネットワーク品質: ",
        "close" to "閉じる",
        "cover_saved" to "カバーが保存されました。",
        "no_playlists_yet" to "プレイリストがありません。",
        "clear_cache_title" to "キャッシュをクリア",
        "clear_cache_warning" to "保存された曲が削除されます。続行しますか？",
        "no_connection_track" to "再生するネットワークがありません。",
        "clear_history" to "履歴を消去",
        "clear_history_desc" to "最近の曲を削除",
        "history_cleared" to "履歴を消去しました",
        "downloading_update" to "ブラウザを開いてダウンロードしています...",
        "language" to "言語",
        "language_desc" to "アプリの言語を変更"
    )

    private val zh = mapOf(
        "lang_system" to "系统 (默认)",
        "offline_title" to "无网络连接",
        "offline_desc" to "前往您的音乐库收听缓存的音乐。",
        "theme_system" to "系统",
        "theme_dark" to "深色",
        "theme_light" to "浅色",
        "error_audio" to "音频加载失败",
        "now_playing" to "正在播放",
        "lyrics" to "歌词",
        "add_lyrics_manual" to "手动添加歌词",
        "paste_lyrics_here" to "在此处粘贴歌词 (包含 [00:00.00] 即可同步)",
        "save" to "保存",
        "cancel" to "取消",
        "no_lyrics_found" to "未找到歌词",
        "add_lyrics" to "添加歌词",
        "play_queue" to "播放队列",
        "new_playlist" to "新建播放列表",
        "playlist_name" to "播放列表名称",
        "create" to "创建",
        "rename_playlist" to "重命名",
        "new_name" to "新名称",
        "delete_playlist" to "删除播放列表",
        "delete_playlist_confirm" to "您确定要删除吗？",
        "delete" to "删除",
        "your_library" to "音乐库",
        "favorites" to "收藏",
        "playlists" to "播放列表",
        "no_favorites_yet" to "暂无收藏",
        "tracks" to "首歌曲",
        "home" to "首页",
        "trending" to "热门",
        "for_you" to "为你推荐",
        "recent" to "最近播放",
        "downloads" to "下载",
        "nothing_to_show" to "没有内容。向下滑动刷新。",
        "no_recent_songs" to "暂无最近播放。",
        "explore" to "探索",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "无结果",
        "settings" to "设置",
        "appearance" to "外观",
        "appearance_desc" to "主题和自适应颜色",
        "player_and_sound" to "播放器和声音",
        "player_desc" to "音质、淡入淡出、音量",
        "storage_and_data" to "存储和数据",
        "storage_desc" to "音频缓存和网络",
        "about_hearon" to "关于 HearOn",
        "adaptive_colors" to "自适应颜色",
        "dynamic_theme" to "动态主题",
        "dynamic_theme_desc" to "从封面提取颜色",
        "app_theme" to "应用主题",
        "player" to "播放器",
        "sound_quality" to "音质",
        "quality_high" to "高",
        "quality_normal" to "正常",
        "quality_low" to "低",
        "crossfade" to "淡入淡出",
        "crossfade_desc" to "歌曲之间的平滑过渡",
        "crossfade_duration" to "淡入淡出持续时间",
        "disabled" to "已禁用",
        "audio_normalization" to "音频标准化",
        "audio_normalization_desc" to "均衡音量",
        "invisible_audio_cache" to "音频缓存",
        "auto_saved_music" to "自动保存的音乐",
        "clear_audio_cache" to "清除音频缓存",
        "clear_audio_cache_desc" to "释放存储空间",
        "images_and_data" to "图像和数据",
        "cover_limit" to "封面限制",
        "clear_covers" to "清除封面",
        "clear_covers_desc" to "删除缓存的图像",
        "data_saver" to "省流模式",
        "data_saver_desc" to "加载低分辨率图像",
        "about" to "关于",
        "version_and_updates" to "版本和更新",
        "current_version" to "当前版本",
        "auto_updates" to "自动更新",
        "notify_updates" to "通知我新版本",
        "check_updates" to "检查更新",
        "update_available" to "可用更新: ",
        "latest_version" to "您使用的是最新版本",
        "searching" to "搜索中...",
        "remove_favorite" to "取消收藏",
        "add_favorite" to "添加到收藏",
        "remove_playlist" to "从列表中移除",
        "add_to_playlist" to "添加到播放列表",
        "change_cover" to "更改封面",
        "track_info" to "歌曲信息",
        "track_info_title" to "信息",
        "title_label" to "标题: ",
        "artist_label" to "艺术家: ",
        "yt_id_label" to "YouTube ID: ",
        "origin_label" to "来源: ",
        "local_cache" to "本地缓存 (离线)",
        "streaming" to "流媒体",
        "network_quality" to "网络质量: ",
        "close" to "关闭",
        "cover_saved" to "封面已保存。",
        "no_playlists_yet" to "您没有播放列表。",
        "clear_cache_title" to "清除缓存",
        "clear_cache_warning" to "保存的歌曲将被删除。是否继续？",
        "no_connection_track" to "无网络连接，无法播放。",
        "clear_history" to "清除历史记录",
        "clear_history_desc" to "删除最近播放的歌曲",
        "history_cleared" to "历史记录已清除",
        "downloading_update" to "正在打开浏览器下载...",
        "language" to "语言",
        "language_desc" to "更改应用语言"
    )

    private val hi = mapOf(
        "lang_system" to "सिस्टम (डिफ़ॉल्ट)",
        "offline_title" to "कोई इंटरनेट कनेक्शन नहीं",
        "offline_desc" to "कैश किए गए संगीत को सुनने के लिए अपनी लाइब्रेरी में जाएं।",
        "theme_system" to "सिस्टम",
        "theme_dark" to "डार्क",
        "theme_light" to "लाइट",
        "error_audio" to "ऑडियो लोड करने में त्रुटि",
        "now_playing" to "अब बज रहा है",
        "lyrics" to "गीत के बोल",
        "add_lyrics_manual" to "मैन्युअल रूप से बोल जोड़ें",
        "paste_lyrics_here" to "यहां बोल चिपकाएं",
        "save" to "सहेजें",
        "cancel" to "रद्द करें",
        "no_lyrics_found" to "कोई बोल नहीं मिला",
        "add_lyrics" to "बोल जोड़ें",
        "play_queue" to "कतार",
        "new_playlist" to "नई प्लेलिस्ट",
        "playlist_name" to "प्लेलिस्ट का नाम",
        "create" to "बनाएं",
        "rename_playlist" to "नाम बदलें",
        "new_name" to "नया नाम",
        "delete_playlist" to "प्लेलिस्ट हटाएं",
        "delete_playlist_confirm" to "क्या आप वाकई हटाना चाहते हैं?",
        "delete" to "हटाएं",
        "your_library" to "आपकी लाइब्रेरी",
        "favorites" to "पसंदीदा",
        "playlists" to "प्लेलिस्ट",
        "no_favorites_yet" to "अभी तक कोई पसंदीदा नहीं",
        "tracks" to "गाने",
        "home" to "होम",
        "trending" to "ट्रेंडिंग",
        "for_you" to "आपके लिए",
        "recent" to "हाल ही में",
        "downloads" to "डाउनलोड",
        "nothing_to_show" to "दिखाने के लिए कुछ नहीं। रीफ़्रेश करने के लिए नीचे स्वाइप करें।",
        "no_recent_songs" to "यहाँ कोई गाना नहीं है।",
        "explore" to "खोजें",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "कोई परिणाम नहीं",
        "settings" to "सेटिंग्स",
        "appearance" to "दिखावट",
        "appearance_desc" to "लाइट/डार्क थीम और रंग",
        "player_and_sound" to "प्लेयर और ध्वनि",
        "player_desc" to "गुणवत्ता, क्रॉसफ़ेड, वॉल्यूम",
        "storage_and_data" to "स्टोरेज और डेटा",
        "storage_desc" to "ऑडियो कैश और नेटवर्क",
        "about_hearon" to "HearOn के बारे में",
        "adaptive_colors" to "अनुकूली रंग",
        "dynamic_theme" to "डायनामिक थीम",
        "dynamic_theme_desc" to "कवर से रंग निकालें",
        "app_theme" to "ऐप थीम",
        "player" to "प्लेयर",
        "sound_quality" to "ध्वनि की गुणवत्ता",
        "quality_high" to "उच्च",
        "quality_normal" to "सामान्य",
        "quality_low" to "कम",
        "crossfade" to "क्रॉसफ़ेड",
        "crossfade_desc" to "गानों के बीच आसान बदलाव",
        "crossfade_duration" to "क्रॉसफ़ेड अवधि",
        "disabled" to "अक्षम",
        "audio_normalization" to "ऑडियो नॉर्मलाइज़ेशन",
        "audio_normalization_desc" to "वॉल्यूम बराबर करें",
        "invisible_audio_cache" to "ऑडियो कैश",
        "auto_saved_music" to "स्वचालित रूप से सहेजा गया संगीत",
        "clear_audio_cache" to "कैश साफ़ करें",
        "clear_audio_cache_desc" to "जगह खाली करें",
        "images_and_data" to "चित्र और डेटा",
        "cover_limit" to "कवर सीमा",
        "clear_covers" to "कवर साफ़ करें",
        "clear_covers_desc" to "कैश की गई छवियां हटाएं",
        "data_saver" to "डेटा सेवर",
        "data_saver_desc" to "कम रिज़ॉल्यूशन में लोड करें",
        "about" to "बारे में",
        "version_and_updates" to "संस्करण और अपडेट",
        "current_version" to "वर्तमान संस्करण",
        "auto_updates" to "स्वचालित अपडेट",
        "notify_updates" to "नए संस्करणों की सूचना दें",
        "check_updates" to "अपडेट की जांच करें",
        "update_available" to "अपडेट उपलब्ध: ",
        "latest_version" to "आपके पास नवीनतम संस्करण है",
        "searching" to "खोज रहा है...",
        "remove_favorite" to "पसंदीदा से हटाएं",
        "add_favorite" to "पसंदीदा में जोड़ें",
        "remove_playlist" to "इस प्लेलिस्ट से हटाएं",
        "add_to_playlist" to "प्लेलिस्ट में जोड़ें",
        "change_cover" to "कवर बदलें",
        "track_info" to "गाने की जानकारी",
        "track_info_title" to "जानकारी",
        "title_label" to "शीर्षक: ",
        "artist_label" to "कलाकार: ",
        "yt_id_label" to "YouTube ID: ",
        "origin_label" to "स्रोत: ",
        "local_cache" to "लोकल कैश (ऑफ़लाइन)",
        "streaming" to "स्ट्रीमिंग",
        "network_quality" to "नेटवर्क गुणवत्ता: ",
        "close" to "बंद करें",
        "cover_saved" to "कवर सहेजा गया।",
        "no_playlists_yet" to "आपके पास कोई प्लेलिस्ट नहीं है।",
        "clear_cache_title" to "कैश साफ़ करें",
        "clear_cache_warning" to "सहेजे गए गाने हटा दिए जाएंगे। जारी रखें?",
        "no_connection_track" to "बजाने के लिए कोई नेटवर्क नहीं।",
        "clear_history" to "इतिहास मिटाएं",
        "clear_history_desc" to "हाल के गाने हटाएं",
        "history_cleared" to "इतिहास मिटा दिया गया",
        "downloading_update" to "डाउनलोड करने के लिए ब्राउज़र खोला जा रहा है...",
        "language" to "भाषा",
        "language_desc" to "ऐप की भाषा बदलें"
    )

    private val ar = mapOf(
        "lang_system" to "النظام (الافتراضي)",
        "offline_title" to "لا يوجد اتصال بالإنترنت",
        "offline_desc" to "انتقل إلى مكتبتك للاستماع إلى الموسيقى المحفوظة.",
        "theme_system" to "النظام",
        "theme_dark" to "داكن",
        "theme_light" to "فاتح",
        "error_audio" to "خطأ في تحميل الصوت",
        "now_playing" to "يتم التشغيل الآن",
        "lyrics" to "كلمات",
        "add_lyrics_manual" to "إضافة كلمات يدويا",
        "paste_lyrics_here" to "الصق الكلمات هنا (يمكنك إضافة [00:00.00] للمزامنة)",
        "save" to "حفظ",
        "cancel" to "إلغاء",
        "no_lyrics_found" to "لم يتم العثور على كلمات",
        "add_lyrics" to "إضافة كلمات",
        "play_queue" to "قائمة التشغيل",
        "new_playlist" to "قائمة جديدة",
        "playlist_name" to "اسم القائمة",
        "create" to "إنشاء",
        "rename_playlist" to "إعادة تسمية",
        "new_name" to "اسم جديد",
        "delete_playlist" to "حذف القائمة",
        "delete_playlist_confirm" to "هل أنت متأكد أنك تريد الحذف؟",
        "delete" to "حذف",
        "your_library" to "مكتبتك",
        "favorites" to "المفضلة",
        "playlists" to "قوائم التشغيل",
        "no_favorites_yet" to "لا يوجد مفضلة حتى الآن",
        "tracks" to "مقاطع",
        "home" to "الرئيسية",
        "trending" to "رائج",
        "for_you" to "لك",
        "recent" to "الأخيرة",
        "downloads" to "التنزيلات",
        "nothing_to_show" to "لا يوجد شيء لعرضه. اسحب للتحديث.",
        "no_recent_songs" to "لا توجد أغاني هنا.",
        "explore" to "استكشاف",
        "yt_music" to "YT Music",
        "youtube" to "YouTube",
        "no_results" to "لا توجد نتائج",
        "settings" to "إعدادات",
        "appearance" to "المظهر",
        "appearance_desc" to "مظهر داكن/فاتح وألوان",
        "player_and_sound" to "المشغل والصوت",
        "player_desc" to "الجودة، التلاشي، الحجم",
        "storage_and_data" to "التخزين والبيانات",
        "storage_desc" to "ذاكرة التخزين المؤقت والشبكة",
        "about_hearon" to "حول HearOn",
        "adaptive_colors" to "ألوان متكيفة",
        "dynamic_theme" to "مظهر ديناميكي",
        "dynamic_theme_desc" to "استخراج الألوان من الغلاف",
        "app_theme" to "مظهر التطبيق",
        "player" to "المشغل",
        "sound_quality" to "جودة الصوت",
        "quality_high" to "عالية",
        "quality_normal" to "عادية",
        "quality_low" to "منخفضة",
        "crossfade" to "تلاشي",
        "crossfade_desc" to "انتقال سلس بين الأغاني",
        "crossfade_duration" to "مدة التلاشي",
        "disabled" to "معطل",
        "audio_normalization" to "تطبيع الصوت",
        "audio_normalization_desc" to "معادلة الحجم",
        "invisible_audio_cache" to "ذاكرة التخزين المؤقت",
        "auto_saved_music" to "موسيقى محفوظة تلقائيا",
        "clear_audio_cache" to "مسح الذاكرة المؤقتة",
        "clear_audio_cache_desc" to "تحرير المساحة",
        "images_and_data" to "الصور والبيانات",
        "cover_limit" to "حد الأغلفة",
        "clear_covers" to "مسح الأغلفة",
        "clear_covers_desc" to "حذف الصور المؤقتة",
        "data_saver" to "توفير البيانات",
        "data_saver_desc" to "تحميل بدقة منخفضة",
        "about" to "حول",
        "version_and_updates" to "الإصدار والتحديثات",
        "current_version" to "الإصدار الحالي",
        "auto_updates" to "تحديثات تلقائية",
        "notify_updates" to "إعلامي بالنسخ الجديدة",
        "check_updates" to "التحقق من التحديثات",
        "update_available" to "تحديث متاح: ",
        "latest_version" to "لديك أحدث إصدار",
        "searching" to "جارٍ البحث...",
        "remove_favorite" to "إزالة من المفضلة",
        "add_favorite" to "إضافة للمفضلة",
        "remove_playlist" to "إزالة من القائمة",
        "add_to_playlist" to "إضافة إلى القائمة",
        "change_cover" to "تغيير الغلاف",
        "track_info" to "معلومات المقطع",
        "track_info_title" to "معلومات",
        "title_label" to "العنوان: ",
        "artist_label" to "الفنان: ",
        "yt_id_label" to "YouTube ID: ",
        "origin_label" to "المصدر: ",
        "local_cache" to "تخزين محلي (بدون إنترنت)",
        "streaming" to "بث",
        "network_quality" to "جودة الشبكة: ",
        "close" to "إغلاق",
        "cover_saved" to "تم حفظ الغلاف.",
        "no_playlists_yet" to "ليس لديك قوائم تشغيل.",
        "clear_cache_title" to "مسح الذاكرة المؤقتة",
        "clear_cache_warning" to "سيتم حذف الأغاني المحفوظة. هل تريد الاستمرار؟",
        "no_connection_track" to "لا يوجد اتصال للتشغيل.",
        "clear_history" to "مسح السجل",
        "clear_history_desc" to "حذف الأغاني الأخيرة",
        "history_cleared" to "تم مسح السجل",
        "downloading_update" to "فتح المتصفح للتحميل...",
        "language" to "اللغة",
        "language_desc" to "تغيير لغة التطبيق"
    )

    fun get(key: String, lang: String): String {
        val resolvedLang = if (lang == "system") Locale.getDefault().language else lang
        val map = when (resolvedLang) {
            "es" -> es
            "en" -> en
            "fr" -> fr
            "de" -> de
            "it" -> it
            "pt" -> pt
            "ru" -> ru
            "ja" -> ja
            "zh" -> zh
            "hi" -> hi
            "ar" -> ar
            else -> en
        }
        return map[key] ?: en[key] ?: key
    }
}

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
fun OfflineScreen(lang: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = L.get("offline_title", lang),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = L.get("offline_desc", lang),
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

            var themeMode by remember { mutableStateOf(prefs.getString("theme", "System") ?: "System") }
            var isDynamicColorEnabled by remember { mutableStateOf(prefs.getBoolean("dynamic_colors", true)) }
            var dominantColor by remember { mutableStateOf<Color?>(null) }
            var appLang by remember { mutableStateOf(prefs.getString("app_lang", "system") ?: "system") }

            val isDark = when (themeMode) {
                "Oscuro", "Dark" -> true
                "Claro", "Light" -> false
                else -> isSystemInDarkTheme()
            }

            val baseColorScheme = if (isDark) darkColorScheme() else lightColorScheme()
            val defaultText = if (isDark) Color.White else Color.Black
            val defaultTextVariant = if (isDark) Color.LightGray else Color.DarkGray

            val animPrimary by animateColorAsState(targetValue = if (isDynamicColorEnabled && dominantColor != null) dominantColor!! else baseColorScheme.primary, animationSpec = tween(1000), label = "")
            val animPrimaryContainer by animateColorAsState(targetValue = if (isDynamicColorEnabled && dominantColor != null) dominantColor!!.copy(alpha = if (isDark) 0.4f else 0.3f) else baseColorScheme.primaryContainer, animationSpec = tween(1000), label = "")
            val animSecondaryContainer by animateColorAsState(targetValue = if (isDynamicColorEnabled && dominantColor != null) dominantColor!!.copy(alpha = if (isDark) 0.2f else 0.15f) else baseColorScheme.secondaryContainer, animationSpec = tween(1000), label = "")
            val animSurface by animateColorAsState(targetValue = if (isDynamicColorEnabled && dominantColor != null) Color(androidx.core.graphics.ColorUtils.blendARGB(baseColorScheme.surface.toArgb(), dominantColor!!.toArgb(), if (isDark) 0.08f else 0.1f)) else baseColorScheme.surface, animationSpec = tween(1000), label = "")
            val animSurfaceHigh by animateColorAsState(targetValue = if (isDynamicColorEnabled && dominantColor != null) Color(androidx.core.graphics.ColorUtils.blendARGB(baseColorScheme.surfaceContainerHigh.toArgb(), dominantColor!!.toArgb(), if (isDark) 0.15f else 0.2f)) else baseColorScheme.surfaceContainerHigh, animationSpec = tween(1000), label = "")

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
                        onThemeChange = { themeMode = it; prefs.edit().putString("theme", it).apply() },
                        isDynamicColorEnabled = isDynamicColorEnabled,
                        onDynamicColorToggle = { isDynamicColorEnabled = it; prefs.edit().putBoolean("dynamic_colors", it).apply() },
                        onColorExtracted = { dominantColor = it },
                        lang = appLang,
                        onLangChange = { appLang = it; prefs.edit().putString("app_lang", it).apply() }
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
    val alpha1 by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(400), repeatMode = RepeatMode.Reverse), label = "d1")
    val alpha2 by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(400, delayMillis = 150), repeatMode = RepeatMode.Reverse), label = "d2")
    val alpha3 by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(400, delayMillis = 300), repeatMode = RepeatMode.Reverse), label = "d3")

    Row(modifier = Modifier.padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha1)))
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha2)))
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha3)))
    }
}

@Composable
fun ExpressiveChip(selected: Boolean, text: String, icon: ImageVector? = null, onClick: () -> Unit) {
    val bgColor by animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh, label = "")
    val contentColor by animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, label = "")
    val scale by animateFloatAsState(targetValue = if (selected) 1.05f else 1f, label = "")

    Surface(
        modifier = Modifier.scale(scale).clip(RoundedCornerShape(24.dp)).clickable(onClick = onClick),
        color = bgColor,
        tonalElevation = if (selected) 8.dp else 2.dp
    ) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
            }
            Text(text = text, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = contentColor)
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
    onColorExtracted: (Color?) -> Unit,
    lang: String,
    onLangChange: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val prefs = context.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    val scope = rememberCoroutineScope()

    var isAppOnline by remember { mutableStateOf(isOnline(context)) }
    LaunchedEffect(Unit) {
        while(true) {
            isAppOnline = isOnline(context)
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
            onColorExtracted(getDominantColor(currentTrack!!.coverUrl))
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
        if (lastId != null && isAppOnline) {
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
        val existingIndex = recentlyPlayed.indexOfFirst { it.id == t.id }
        if (existingIndex != -1) recentlyPlayed.removeAt(existingIndex)
        recentlyPlayed.add(0, t)
        if (recentlyPlayed.size > 50) recentlyPlayed.removeAt(recentlyPlayed.size - 1)
        prefs.edit().putStringSet("recent_tracks_data", recentlyPlayed.map { "${it.id}|||${it.title}|||${it.artist}|||${it.coverUrl}" }.toSet()).apply()
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
        prefs.edit().putStringSet("cached_tracks_data", verified.map { "${it.id}|||${it.title}|||${it.artist}|||${it.coverUrl}" }.toSet()).apply()
        refreshCacheCount()
    }

    LaunchedEffect(Unit) { onRefresh() }

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
                Toast.makeText(context, L.get("cover_saved", lang), Toast.LENGTH_SHORT).show()
            }
        }
        trackOptionsMenu = null
    }

    fun savePlaylists() {
        prefs.edit().putStringSet("playlists_data", playlists.map { p -> "${p.name}:::${p.tracks.joinToString(";;;") { t -> "${t.id}|||${t.title}|||${t.artist}|||${t.coverUrl}" }}" }.toSet()).apply()
    }

    fun saveLikedTracks() {
        prefs.edit().putStringSet("liked_tracks_data", likedTracks.map { "${it.id}|||${it.title}|||${it.artist}|||${it.coverUrl}" }.toSet()).apply()
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
        onDispose { activity?.removeOnNewIntentListener(listener) }
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

    val handleTrackSelect: (YtTrack) -> Unit = { track ->
        val isLocal = File(context.filesDir, "hearon_downloads/${track.id}.m4a").let { it.exists() && it.length() >= 50_000 }
        if (!isAppOnline && !isLocal) {
            Toast.makeText(context, L.get("no_connection_track", lang), Toast.LENGTH_SHORT).show()
        } else {
            currentTrack = track
            scope.launch {
                playTrack(track, player, context, prefs, lang)
                saveRecentTrack(track)
                if (isAppOnline) {
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
            val nextTrack = playQueue[currentQueueIndex + 1]
            val isLocal = File(context.filesDir, "hearon_downloads/${nextTrack.id}.m4a").let { it.exists() && it.length() >= 50_000 }
            if (!isAppOnline && !isLocal) {
                Toast.makeText(context, L.get("no_connection_track", lang), Toast.LENGTH_SHORT).show()
                player?.pause()
                return
            }
            currentQueueIndex++
            currentTrack = playQueue[currentQueueIndex]
            scope.launch {
                playTrack(currentTrack!!, player, context, prefs, lang)
                saveRecentTrack(currentTrack!!)
                prefetchNextTrack(currentQueueIndex)
                onRefresh()
            }
        } else if (repeatMode == Player.REPEAT_MODE_ALL && playQueue.isNotEmpty()) {
            val nextTrack = playQueue[0]
            val isLocal = File(context.filesDir, "hearon_downloads/${nextTrack.id}.m4a").let { it.exists() && it.length() >= 50_000 }
            if (!isAppOnline && !isLocal) {
                Toast.makeText(context, L.get("no_connection_track", lang), Toast.LENGTH_SHORT).show()
                player?.pause()
                return
            }
            currentQueueIndex = 0
            currentTrack = playQueue[currentQueueIndex]
            scope.launch {
                playTrack(currentTrack!!, player, context, prefs, lang)
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
            val prevTrack = playQueue[currentQueueIndex - 1]
            val isLocal = File(context.filesDir, "hearon_downloads/${prevTrack.id}.m4a").let { it.exists() && it.length() >= 50_000 }
            if (!isAppOnline && !isLocal) {
                Toast.makeText(context, L.get("no_connection_track", lang), Toast.LENGTH_SHORT).show()
                player?.pause()
                return
            }
            currentQueueIndex--
            currentTrack = playQueue[currentQueueIndex]
            scope.launch {
                playTrack(currentTrack!!, player, context, prefs, lang)
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
        ContextCompat.registerReceiver(
            context, receiver, IntentFilter().apply {
                addAction("com.clio.hearon.NEXT_TRACK")
                addAction("com.clio.hearon.PREV_TRACK")
            }, ContextCompat.RECEIVER_NOT_EXPORTED
        )

        if (isOnline(context)) {
            tracks = HearonBackend.search("Éxitos Globales")
        }
        isLoading = false

        val autoUpdate = prefs.getBoolean("auto_update", false)
        val notifyUpdate = prefs.getBoolean("notify_update", false)

        if (autoUpdate || notifyUpdate) {
            val latest = HearonBackend.checkUpdate()
            if (latest != null && latest != APP_VERSION && latest > APP_VERSION) {
                if (autoUpdate) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/oriilol/hearon/releases/latest"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, L.get("update_available", lang) + latest, Toast.LENGTH_LONG).show()
                }
            }
        }

        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, token).buildAsync()

        controllerFuture.addListener({
            player = controllerFuture.get()
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }
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
                override fun onPlayerError(error: PlaybackException) {
                    player?.pause()
                    Toast.makeText(context, L.get("no_connection_track", lang), Toast.LENGTH_SHORT).show()
                }
            })
            player?.repeatMode = repeatMode
        }, ContextCompat.getMainExecutor(context))
    }

    LaunchedEffect(repeatMode) {
        player?.repeatMode = repeatMode
    }

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
                    val customCover = remember(t.id, coverUpdateTrigger) {
                        prefs.getString("custom_cover_${t.id}", t.coverUrl.replace("w1080-h1080", "w226-h226"))
                    }
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

                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(if (isLiked) L.get("remove_favorite", lang) else L.get("add_favorite", lang)) },
                    leadingContent = {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.clickable {
                        if (isLiked) {
                            likedTracks.removeAll { it.id == t.id }
                        } else {
                            likedTracks.add(t)
                        }
                        saveLikedTracks()
                        trackOptionsMenu = null
                    }
                )

                if (menuData.playlistIndex != null && menuData.playlistTrackIndex != null) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(L.get("remove_playlist", lang)) },
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
                    headlineContent = { Text(L.get("add_to_playlist", lang)) },
                    leadingContent = { Icon(Icons.Default.PlaylistAdd, null) },
                    modifier = Modifier.clickable {
                        trackToAdd = t
                        trackOptionsMenu = null
                    }
                )

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(L.get("change_cover", lang)) },
                    leadingContent = { Icon(Icons.Default.ImageSearch, null) },
                    modifier = Modifier.clickable {
                        coverPicker.launch("image/*")
                    }
                )

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(L.get("track_info", lang)) },
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
            title = { Text(text = L.get("track_info_title", lang), style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(text = L.get("title_label", lang) + "${t.title}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = L.get("artist_label", lang) + "${t.artist}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = L.get("yt_id_label", lang) + "${t.id}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = L.get("origin_label", lang) + "${if (isLocal) L.get("local_cache", lang) else L.get("streaming", lang)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (!isLocal) {
                        Text(text = L.get("network_quality", lang) + "$quality", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showTrackInfo = null }) {
                    Text(L.get("close", lang))
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
                    text = L.get("add_to_playlist", lang),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
                if (playlists.isEmpty()) {
                    Text(
                        text = L.get("no_playlists_yet", lang),
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
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
                                            Icon(imageVector = Icons.Default.QueueMusic, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
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
            title = { Text(text = L.get("clear_cache_title", lang), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(text = L.get("clear_cache_warning", lang), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                ) { Text(L.get("delete", lang)) }
            },
            dismissButton = { TextButton(onClick = { showCacheWarning = false }) { Text(L.get("cancel", lang)) } }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(targetState = selectedTab, label = "MainCrossfade") { tab ->
                when (tab) {
                    0 -> {
                        HomeScreen(
                            tracks = tracks, forYouTracks = forYouTracks, recentTracks = recentlyPlayed,
                            loading = isLoading && isOnline(context), loadingForYou = isLoadingForYou && isOnline(context),
                            currentId = currentTrack?.id, coverUpdateTrigger = coverUpdateTrigger, lang = lang,
                            onSelect = handleTrackSelect, onOptionsClick = { trackOptionsMenu = TrackMenuData(it) },
                            onRefresh = {
                                scope.launch {
                                    isLoading = true; tracks = HearonBackend.search("Éxitos Globales"); isLoading = false
                                    val lastId = recentlyPlayed.firstOrNull()?.id
                                    if (lastId != null && isOnline(context)) {
                                        isLoadingForYou = true; forYouTracks = HearonBackend.getUpNext(lastId).filter { it.id != lastId }; isLoadingForYou = false
                                    }
                                }
                            }
                        )
                    }
                    1 -> {
                        SearchScreen(
                            currentId = currentTrack?.id, coverUpdateTrigger = coverUpdateTrigger, lang = lang,
                            onSelect = handleTrackSelect, onOptionsClick = { trackOptionsMenu = TrackMenuData(it) }
                        )
                    }
                    2 -> {
                        LibraryScreen(
                            likedTracks = likedTracks, playlists = playlists, downloadedTracks = downloadedTracks,
                            currentId = currentTrack?.id, coverUpdateTrigger = coverUpdateTrigger, lang = lang,
                            onCreatePlaylist = { name -> playlists.add(Playlist(name, emptyList())); savePlaylists() },
                            onDeletePlaylist = { pIndex -> playlists.removeAt(pIndex); savePlaylists() },
                            onRenamePlaylist = { pIndex, newName -> val p = playlists[pIndex]; playlists[pIndex] = p.copy(name = newName); savePlaylists() },
                            onSelectTrack = { track, queue, index ->
                                val isLocal = File(context.filesDir, "hearon_downloads/${track.id}.m4a").let { it.exists() && it.length() >= 50_000 }
                                if (!isAppOnline && !isLocal) { Toast.makeText(context, L.get("no_connection_track", lang), Toast.LENGTH_SHORT).show() } else {
                                    currentTrack = track; originalQueue = queue
                                    playQueue = if (isShuffle) { listOf(track) + queue.filter { it.id != track.id }.shuffled() } else { queue }
                                    currentQueueIndex = playQueue.indexOf(track)
                                    scope.launch { playTrack(track, player, context, prefs, lang); saveRecentTrack(track); prefetchNextTrack(currentQueueIndex); refreshCacheCount() }
                                }
                            },
                            onOptionsClick = { track, pIdx, tIdx -> trackOptionsMenu = TrackMenuData(track, pIdx, tIdx) },
                            onClearCache = { showCacheWarning = true }, onRefresh = onRefresh
                        )
                    }
                    3 -> {
                        SettingsScreen(
                            currentTheme = currentTheme, onThemeChange = onThemeChange, prefs = prefs, context = context,
                            cachedCount = cachedFilesCount, cacheSizeMB = cacheSizeMB, isDynamicColorEnabled = isDynamicColorEnabled,
                            onDynamicColorToggle = onDynamicColorToggle, onClearCache = { showCacheWarning = true }, lang = lang,
                            onClearHistory = {
                                recentlyPlayed.clear()
                                prefs.edit().putStringSet("recent_tracks_data", emptySet()).apply()
                                Toast.makeText(context, L.get("history_cleared", lang), Toast.LENGTH_SHORT).show()
                            },
                            onLangChange = onLangChange
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = currentTrack != null && !isFullScreen,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                ExpressiveMiniPlayer(
                    track = currentTrack!!, isPlaying = isPlaying, coverUpdateTrigger = coverUpdateTrigger,
                    onPlayPause = { if (isPlaying) { player?.pause() } else { player?.play() } },
                    onNext = { playNext() },
                    onExpand = { isFullScreen = true; fullScreenState = "PLAYER" }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(visible = !isFullScreen) {
                FloatingNavBar(
                    selectedTab = selectedTab, onTabSelected = { selectedTab = it },
                    onTabReselected = { tabIndex ->
                        scope.launch {
                            when (tabIndex) {
                                0 -> {
                                    isLoading = true; tracks = HearonBackend.search("Éxitos Globales"); isLoading = false
                                    val lastId = recentlyPlayed.firstOrNull()?.id
                                    if (lastId != null && isOnline(context)) {
                                        isLoadingForYou = true; forYouTracks = HearonBackend.getUpNext(lastId).filter { it.id != lastId }; isLoadingForYou = false
                                    }
                                }
                                2 -> { onRefresh() }
                            }
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = isFullScreen,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 400)) + fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                AnimatedContent(
                    targetState = fullScreenState,
                    transitionSpec = { fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(initialScale = 0.95f) togetherWith fadeOut(animationSpec = tween(durationMillis = 300)) + scaleOut(targetScale = 1.05f) },
                    label = "PlayerStateTransition"
                ) { state ->
                    when (state) {
                        "LYRICS" -> currentTrack?.let {
                            LyricsScreen(
                                track = it, isPlaying = isPlaying, pos = pos, dur = dur, lang = lang,
                                onBack = { fullScreenState = "PLAYER" },
                                onPP = { if (isPlaying) { player?.pause() } else { player?.play() } },
                                onSeek = { p -> player?.seekTo(p); player?.playWhenReady = true }
                            )
                        }
                        "QUEUE" -> currentTrack?.let {
                            QueueScreen(
                                queue = playQueue, currentIndex = currentQueueIndex, coverUpdateTrigger = coverUpdateTrigger, lang = lang,
                                onBack = { fullScreenState = "PLAYER" },
                                onSelect = { idx ->
                                    currentQueueIndex = idx; currentTrack = playQueue[idx]
                                    scope.launch { playTrack(currentTrack!!, player, context, prefs, lang); saveRecentTrack(currentTrack!!); prefetchNextTrack(currentQueueIndex); refreshCacheCount() }
                                }
                            )
                        }
                        else -> currentTrack?.let { trackState ->
                            ExpressiveFullScreenPlayer(
                                track = trackState, isPlaying = isPlaying, pos = pos, dur = dur, isLiked = likedTracks.any { it.id == trackState.id },
                                isShuffle = isShuffle, repeatMode = repeatMode, hasPrev = pos > 3000 || currentQueueIndex > 0,
                                hasNext = currentQueueIndex < playQueue.size - 1 || repeatMode == Player.REPEAT_MODE_ALL, coverUpdateTrigger = coverUpdateTrigger, lang = lang,
                                onLike = {
                                    if (likedTracks.any { it.id == trackState.id }) { likedTracks.removeAll { it.id == trackState.id } } else { likedTracks.add(trackState) }
                                    saveLikedTracks()
                                },
                                onLyrics = { fullScreenState = "LYRICS" }, onQueue = { fullScreenState = "QUEUE" }, onMenuClick = { trackOptionsMenu = TrackMenuData(trackState) },
                                onShuffle = {
                                    isShuffle = !isShuffle; val current = trackState
                                    if (isShuffle) { playQueue = listOf(current) + originalQueue.filter { it.id != current.id }.shuffled(); currentQueueIndex = 0 } else { playQueue = originalQueue; currentQueueIndex = playQueue.indexOf(trackState) }
                                },
                                onRepeat = { repeatMode = when (repeatMode) { Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL; Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE; else -> Player.REPEAT_MODE_OFF }; player?.repeatMode = repeatMode },
                                onPP = { if (isPlaying) { player?.pause() } else { player?.play() } },
                                onSeek = { p -> player?.seekTo(p); player?.playWhenReady = true },
                                onPrev = { playPrev() }, onNext = { playNext() }, onClose = { isFullScreen = false; fullScreenState = "PLAYER" }
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private suspend fun playTrack(track: YtTrack, player: Player?, context: Context, prefs: SharedPreferences, lang: String) {
    if (player == null) { return }
    if (player.currentMediaItem?.mediaId == track.id && (player.playbackState == Player.STATE_READY || player.playbackState == Player.STATE_BUFFERING)) { return }

    val downloadDir = File(context.filesDir, "hearon_downloads")
    if (!downloadDir.exists()) { downloadDir.mkdirs() }
    val file = File(downloadDir, "${track.id}.m4a")

    if (file.exists() && file.length() < 50_000) { file.delete() }
    val isLocal = file.exists() && file.length() >= 50_000

    val streamUrl = if (isLocal) { Uri.fromFile(file).toString() } else { com.clio.hearon.api.YtMusicApi.getStreamUrl(track.id) }

    if (streamUrl != null) {
        val mime = if (isLocal) { MimeTypes.AUDIO_MP4 } else if (streamUrl.contains("webm")) { MimeTypes.AUDIO_WEBM } else { MimeTypes.AUDIO_MP4 }
        val mediaItem = MediaItem.Builder().setUri(streamUrl).setMediaId(track.id).setMimeType(mime).setMediaMetadata(MediaMetadata.Builder().setTitle(track.title).setArtist(track.artist).setArtworkUri(Uri.parse(track.coverUrl)).build()).build()

        withContext(Dispatchers.Main) { player.stop(); player.clearMediaItems(); player.setMediaItem(mediaItem); player.prepare(); player.play() }

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
                        } else { tmpFile.delete() }
                    }
                } catch (e: Exception) { }
            }
        }
    } else {
        withContext(Dispatchers.Main) { Toast.makeText(context, L.get("error_audio", lang), Toast.LENGTH_SHORT).show() }
    }
}

@Composable
fun ExpressiveFullScreenPlayer(
    track: YtTrack, isPlaying: Boolean, pos: Long, dur: Long, isLiked: Boolean, isShuffle: Boolean, repeatMode: Int, hasPrev: Boolean, hasNext: Boolean, coverUpdateTrigger: Int, lang: String,
    onLike: () -> Unit, onLyrics: () -> Unit, onQueue: () -> Unit, onMenuClick: () -> Unit, onShuffle: () -> Unit, onRepeat: () -> Unit, onPP: () -> Unit, onSeek: (Long) -> Unit, onPrev: () -> Unit, onNext: () -> Unit, onClose: () -> Unit
) {
    BackHandler { onClose() }
    val prefs = LocalContext.current.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    var sliderPos by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val coverScale by animateFloatAsState(targetValue = if (isPlaying) { 1f } else { 0.85f }, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "CoverScale")
    val coverRadius by animateDpAsState(targetValue = if (isPlaying) { 48.dp } else { 24.dp }, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "CoverRadius")
    val ppWidth by animateDpAsState(targetValue = if (isPlaying) { 100.dp } else { 84.dp }, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "PPWidth")
    val ppRadius by animateDpAsState(targetValue = if (isPlaying) { 32.dp } else { 40.dp }, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "PPRadius")

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

    val repeatIcon = when (repeatMode) { Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne; Player.REPEAT_MODE_ALL -> Icons.Default.RepeatOn; else -> Icons.Default.Repeat }
    val repeatBg = if (repeatMode != Player.REPEAT_MODE_OFF) { MaterialTheme.colorScheme.secondaryContainer } else { MaterialTheme.colorScheme.surfaceContainerHigh }
    val customCover = remember(track.id, coverUpdateTrigger) { prefs.getString("custom_cover_${track.id}", track.coverUrl) }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val offsetY by animateFloatAsState(targetValue = dragOffset, label = "")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(0, offsetY.toInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = { if (dragOffset > 300f) { onClose() }; dragOffset = 0f }
                ) { change, dragAmount -> change.consume(); if (dragOffset + dragAmount >= 0) { dragOffset += dragAmount } }
            }
    ) {
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize().background(gradient).safeDrawingPadding().padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    AsyncImage(model = customCover, contentDescription = null, modifier = Modifier.fillMaxHeight(0.8f).aspectRatio(1f).scale(coverScale).clip(RoundedCornerShape(coverRadius)), contentScale = ContentScale.Crop)
                }
                Spacer(modifier = Modifier.width(32.dp))
                Column(modifier = Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        IconButton(onClick = onClose, modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)) { Icon(Icons.Default.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                        IconButton(onClick = onMenuClick, modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)) { Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = track.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = track.artist, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(value = if (isDragging) { sliderPos } else { (if (dur > 0) pos.toFloat() / dur else 0f).coerceIn(0f, 1f) }, onValueChange = { isDragging = true; sliderPos = it }, onValueChangeFinished = { isDragging = false; onSeek((sliderPos * dur).toLong()) }, colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = formatTime(if (isDragging) { (sliderPos * dur).toLong() } else { pos }), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = formatTime(dur), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onPrev, enabled = hasPrev, modifier = Modifier.size(64.dp).background(if (hasPrev) { MaterialTheme.colorScheme.surfaceContainerHighest } else { Color.Transparent }, CircleShape)) { Icon(Icons.Default.SkipPrevious, null, tint = if (hasPrev) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(0.3f) }) }
                        IconButton(onClick = onPP, modifier = Modifier.width(ppWidth).height(72.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(ppRadius))) { Icon(if (isPlaying) { Icons.Default.Pause } else { Icons.Default.PlayArrow }, null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
                        IconButton(onClick = onNext, enabled = hasNext, modifier = Modifier.size(64.dp).background(if (hasNext) { MaterialTheme.colorScheme.surfaceContainerHighest } else { Color.Transparent }, CircleShape)) { Icon(Icons.Default.SkipNext, null, tint = if (hasNext) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(0.3f) }) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        IconButton(onClick = onLyrics, modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))) { Icon(Icons.Default.Lyrics, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                        IconButton(onClick = onShuffle, modifier = Modifier.size(56.dp).background(if (isShuffle) { MaterialTheme.colorScheme.secondaryContainer } else { MaterialTheme.colorScheme.surfaceContainerHigh }, RoundedCornerShape(16.dp))) { Icon(Icons.Default.Shuffle, null, tint = if (isShuffle) { MaterialTheme.colorScheme.onSecondaryContainer } else { MaterialTheme.colorScheme.onSurfaceVariant }) }
                        IconButton(onClick = onRepeat, modifier = Modifier.size(56.dp).background(repeatBg, RoundedCornerShape(16.dp))) { Icon(repeatIcon, null, tint = if (repeatMode != Player.REPEAT_MODE_OFF) { MaterialTheme.colorScheme.onSecondaryContainer } else { MaterialTheme.colorScheme.onSurfaceVariant }) }
                        IconButton(onClick = onQueue, modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))) { Icon(Icons.Default.QueueMusic, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().background(gradient).statusBarsPadding().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart).size(48.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)) { Icon(Icons.Default.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Text(text = L.get("now_playing", lang), modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    IconButton(onClick = onMenuClick, modifier = Modifier.align(Alignment.CenterEnd).size(48.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)) { Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                Spacer(modifier = Modifier.weight(1f))
                AnimatedContent(targetState = track, transitionSpec = { fadeIn(animationSpec = tween(durationMillis = 500)) + scaleIn(initialScale = 0.8f) togetherWith fadeOut(animationSpec = tween(durationMillis = 500)) + scaleOut(targetScale = 1.2f) }, label = "TrackAnimation") { currentTrack ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(model = customCover, contentDescription = null, modifier = Modifier.fillMaxWidth(0.92f).aspectRatio(1f).scale(coverScale).clip(RoundedCornerShape(coverRadius)), contentScale = ContentScale.Crop)
                        Spacer(modifier = Modifier.height(48.dp))
                        Text(text = currentTrack.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = currentTrack.artist, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Slider(value = if (isDragging) { sliderPos } else { (if (dur > 0) pos.toFloat() / dur else 0f).coerceIn(0f, 1f) }, onValueChange = { isDragging = true; sliderPos = it }, onValueChangeFinished = { isDragging = false; onSeek((sliderPos * dur).toLong()) }, colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = formatTime(if (isDragging) { (sliderPos * dur).toLong() } else { pos }), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = formatTime(dur), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPrev, enabled = hasPrev, modifier = Modifier.size(72.dp).background(if (hasPrev) { MaterialTheme.colorScheme.surfaceContainerHighest } else { Color.Transparent }, CircleShape)) { Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = null, modifier = Modifier.size(32.dp), tint = if (hasPrev) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(0.3f) }) }
                    IconButton(onClick = onPP, modifier = Modifier.width(ppWidth).height(80.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(ppRadius))) { Icon(imageVector = if (isPlaying) { Icons.Default.Pause } else { Icons.Default.PlayArrow }, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer) }
                    IconButton(onClick = onNext, enabled = hasNext, modifier = Modifier.size(72.dp).background(if (hasNext) { MaterialTheme.colorScheme.surfaceContainerHighest } else { Color.Transparent }, CircleShape)) { Icon(imageVector = Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(32.dp), tint = if (hasNext) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(0.3f) }) }
                }
                Spacer(modifier = Modifier.height(40.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLyrics, modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))) { Icon(Icons.Default.Lyrics, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    IconButton(onClick = onShuffle, modifier = Modifier.size(64.dp).background(if (isShuffle) { MaterialTheme.colorScheme.secondaryContainer } else { MaterialTheme.colorScheme.surfaceContainerHigh }, RoundedCornerShape(16.dp))) { Icon(Icons.Default.Shuffle, null, tint = if (isShuffle) { MaterialTheme.colorScheme.onSecondaryContainer } else { MaterialTheme.colorScheme.onSurfaceVariant }) }
                    IconButton(onClick = onRepeat, modifier = Modifier.size(64.dp).background(repeatBg, RoundedCornerShape(16.dp))) { Icon(imageVector = repeatIcon, contentDescription = null, tint = if (repeatMode != Player.REPEAT_MODE_OFF) { MaterialTheme.colorScheme.onSecondaryContainer } else { MaterialTheme.colorScheme.onSurfaceVariant }) }
                    IconButton(onClick = onQueue, modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))) { Icon(Icons.Default.QueueMusic, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun LyricsScreen(
    track: YtTrack, isPlaying: Boolean, pos: Long, dur: Long, lang: String, onBack: () -> Unit, onPP: () -> Unit, onSeek: (Long) -> Unit
) {
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

    val playPauseShapeRadius by animateDpAsState(targetValue = if (isPlaying) { 16.dp } else { 28.dp }, animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f), label = "PPShape")
    val playPauseColor by animateColorAsState(targetValue = if (isPlaying) { MaterialTheme.colorScheme.primaryContainer } else { MaterialTheme.colorScheme.secondaryContainer }, animationSpec = tween(durationMillis = 300), label = "PPColor")

    LaunchedEffect(track.id) {
        isLoadingLyrics = true
        val manual = prefs.getString("manual_lyrics_${track.id}", null)
        if (manual != null) {
            if (manual.contains("[")) { syncedLyricsRaw = manual } else { plainLyrics = manual }
        } else {
            if (isOnline(context)) { val (s, p) = HearonBackend.getLyrics(track.title, track.artist); syncedLyricsRaw = s; plainLyrics = p }
        }
        isLoadingLyrics = false
        if (syncedLyricsRaw == null && plainLyrics != null) { tab = "Static" }
    }

    val parsedLyrics = remember(syncedLyricsRaw) {
        if (syncedLyricsRaw == null) { emptyList() } else {
            val regex = Regex("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)")
            syncedLyricsRaw!!.lines().mapNotNull { line ->
                val match = regex.find(line)
                if (match != null) {
                    val (m, s, msStr, text) = match.destructured
                    val ms = if (msStr.length == 2) { msStr.toLong() * 10 } else { msStr.toLong() }
                    LyricLine(m.toLong() * 60000 + s.toLong() * 1000 + ms, text.trim())
                } else null
            }
        }
    }

    val activeLineIndex by remember(pos, parsedLyrics) { derivedStateOf { if (parsedLyrics.isEmpty()) { -1 } else { parsedLyrics.indexOfLast { it.timeMs <= pos }.coerceAtLeast(0) } } }
    val listState = rememberLazyListState()
    LaunchedEffect(activeLineIndex) { if (tab == "Synced" && activeLineIndex >= 0 && !listState.isScrollInProgress) { listState.animateScrollToItem(activeLineIndex, scrollOffset = 150) } }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(L.get("add_lyrics_manual", lang)) },
            text = { OutlinedTextField(value = userLyricsInput, onValueChange = { userLyricsInput = it }, placeholder = { Text(L.get("paste_lyrics_here", lang)) }, modifier = Modifier.fillMaxWidth().height(200.dp)) },
            confirmButton = { Button(onClick = { prefs.edit().putString("manual_lyrics_${track.id}", userLyricsInput).apply(); if (userLyricsInput.contains("[")) { syncedLyricsRaw = userLyricsInput; tab = "Synced" } else { plainLyrics = userLyricsInput; tab = "Static" }; showAddDialog = false }) { Text(L.get("save", lang)) } },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text(L.get("cancel", lang)) } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).statusBarsPadding().navigationBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp).height(48.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)) { Icon(Icons.Default.ArrowBack, null) }
            Text(text = L.get("lyrics", lang), modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            val isManual = prefs.getString("manual_lyrics_${track.id}", null) != null
            val noOfficialLyrics = syncedLyricsRaw == null && plainLyrics == null
            if (noOfficialLyrics || isManual) { IconButton(onClick = { userLyricsInput = prefs.getString("manual_lyrics_${track.id}", "") ?: ""; showAddDialog = true }, modifier = Modifier.align(Alignment.CenterEnd)) { Icon(Icons.Default.Edit, null) } }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (isLoadingLyrics) { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) } else if (syncedLyricsRaw == null && plainLyrics == null) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = L.get("no_lyrics_found", lang), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showAddDialog = true }) { Text(text = L.get("add_lyrics", lang)) }
                }
            } else {
                if (tab == "Synced" && parsedLyrics.isNotEmpty()) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 300.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
                        itemsIndexed(parsedLyrics) { i, line ->
                            val isActive = i == activeLineIndex
                            val isInstrumental = line.text.isBlank() || line.text == "♪" || line.text.contains("Instrumental", ignoreCase = true)
                            val bgColor by animateColorAsState(targetValue = if (isActive && !isInstrumental) { MaterialTheme.colorScheme.primaryContainer } else { Color.Transparent }, label = "")
                            val shapeRadius by animateDpAsState(targetValue = if (isActive) { 32.dp } else { 8.dp }, label = "")
                            val scale by animateFloatAsState(targetValue = if (isActive) { 1.05f } else { 1f }, label = "")
                            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).scale(scale).clip(RoundedCornerShape(shapeRadius)).background(bgColor).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onSeek(line.timeMs) }.padding(vertical = if (isInstrumental) 8.dp else 16.dp, horizontal = 16.dp), contentAlignment = Alignment.Center) {
                                if (isInstrumental) { if (isActive) { AppleMusicDots() } else { Text("•••", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)) } } else { Text(text = line.text, textAlign = TextAlign.Center, style = if (isActive) { MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold) } else { MaterialTheme.typography.titleLarge }, color = if (isActive) { MaterialTheme.colorScheme.onPrimaryContainer } else { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) }) }
                            }
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(32.dp)) {
                        itemsIndexed(plainLyrics?.split("\n") ?: emptyList()) { _, line -> Text(text = line, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), style = MaterialTheme.typography.titleLarge) }
                    }
                }
            }
        }

        Surface(modifier = Modifier.fillMaxWidth().padding(24.dp), shape = RoundedCornerShape(32.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = formatTime(if (isDragging) { (sliderPos * dur).toLong() } else { pos }), style = MaterialTheme.typography.labelMedium)
                Slider(value = if (isDragging) { sliderPos } else { (if (dur > 0) pos.toFloat() / dur else 0f).coerceIn(0f, 1f) }, onValueChange = { isDragging = true; sliderPos = it }, onValueChangeFinished = { isDragging = false; onSeek((sliderPos * dur).toLong()) }, modifier = Modifier.weight(1f))
                Text(text = formatTime(dur), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(12.dp))
                AnimatedContent(targetState = isPlaying, label = "PlayPauseIcon", modifier = Modifier.size(56.dp).clip(RoundedCornerShape(playPauseShapeRadius)).background(playPauseColor).clickable { onPP() }) { playing ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(imageVector = if (playing) { Icons.Default.Pause } else { Icons.Default.PlayArrow }, contentDescription = null, tint = if (playing) { MaterialTheme.colorScheme.onPrimaryContainer } else { MaterialTheme.colorScheme.onSecondaryContainer }) }
                }
            }
        }
    }
}

@Composable
fun QueueScreen(
    queue: List<YtTrack>, currentIndex: Int, coverUpdateTrigger: Int, lang: String, onBack: () -> Unit, onSelect: (Int) -> Unit
) {
    BackHandler { onBack() }
    val prefs = LocalContext.current.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).statusBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp).height(48.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
            Text(text = L.get("play_queue", lang), modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        }
        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
            itemsIndexed(queue) { index, track ->
                val active = index == currentIndex
                val customCover = remember(track.id, coverUpdateTrigger) { prefs.getString("custom_cover_${track.id}", track.coverUrl.replace("w1080-h1080", "w226-h226")) }
                Row(modifier = Modifier.fillMaxWidth().animateContentSize().background(if (active) { MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) } else { Color.Transparent }).clickable { onSelect(index) }.padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(model = customCover, contentDescription = null, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentScale = ContentScale.Crop)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = track.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (active) { MaterialTheme.colorScheme.primary } else { MaterialTheme.colorScheme.onSurface }, maxLines = 1)
                        Text(text = track.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                    if (active) { Icon(Icons.Default.GraphicEq, null, tint = MaterialTheme.colorScheme.primary) }
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(
    likedTracks: List<YtTrack>, playlists: MutableList<Playlist>, downloadedTracks: List<YtTrack>, currentId: String?, coverUpdateTrigger: Int, lang: String,
    onCreatePlaylist: (String) -> Unit, onDeletePlaylist: (Int) -> Unit, onRenamePlaylist: (Int, String) -> Unit, onSelectTrack: (YtTrack, List<YtTrack>, Int) -> Unit, onOptionsClick: (YtTrack, Int?, Int?) -> Unit, onClearCache: () -> Unit, onRefresh: () -> Unit
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
        AlertDialog(onDismissRequest = { showPlaylistDialog = false }, title = { Text(L.get("new_playlist", lang), color = MaterialTheme.colorScheme.onSurface) }, text = { OutlinedTextField(value = newPlaylistName, onValueChange = { newPlaylistName = it }, placeholder = { Text(L.get("playlist_name", lang)) }, singleLine = true, shape = RoundedCornerShape(16.dp)) }, confirmButton = { Button(onClick = { if (newPlaylistName.isNotBlank()) { onCreatePlaylist(newPlaylistName); showPlaylistDialog = false; newPlaylistName = "" } }) { Text(L.get("create", lang)) } }, dismissButton = { TextButton(onClick = { showPlaylistDialog = false }) { Text(L.get("cancel", lang)) } })
    }

    if (renamePlaylistIndex != null) {
        AlertDialog(onDismissRequest = { renamePlaylistIndex = null }, title = { Text(L.get("rename_playlist", lang), color = MaterialTheme.colorScheme.onSurface) }, text = { OutlinedTextField(value = renamePlaylistName, onValueChange = { renamePlaylistName = it }, placeholder = { Text(L.get("new_name", lang)) }, singleLine = true, shape = RoundedCornerShape(16.dp)) }, confirmButton = { Button(onClick = { if (renamePlaylistName.isNotBlank()) { onRenamePlaylist(renamePlaylistIndex!!, renamePlaylistName); renamePlaylistIndex = null; renamePlaylistName = "" } }) { Text(L.get("save", lang)) } }, dismissButton = { TextButton(onClick = { renamePlaylistIndex = null }) { Text(L.get("cancel", lang)) } })
    }

    if (showDeleteWarning != null) {
        AlertDialog(onDismissRequest = { showDeleteWarning = null }, title = { Text(L.get("delete_playlist", lang), color = MaterialTheme.colorScheme.onSurface) }, text = { Text(L.get("delete_playlist_confirm", lang), color = MaterialTheme.colorScheme.onSurfaceVariant) }, confirmButton = { Button(onClick = { onDeletePlaylist(showDeleteWarning!!); showDeleteWarning = null }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)) { Text(L.get("delete", lang)) } }, dismissButton = { TextButton(onClick = { showDeleteWarning = null }) { Text(L.get("cancel", lang)) } })
    }

    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    PullToRefreshBox(isRefreshing = refreshing, onRefresh = { scope.launch { refreshing = true; onRefresh(); delay(1000); refreshing = false } }, modifier = Modifier.fillMaxSize()) {
        AnimatedContent(targetState = playlistToOpenName, transitionSpec = { if (targetState != null) { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 400)) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(durationMillis = 400)) + fadeOut() } else { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut() } }, label = "LibraryTransition") { openedName ->
            if (openedName != null && playlistToOpen != null) {
                BackHandler { playlistToOpenName = null }
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp).height(48.dp)) {
                        IconButton(onClick = { playlistToOpenName = null }, modifier = Modifier.align(Alignment.CenterStart).size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                        Text(text = playlistToOpen.name, modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 320.dp), contentPadding = PaddingValues(bottom = 200.dp), modifier = Modifier.fillMaxSize()) {
                        val pIndex = playlists.indexOf(playlistToOpen)
                        itemsIndexed(playlistToOpen.tracks) { i, track -> ExpressiveTrackRow(t = track, active = track.id == currentId, coverUpdateTrigger = coverUpdateTrigger, onClick = { onSelectTrack(track, playlistToOpen.tracks, i) }, onOptionsClick = { onOptionsClick(track, pIndex, i) }) }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Text(text = L.get("your_library", lang), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(24.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ExpressiveChip(selected = libraryTab == "Favoritos", text = L.get("favorites", lang), onClick = { libraryTab = "Favoritos" })
                        ExpressiveChip(selected = libraryTab == "Playlists", text = L.get("playlists", lang), onClick = { libraryTab = "Playlists" })
                    }
                    when (libraryTab) {
                        "Favoritos" -> {
                            if (likedTracks.isEmpty()) { LazyColumn(modifier = Modifier.fillMaxSize()) { item { Box(modifier = Modifier.fillParentMaxSize().padding(vertical = 100.dp), contentAlignment = Alignment.Center) { Text(L.get("no_favorites_yet", lang), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) } } } } else {
                                LazyVerticalGrid(columns = GridCells.Adaptive(160.dp), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 200.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                                    itemsIndexed(likedTracks) { i, track ->
                                        val isActive = track.id == currentId
                                        val customCover = remember(track.id, coverUpdateTrigger) { prefs.getString("custom_cover_${track.id}", track.coverUrl.replace("w1080-h1080", "w226-h226")) }
                                        Surface(modifier = Modifier.fillMaxWidth().aspectRatio(0.8f), shape = RoundedCornerShape(16.dp), color = if (isActive) { MaterialTheme.colorScheme.secondaryContainer } else { MaterialTheme.colorScheme.surfaceContainerHigh }, onClick = { onSelectTrack(track, likedTracks, i) }) {
                                            Column(modifier = Modifier.fillMaxSize()) {
                                                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                                    AsyncImage(model = customCover, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                                    IconButton(onClick = { onOptionsClick(track, null, null) }, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape).size(32.dp)) { Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface) }
                                                }
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Text(text = track.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = if (isActive) { MaterialTheme.colorScheme.primary } else { MaterialTheme.colorScheme.onSurface }, maxLines = 1)
                                                    Text(text = track.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Playlists" -> {
                            LazyVerticalGrid(columns = GridCells.Adaptive(160.dp), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 200.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                                item {
                                    Surface(modifier = Modifier.fillMaxWidth().aspectRatio(0.8f), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh, onClick = { showPlaylistDialog = true }) {
                                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                            Box(modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(L.get("new_playlist", lang), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }
                                itemsIndexed(playlists) { index, playlist ->
                                    Surface(modifier = Modifier.fillMaxWidth().aspectRatio(0.8f), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh, onClick = { playlistToOpenName = playlist.name }) {
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                                val firstTrack = playlist.tracks.firstOrNull()
                                                val customCover = remember(firstTrack?.id, coverUpdateTrigger) { firstTrack?.let { prefs.getString("custom_cover_${it.id}", it.coverUrl.replace("w1080-h1080", "w226-h226")) } }
                                                if (customCover != null) { AsyncImage(model = customCover, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) } else { Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) { Icon(Icons.Default.QueueMusic, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer) } }
                                                Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                                    IconButton(onClick = { renamePlaylistIndex = index; renamePlaylistName = playlist.name }, modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape).size(32.dp)) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface) }
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    IconButton(onClick = { showDeleteWarning = index }, modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape).size(32.dp)) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error) }
                                                }
                                            }
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(text = playlist.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                                Text(text = "${playlist.tracks.size} " + L.get("tracks", lang), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    Surface(modifier = Modifier.fillMaxWidth(0.9f).height(72.dp), shape = RoundedCornerShape(40.dp), tonalElevation = 10.dp, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            val icons = listOf(Icons.Default.Home to 0, Icons.Default.Search to 1, Icons.Default.LibraryMusic to 2, Icons.Default.Settings to 3)
            val scope = rememberCoroutineScope()
            icons.forEach { (icon, index) ->
                val scale = remember { androidx.compose.animation.core.Animatable(1f) }
                Box(modifier = Modifier.clip(CircleShape).background(if (selectedTab == index) { MaterialTheme.colorScheme.secondaryContainer } else { Color.Transparent }).scale(scale.value).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { scope.launch { scale.animateTo(0.6f, tween(durationMillis = 100)); scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }; if (selectedTab == index) { onTabReselected(index) } else { onTabSelected(index) } }.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Icon(imageVector = icon, contentDescription = null, tint = if (selectedTab == index) { MaterialTheme.colorScheme.onSecondaryContainer } else { MaterialTheme.colorScheme.onSurfaceVariant })
                }
            }
        }
    }
}

@Composable
fun ExpressiveMiniPlayer(
    track: YtTrack, isPlaying: Boolean, coverUpdateTrigger: Int, onPlayPause: () -> Unit, onNext: () -> Unit, onExpand: () -> Unit
) {
    val prefs = LocalContext.current.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    val customCover = remember(track.id, coverUpdateTrigger) { prefs.getString("custom_cover_${track.id}", track.coverUrl.replace("w1080-h1080", "w226-h226")) }
    val ppRadius by animateDpAsState(targetValue = if (isPlaying) { 16.dp } else { 26.dp }, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "MiniPPRadius")

    Surface(modifier = Modifier.fillMaxWidth(0.95f).height(72.dp).clip(RoundedCornerShape(36.dp)).clickable { onExpand() }, tonalElevation = 8.dp, color = MaterialTheme.colorScheme.surfaceContainerHighest) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AsyncImage(model = customCover, contentDescription = null, modifier = Modifier.size(48.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = track.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = track.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPlayPause, modifier = Modifier.size(width = 52.dp, height = 52.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(ppRadius))) { Icon(imageVector = if (isPlaying) { Icons.Default.Pause } else { Icons.Default.PlayArrow }, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
                IconButton(onClick = onNext) { Icon(imageVector = Icons.Default.SkipNext, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
fun ExpressiveTrackRow(
    t: YtTrack, active: Boolean, coverUpdateTrigger: Int, onClick: () -> Unit, onOptionsClick: () -> Unit
) {
    val prefs = LocalContext.current.getSharedPreferences("hearon_prefs", Context.MODE_PRIVATE)
    val customCover = remember(t.id, coverUpdateTrigger) { prefs.getString("custom_cover_${t.id}", t.coverUrl.replace("w1080-h1080", "w226-h226")) }

    Row(modifier = Modifier.fillMaxWidth().animateContentSize().background(if (active) { MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) } else { Color.Transparent }).clickable { onClick() }.padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = customCover, contentDescription = null, modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = t.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (active) { MaterialTheme.colorScheme.primary } else { MaterialTheme.colorScheme.onSurface }, maxLines = 1)
            Text(text = t.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
        IconButton(onClick = onOptionsClick) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
fun HomeScreen(
    tracks: List<YtTrack>, forYouTracks: List<YtTrack>, recentTracks: List<YtTrack>, loading: Boolean, loadingForYou: Boolean, currentId: String?, coverUpdateTrigger: Int, lang: String, onSelect: (YtTrack) -> Unit, onOptionsClick: (YtTrack) -> Unit, onRefresh: () -> Unit
) {
    var homeTab by remember { mutableStateOf("Tendencias") }
    val currentList = when (homeTab) { "Tendencias" -> tracks; "Para ti" -> forYouTracks; "Recientes" -> recentTracks; else -> tracks }
    val isListLoading = when(homeTab) { "Tendencias" -> loading; "Para ti" -> loadingForYou; else -> false }
    val state = rememberPullToRefreshState()

    PullToRefreshBox(isRefreshing = isListLoading, onRefresh = onRefresh, state = state, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Text(text = L.get("home", lang), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(24.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExpressiveChip(selected = homeTab == "Tendencias", text = L.get("trending", lang), onClick = { homeTab = "Tendencias" })
                ExpressiveChip(selected = homeTab == "Para ti", text = L.get("for_you", lang), onClick = { homeTab = "Para ti" })
                ExpressiveChip(selected = homeTab == "Recientes", text = L.get("recent", lang), onClick = { homeTab = "Recientes" })
            }

            val isOffline = !isOnline(LocalContext.current)

            if (currentList.isEmpty() && !isListLoading) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize().padding(vertical = 100.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isOffline && homeTab != "Recientes") L.get("offline_title", lang) else if (homeTab == "Tendencias") L.get("nothing_to_show", lang) else L.get("no_recent_songs", lang),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 320.dp), contentPadding = PaddingValues(bottom = 200.dp), modifier = Modifier.fillMaxSize()) {
                    items(currentList.size) { index -> ExpressiveTrackRow(t = currentList[index], active = currentList[index].id == currentId, coverUpdateTrigger = coverUpdateTrigger, onClick = { onSelect(currentList[index]) }, onOptionsClick = { onOptionsClick(currentList[index]) }) }
                }
            }
        }
    }
}

@Composable
fun SearchScreen(
    currentId: String?, coverUpdateTrigger: Int, lang: String, onSelect: (YtTrack) -> Unit, onOptionsClick: (YtTrack) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<YtTrack>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    var searchType by remember { mutableStateOf("MUSICA") }
    var hasSearched by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current

    fun performSearch() {
        if (query.isBlank()) { return }; focus.clearFocus(); searching = true; hasSearched = true
        scope.launch { results = HearonBackend.search(query, searchType); searching = false }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Text(text = L.get("explore", lang), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(24.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }, shape = RoundedCornerShape(24.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { performSearch() }), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer, unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ExpressiveChip(selected = searchType == "MUSICA", text = L.get("yt_music", lang), onClick = { searchType = "MUSICA"; if (query.isNotBlank()) { performSearch() } })
            ExpressiveChip(selected = searchType == "VIDEO", text = L.get("youtube", lang), onClick = { searchType = "VIDEO"; if (query.isNotBlank()) { performSearch() } })
        }

        val isOffline = !isOnline(LocalContext.current)

        if (isOffline) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize().padding(vertical = 100.dp), contentAlignment = Alignment.Center) {
                        Text(L.get("offline_title", lang), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else if (searching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
        } else if (hasSearched && results.isEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize().padding(vertical = 100.dp), contentAlignment = Alignment.Center) {
                        Text(L.get("no_results", lang), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 320.dp), contentPadding = PaddingValues(bottom = 200.dp), modifier = Modifier.fillMaxSize()) {
                items(results.size) { index -> ExpressiveTrackRow(t = results[index], active = results[index].id == currentId, coverUpdateTrigger = coverUpdateTrigger, onClick = { onSelect(results[index]) }, onOptionsClick = { onOptionsClick(results[index]) }) }
            }
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
}

@Composable
fun SettingItemRow(
    icon: ImageVector?, title: String, subtitle: String = "", onClick: () -> Unit = {}, trailing: @Composable (() -> Unit)? = null
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), shape = RoundedCornerShape(20.dp), onClick = onClick, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                if (icon != null) { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                if (subtitle.isNotEmpty()) { Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            if (trailing != null) { Spacer(modifier = Modifier.width(16.dp)); trailing() }
        }
    }
}

@Composable
fun SettingSliderRow(
    icon: ImageVector, title: String, value: Float, valueRange: ClosedFloatingPointRange<Float>, valueText: String, enabled: Boolean = true, onValueChange: (Float) -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = if (enabled) { MaterialTheme.colorScheme.onSurfaceVariant } else { MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f) })
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = if (enabled) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) })
                Text(text = valueText, style = MaterialTheme.typography.bodyMedium, color = if (enabled) { MaterialTheme.colorScheme.onSurfaceVariant } else { MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f) })
                Slider(value = value, onValueChange = onValueChange, valueRange = valueRange, enabled = enabled, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: String, onThemeChange: (String) -> Unit, prefs: SharedPreferences, context: Context, cachedCount: Int, cacheSizeMB: Int, isDynamicColorEnabled: Boolean, onDynamicColorToggle: (Boolean) -> Unit, onClearCache: () -> Unit, lang: String, onClearHistory: () -> Unit, onLangChange: (String) -> Unit
) {
    var currentSettingsPage by remember { mutableStateOf("MAIN") }
    BackHandler(enabled = currentSettingsPage != "MAIN") { currentSettingsPage = "MAIN" }

    var audioQuality by remember { mutableStateOf(prefs.getString("audio_quality", "Alta") ?: "Alta") }
    var showQuality by remember { mutableStateOf(false) }
    var showLang by remember { mutableStateOf(false) }

    var crossfadeEnabled by remember { mutableStateOf(prefs.getBoolean("crossfade_enabled", false)) }
    var crossfadeDur by remember { mutableFloatStateOf(prefs.getFloat("crossfade_dur", 1f).coerceAtLeast(1f)) }
    var normAudio by remember { mutableStateOf(prefs.getBoolean("norm_audio", true)) }
    var maxImgCache by remember { mutableFloatStateOf(prefs.getFloat("max_img_cache", 512f)) }
    var dataSaver by remember { mutableStateOf(prefs.getBoolean("data_saver", false)) }
    var autoUpdate by remember { mutableStateOf(prefs.getBoolean("auto_update", false)) }
    var notifyUpdate by remember { mutableStateOf(prefs.getBoolean("notify_update", false)) }
    var checkUpdateState by remember { mutableStateOf(L.get("check_updates", lang)) }

    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    if (showQuality) {
        ModalBottomSheet(onDismissRequest = { showQuality = false }) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(L.get("sound_quality", lang), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(24.dp))
                listOf(L.get("quality_high", lang), L.get("quality_normal", lang), L.get("quality_low", lang)).forEach { q ->
                    Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = if (audioQuality == q) { MaterialTheme.colorScheme.secondaryContainer } else { Color.Transparent }, onClick = { audioQuality = q; prefs.edit().putString("audio_quality", q).apply(); showQuality = false }) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = q, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.weight(1f))
                            if (audioQuality == q) { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onSecondaryContainer) }
                        }
                    }
                }
            }
        }
    }

    if (showLang) {
        ModalBottomSheet(onDismissRequest = { showLang = false }) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(L.get("language", lang), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(24.dp))
                val langs = mapOf("system" to L.get("lang_system", lang), "es" to "Español", "en" to "English", "fr" to "Français", "de" to "Deutsch", "it" to "Italiano", "pt" to "Português", "ru" to "Русский", "ja" to "日本語", "zh" to "中文", "hi" to "हिन्दी", "ar" to "العربية")
                val langsKeys = langs.keys.toList()
                LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(langsKeys.size) { index ->
                        val l = langsKeys[index]
                        Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = if (lang == l) { MaterialTheme.colorScheme.secondaryContainer } else { Color.Transparent }, onClick = { onLangChange(l); showLang = false }) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = langs[l]!!, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.weight(1f))
                                if (lang == l) { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onSecondaryContainer) }
                            }
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
                    Text(L.get("settings", lang), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(24.dp))
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        item {
                            SettingItemRow(Icons.Default.Palette, L.get("appearance", lang), L.get("appearance_desc", lang), { currentSettingsPage = "APARIENCIA" })
                            SettingItemRow(Icons.Default.GraphicEq, L.get("player_and_sound", lang), L.get("player_desc", lang), { currentSettingsPage = "REPRODUCTOR" })
                            SettingItemRow(Icons.Default.Storage, L.get("storage_and_data", lang), L.get("storage_desc", lang), { currentSettingsPage = "ALMACENAMIENTO" })
                            SettingItemRow(Icons.Default.SystemUpdate, L.get("about_hearon", lang), L.get("current_version", lang) + " $APP_VERSION", { currentSettingsPage = "ACTUALIZADOR" })
                        }
                    }
                }
            }
            "APARIENCIA" -> {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentSettingsPage = "MAIN" }) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(L.get("appearance", lang), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        item {
                            SettingItemRow(null, L.get("language", lang), L.get("language_desc", lang), { showLang = true })
                            SettingsHeader(L.get("adaptive_colors", lang))
                            SettingItemRow(Icons.Default.ColorLens, L.get("dynamic_theme", lang), L.get("dynamic_theme_desc", lang), { onDynamicColorToggle(!isDynamicColorEnabled) }) { Switch(checked = isDynamicColorEnabled, onCheckedChange = null) }
                            SettingsHeader(L.get("app_theme", lang))
                            listOf(L.get("theme_system", lang), L.get("theme_light", lang), L.get("theme_dark", lang)).forEach { t ->
                                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.elevatedCardColors(containerColor = if (currentTheme == t) { MaterialTheme.colorScheme.secondaryContainer } else { MaterialTheme.colorScheme.surfaceContainerHigh }), onClick = { onThemeChange(t) }) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = t, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.weight(1f))
                                        if (currentTheme == t) { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onSecondaryContainer) }
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
                        Text(L.get("player_and_sound", lang), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        item {
                            SettingsHeader(L.get("player", lang))
                            SettingItemRow(Icons.Default.GraphicEq, L.get("sound_quality", lang), audioQuality, { showQuality = true })
                            SettingItemRow(Icons.Default.CompareArrows, L.get("crossfade", lang), L.get("crossfade_desc", lang), { crossfadeEnabled = !crossfadeEnabled; prefs.edit().putBoolean("crossfade_enabled", crossfadeEnabled).apply() }) { Switch(checked = crossfadeEnabled, onCheckedChange = null) }
                            SettingSliderRow(Icons.Default.WrapText, L.get("crossfade_duration", lang), crossfadeDur, 1f..10f, if (crossfadeEnabled) { "${crossfadeDur.toInt()} s" } else { L.get("disabled", lang) }, crossfadeEnabled) { crossfadeDur = it; prefs.edit().putFloat("crossfade_dur", it).apply() }
                            SettingItemRow(Icons.Default.VolumeUp, L.get("audio_normalization", lang), L.get("audio_normalization_desc", lang), { normAudio = !normAudio; prefs.edit().putBoolean("norm_audio", normAudio).apply() }) { Switch(checked = normAudio, onCheckedChange = null) }
                        }
                    }
                }
            }
            "ALMACENAMIENTO" -> {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentSettingsPage = "MAIN" }) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(L.get("storage_and_data", lang), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    }
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        item {
                            SettingsHeader(L.get("invisible_audio_cache", lang))
                            SettingItemRow(Icons.Default.LibraryMusic, L.get("auto_saved_music", lang), if(cachedCount > 0) { "$cachedCount " + L.get("tracks", lang) + " ($cacheSizeMB MB)" } else { "0 MB" }, {})
                            SettingItemRow(Icons.Default.DeleteSweep, L.get("clear_audio_cache", lang), L.get("clear_audio_cache_desc", lang), { onClearCache() })
                            SettingsHeader(L.get("images_and_data", lang))
                            SettingSliderRow(Icons.Default.ImageSearch, L.get("cover_limit", lang), maxImgCache, 128f..1024f, java.lang.String.format(java.util.Locale.US, "%.0f MB", maxImgCache), true) { maxImgCache = it; prefs.edit().putFloat("max_img_cache", it).apply() }
                            SettingItemRow(Icons.Default.DeleteOutline, L.get("clear_covers", lang), L.get("clear_covers_desc", lang), { })
                            SettingItemRow(Icons.Default.Info, L.get("data_saver", lang), L.get("data_saver_desc", lang), { dataSaver = !dataSaver; prefs.edit().putBoolean("data_saver", dataSaver).apply() }) { Switch(checked = dataSaver, onCheckedChange = null) }
                            SettingsHeader(L.get("clear_history", lang))
                            SettingItemRow(Icons.Default.History, L.get("clear_history", lang), L.get("clear_history_desc", lang), { onClearHistory() })
                        }
                    }
                }
            }
            "ACTUALIZADOR" -> {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentSettingsPage = "MAIN" }) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(L.get("about_hearon", lang), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
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
                            SettingsHeader(L.get("version_and_updates", lang))
                            SettingItemRow(null, L.get("current_version", lang), "v$APP_VERSION", {})
                            SettingItemRow(Icons.Default.Update, L.get("auto_updates", lang), "", { autoUpdate = !autoUpdate; prefs.edit().putBoolean("auto_update", autoUpdate).apply() }) { Switch(checked = autoUpdate, onCheckedChange = null) }
                            SettingItemRow(Icons.Default.Notifications, L.get("notify_updates", lang), "", { notifyUpdate = !notifyUpdate; prefs.edit().putBoolean("notify_update", notifyUpdate).apply() }) { Switch(checked = notifyUpdate, onCheckedChange = null) }
                            Spacer(modifier = Modifier.height(16.dp))
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp),
                                onClick = {
                                    if (checkUpdateState.startsWith(L.get("update_available", lang))) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/oriilol/hearon/releases/latest"))
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    } else {
                                        checkUpdateState = L.get("searching", lang)
                                        scope.launch {
                                            val latest = HearonBackend.checkUpdate()
                                            checkUpdateState = if (latest != null && latest != APP_VERSION && latest > APP_VERSION) { L.get("update_available", lang) + latest } else { L.get("latest_version", lang) }
                                            delay(3000)
                                            if (!checkUpdateState.startsWith(L.get("update_available", lang))) checkUpdateState = L.get("check_updates", lang)
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
            if (k == "playlistPanelRenderer") { return json.optJSONObject(k)?.optJSONArray("contents") }
            val o = json.optJSONObject(k)
            if (o != null) { val r = findPanel(o); if (r != null) { return r } }
            val a = json.optJSONArray(k)
            if (a != null) { for (i in 0 until a.length()) { val ao = a.optJSONObject(i); if (ao != null) { val r = findPanel(ao); if (r != null) { return r } } } }
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
                        if (id.isNotEmpty()) { list.add(YtTrack(id, title, artist, cover)) }
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
                return@withContext Pair(j.optString("syncedLyrics", "").takeIf { it.isNotBlank() }, j.optString("plainLyrics", "").takeIf { it.isNotBlank() })
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