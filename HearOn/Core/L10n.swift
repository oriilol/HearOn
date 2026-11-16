import Foundation
import SwiftUI

/// Same model as the Android `L` object: the user can pick the app language
/// inside the app, independently of the system language.
/// Strings come from Resources/Strings.json (generated from the Android app)
/// plus the iOS-only keys defined in `extra` below.
enum L {
    static let supported: [(code: String, name: String)] = [
        ("es", "Español"), ("en", "English"), ("fr", "Français"), ("de", "Deutsch"),
        ("it", "Italiano"), ("pt", "Português"), ("ru", "Русский"), ("ja", "日本語"),
        ("zh", "中文"), ("hi", "हिन्दी"), ("ar", "العربية"),
    ]

    private static let table: [String: [String: String]] = {
        var t: [String: [String: String]] = [:]
        if let url = Bundle.main.url(forResource: "Strings", withExtension: "json"),
           let data = try? Data(contentsOf: url),
           let decoded = try? JSONDecoder().decode([String: [String: String]].self, from: data) {
            t = decoded
        }
        for (lang, values) in extra {
            t[lang, default: [:]].merge(values) { _, new in new }
        }
        return t
    }()

    static func resolve(_ lang: String) -> String {
        guard lang == "system" else { return lang }
        let sys = Locale.preferredLanguages.first.map { Locale(identifier: $0).language.languageCode?.identifier ?? "en" } ?? "en"
        return table[sys] != nil ? sys : "en"
    }

    static func get(_ key: String, _ lang: String) -> String {
        let l = resolve(lang)
        return table[l]?[key] ?? table["en"]?[key] ?? key
    }

    static func isRTL(_ lang: String) -> Bool { resolve(lang) == "ar" }

    /// Keys added for the iOS version.
    private static let extra: [String: [String: String]] = [
        "es": ["duration": "Duración", "bitrate": "Calidad del audio", "file_size": "Tamaño", "copy_id": "Copiar ID", "copied": "Copiado", "share": "Compartir", "download": "Descargar", "remove_download": "Quitar descarga", "mix": "Mix", "play_all": "Reproducir", "search_placeholder": "Canciones, artistas…", "no_downloads_yet": "Aún no has descargado música"],
        "en": ["duration": "Duration", "bitrate": "Audio quality", "file_size": "Size", "copy_id": "Copy ID", "copied": "Copied", "share": "Share", "download": "Download", "remove_download": "Remove download", "mix": "Mix", "play_all": "Play", "search_placeholder": "Songs, artists…", "no_downloads_yet": "You haven't downloaded any music yet"],
        "fr": ["duration": "Durée", "bitrate": "Qualité audio", "file_size": "Taille", "copy_id": "Copier l'ID", "copied": "Copié", "share": "Partager", "download": "Télécharger", "remove_download": "Supprimer le téléchargement", "mix": "Mix", "play_all": "Lire", "search_placeholder": "Titres, artistes…", "no_downloads_yet": "Vous n'avez encore rien téléchargé"],
        "de": ["duration": "Dauer", "bitrate": "Audioqualität", "file_size": "Größe", "copy_id": "ID kopieren", "copied": "Kopiert", "share": "Teilen", "download": "Herunterladen", "remove_download": "Download entfernen", "mix": "Mix", "play_all": "Abspielen", "search_placeholder": "Songs, Künstler…", "no_downloads_yet": "Du hast noch keine Musik heruntergeladen"],
        "it": ["duration": "Durata", "bitrate": "Qualità audio", "file_size": "Dimensione", "copy_id": "Copia ID", "copied": "Copiato", "share": "Condividi", "download": "Scarica", "remove_download": "Rimuovi download", "mix": "Mix", "play_all": "Riproduci", "search_placeholder": "Brani, artisti…", "no_downloads_yet": "Non hai ancora scaricato musica"],
        "pt": ["duration": "Duração", "bitrate": "Qualidade do áudio", "file_size": "Tamanho", "copy_id": "Copiar ID", "copied": "Copiado", "share": "Partilhar", "download": "Transferir", "remove_download": "Remover transferência", "mix": "Mix", "play_all": "Reproduzir", "search_placeholder": "Músicas, artistas…", "no_downloads_yet": "Ainda não transferiste música"],
        "ru": ["duration": "Длительность", "bitrate": "Качество звука", "file_size": "Размер", "copy_id": "Копировать ID", "copied": "Скопировано", "share": "Поделиться", "download": "Скачать", "remove_download": "Удалить загрузку", "mix": "Микс", "play_all": "Слушать", "search_placeholder": "Песни, артисты…", "no_downloads_yet": "Вы ещё ничего не скачали"],
        "ja": ["duration": "再生時間", "bitrate": "音質", "file_size": "サイズ", "copy_id": "IDをコピー", "copied": "コピーしました", "share": "共有", "download": "ダウンロード", "remove_download": "ダウンロードを削除", "mix": "ミックス", "play_all": "再生", "search_placeholder": "曲、アーティスト…", "no_downloads_yet": "まだダウンロードした曲はありません"],
        "zh": ["duration": "时长", "bitrate": "音质", "file_size": "大小", "copy_id": "复制 ID", "copied": "已复制", "share": "分享", "download": "下载", "remove_download": "删除下载", "mix": "混音", "play_all": "播放", "search_placeholder": "歌曲、艺人…", "no_downloads_yet": "你还没有下载任何音乐"],
        "hi": ["duration": "अवधि", "bitrate": "ऑडियो गुणवत्ता", "file_size": "आकार", "copy_id": "ID कॉपी करें", "copied": "कॉपी हो गया", "share": "शेयर करें", "download": "डाउनलोड करें", "remove_download": "डाउनलोड हटाएँ", "mix": "मिक्स", "play_all": "चलाएँ", "search_placeholder": "गाने, कलाकार…", "no_downloads_yet": "आपने अभी तक कोई संगीत डाउनलोड नहीं किया है"],
        "ar": ["duration": "المدة", "bitrate": "جودة الصوت", "file_size": "الحجم", "copy_id": "نسخ المعرف", "copied": "تم النسخ", "share": "مشاركة", "download": "تنزيل", "remove_download": "إزالة التنزيل", "mix": "مزيج", "play_all": "تشغيل", "search_placeholder": "أغانٍ، فنانون…", "no_downloads_yet": "لم تقم بتنزيل أي موسيقى بعد"],
    ]
}

/// Lets any view write `t("key")` using the language from settings.
struct LangKey: EnvironmentKey { static let defaultValue = "system" }
extension EnvironmentValues {
    var appLang: String {
        get { self[LangKey.self] }
        set { self[LangKey.self] = newValue }
    }
}
