# Prompt para continuar el proyecto HearOn

Copia todo lo que hay debajo de la línea y pégalo como primer mensaje en Antigravity, con la carpeta del proyecto iOS abierta.

---

Vas a continuar el desarrollo de **HearOn**, un reproductor de música que obtiene canciones de YouTube Music. Háblame en español.

## Los dos proyectos

1. **Android (original):** rama `main` de este repo, en Kotlin con Jetpack Compose. Casi todo el código está en `app/src/main/java/com/clio/hearon/MainActivity.kt` (~3.400 líneas), además de `PlaybackService.kt` (ExoPlayer/MediaSession) y `api/YtMusicApi.kt` (NewPipeExtractor). **Todavía no se ha cambiado nada en este proyecto.**
2. **iOS (port nuevo):** rama `ios` de este repo, en SwiftUI.

## Proyecto iOS: cómo está hecho

- **Versión mínima:** iOS 26, porque Liquid Glass existe desde iOS 26. Se compila con Xcode 27. Fuente del sistema (San Francisco); no usar fuentes propias.
- **Proyecto generado con XcodeGen:** la fuente de verdad es `project.yml`. Después de cambiarlo, ejecuta `xcodegen generate`. No edites a mano `project.pbxproj` para cosas que deban sobrevivir a esa regeneración. El equipo de firma va en `Local.xcconfig` (no se sube a git; ver `Signing.xcconfig`). Bundle ID: `com.clio.hearon`.
- **Dependencia:** [YouTubeKit](https://github.com/alexeichhorn/YouTubeKit) por SPM. Sustituye a NewPipeExtractor, que es Java y no funciona en iOS.
- **Compilar desde terminal:**
  ```
  xcodebuild -project HearOn.xcodeproj -scheme HearOn -destination 'generic/platform=iOS Simulator' -derivedDataPath build CODE_SIGNING_ALLOWED=NO build
  ```
- **Estructura:**
  - `HearOn/App/HearOnApp.swift`: entrada de la app, función global `t("clave")` para las traducciones y `UIState` (hojas y alertas compartidas).
  - `HearOn/App/RootView.swift`: `TabView` de iOS 26 (Inicio, Biblioteca, Ajustes y Búsqueda con `role: .search`), mini reproductor en `.tabViewBottomAccessory`, reproductor a pantalla completa con transición zoom, toasts, selector de fotos para portadas.
  - `HearOn/API/YTMusicClient.swift`: API interna de YouTube Music (`/youtubei/v1/search`, `/next` con `RDAMVM{id}`, `/browse` con `FEmusic_charts`) y letras de lrclib.net.
  - `HearOn/API/StreamResolver.swift`: URL de audio m4a con YouTubeKit (AVPlayer no reproduce WebM/Opus).
  - `HearOn/Core/PlayerController.swift`: AVPlayer, cola, aleatorio/repetir, crossfade, mix, pantalla de bloqueo (MPNowPlayingInfoCenter / MPRemoteCommandCenter), color dominante.
  - `HearOn/Core/DownloadManager.swift`: caché y descargas por bloques con cabecera `Range` (YouTube corta las descargas sin `Range`).
  - `HearOn/Core/LibraryStore.swift`: favoritos, historial, playlists, portadas personalizadas y letras manuales, guardados en JSON en Application Support.
  - `HearOn/Core/AppSettings.swift`: ajustes en UserDefaults, caché de imágenes (`URLCache`) y `Covers.url()` (tamaño de portada y ahorro de datos).
  - `HearOn/Core/L10n.swift` y `HearOn/Resources/Strings.json`: idiomas.
  - `HearOn/Views/`: Home, Search, Library (incluye PlaylistDetail), Settings, PlayerViews (MiniPlayer, PlayerScreen, LyricsView, QueueView) y Components.

## Reglas obligatorias

1. **Idiomas:** la app tiene 11 idiomas (es, en, fr, de, it, pt, ru, ja, zh, hi, ar) y se pueden cambiar dentro de la app; el árabe va de derecha a izquierda. **Todo texto nuevo debe estar traducido a los 11.** Las claves heredadas de Android están en `Strings.json`; las nuevas de iOS, en el diccionario `extra` de `L10n.swift`. Nunca pongas texto visible sin traducir.
2. **Liquid Glass:** usa las APIs nativas (`.glassEffect`, `.buttonStyle(.glass)` / `.glassProminent`, `GlassEffectContainer`, componentes del sistema). No imites el cristal con blur a mano.
3. **Compila después de cada cambio** y no des nada por terminado sin compilar.
4. **Git:** haz commits pequeños y descriptivos. **Pregúntame siempre antes de hacer `git push`** o de publicar algo en GitHub.
5. **Android:** no cambies nada del proyecto Android sin que yo te lo apruebe primero.

## Estado actual

- La app **se ha probado en el simulador de iOS 27 (iPhone 17)** y funciona: tendencias reales (playlists enlazadas desde `FEmusic_charts`), reproducción en streaming y desde archivo descargado, descargas por bloques completas, mini reproductor, reproductor a pantalla completa con color dinámico y pantalla de letras.
- Problemas ya resueltos (no los reintroduzcas): YouTube responde 403 a AVPlayer si no lleva cabecera `User-Agent` (ver `makeItem` en `PlayerController`); el mini reproductor usa `tabViewBottomAccessory(isEnabled:)` en iOS 26.1+ para que las pestañas no se reinicien; las filas son `Button` (con `onTapGesture` + `contextMenu` los toques iban con retraso).
- **Falta probar:** búsqueda, biblioteca/playlists/mix, portadas personalizadas, cambio de idioma y árabe RTL, pantalla de bloqueo y Centro de Control, audio en segundo plano, crossfade, y que la calidad "Alta" elija de verdad el m4a de mayor bitrate (algunos archivos descargados parecen de ~64 kbps).
- **Después:** generar un **IPA sin firmar** para subirlo a una release de GitHub (que se instale con AltStore, SideStore o Sideloadly). Se compila con `-sdk iphoneos CODE_SIGNING_ALLOWED=NO`, se mete `HearOn.app` en una carpeta `Payload/` y se comprime en `HearOn.ipa`.

## Mejoras pendientes de aprobar en Android

Ya están hechas en iOS. En Android solo se aplican si yo lo apruebo:

1. **Tendencias:** ahora buscan "Éxitos Globales" y devuelven éxitos de siempre. Hay que usar las listas reales de YouTube Music.
2. **Ahorro de datos y límite de caché de portadas:** los ajustes se guardan pero ningún código los lee. Hay que conectarlos a Coil.
3. **Portadas:** el regex `=w1080-h1080` rompe las miniaturas de `i.ytimg.com`. La portada de las playlists ignora la portada personalizada; propuesta: collage 2×2.
4. **Descargas:** la caché descarga la URL completa sin `Range` y quedan archivos cortados. Hay que descargar por bloques y añadir un botón "Descargar" y una sección Descargas.
5. **Mix de una playlist.**
6. **Guardado:** playlists, favoritos e historial usan `putStringSet`, que pierde el orden y se rompe con `:::` o `|||` en los nombres. Hay que pasarlo a JSON con migración.
7. **Idiomas:** el sistema actual son mapas en Kotlin; los 11 idiomas tienen las 108 claves completas. Opciones: A) pasar a `strings.xml` (RTL correcto, plurales); B) mantenerlo pero moverlo a su propio archivo y forzar RTL para el árabe. **Aún no he elegido.**
8. **Código duplicado:** unir `YtMusicApi` y `HearonBackend`.
9. **Builds:** se pueden compilar desde terminal con el JDK que trae Android Studio: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleRelease`. Hay que firmar los APK con **la misma clave de siempre** para que se puedan actualizar encima; todavía no sé dónde está el keystore. Pregúntamelo.

Empieza leyendo el código del proyecto iOS para entenderlo, y después dime qué vas a hacer primero antes de hacerlo.
