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
        "es": ["download": "Descargar", "remove_download": "Quitar descarga", "mix": "Mix", "play_all": "Reproducir", "search_placeholder": "Canciones, artistas…", "no_downloads_yet": "Aún no has descargado música"],
        "en": ["download": "Download", "remove_download": "Remove download", "mix": "Mix", "play_all": "Play", "search_placeholder": "Songs, artists…", "no_downloads_yet": "You haven't downloaded any music yet"],
        "fr": ["download": "Télécharger", "remove_download": "Supprimer le téléchargement", "mix": "Mix", "play_all": "Lire", "search_placeholder": "Titres, artistes…", "no_downloads_yet": "Vous n'avez encore rien téléchargé"],
        "de": ["download": "Herunterladen", "remove_download": "Download entfernen", "mix": "Mix", "play_all": "Abspielen", "search_placeholder": "Songs, Künstler…", "no_downloads_yet": "Du hast noch keine Musik heruntergeladen"],
        "it": ["download": "Scarica", "remove_download": "Rimuovi download", "mix": "Mix", "play_all": "Riproduci", "search_placeholder": "Brani, artisti…", "no_downloads_yet": "Non hai ancora scaricato musica"],
        "pt": ["download": "Transferir", "remove_download": "Remover transferência", "mix": "Mix", "play_all": "Reproduzir", "search_placeholder": "Músicas, artistas…", "no_downloads_yet": "Ainda não transferiste música"],
        "ru": ["download": "Скачать", "remove_download": "Удалить загрузку", "mix": "Микс", "play_all": "Слушать", "search_placeholder": "Песни, артисты…", "no_downloads_yet": "Вы ещё ничего не скачали"],
        "ja": ["download": "ダウンロード", "remove_download": "ダウンロードを削除", "mix": "ミックス", "play_all": "再生", "search_placeholder": "曲、アーティスト…", "no_downloads_yet": "まだダウンロードした曲はありません"],
        "zh": ["download": "下载", "remove_download": "删除下载", "mix": "混音", "play_all": "播放", "search_placeholder": "歌曲、艺人…", "no_downloads_yet": "你还没有下载任何音乐"],
        "hi": ["download": "डाउनलोड करें", "remove_download": "डाउनलोड हटाएँ", "mix": "मिक्स", "play_all": "चलाएँ", "search_placeholder": "गाने, कलाकार…", "no_downloads_yet": "आपने अभी तक कोई संगीत डाउनलोड नहीं किया है"],
        "ar": ["download": "تنزيل", "remove_download": "إزالة التنزيل", "mix": "مزيج", "play_all": "تشغيل", "search_placeholder": "أغانٍ، فنانون…", "no_downloads_yet": "لم تقم بتنزيل أي موسيقى بعد"],
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
