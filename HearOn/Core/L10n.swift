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
        "es": ["play_next": "Reproducir a continuación", "add_to_queue": "Añadir a la cola", "added_to_queue": "Añadida a la cola", "will_play_next": "Sonará a continuación", "sleep_timer": "Temporizador", "sleep_end_of_track": "Se parará al acabar la canción", "end_of_track": "Al acabar la canción", "sleep_in": "Se parará en %@", "up_next": "A continuación", "turn_off": "Desactivar", "duration": "Duración", "bitrate": "Calidad del audio", "file_size": "Tamaño", "copy_id": "Copiar ID", "copied": "Copiado", "share": "Compartir", "download": "Descargar", "remove_download": "Quitar descarga", "mix": "Mix", "play_all": "Reproducir", "search_placeholder": "Canciones, artistas…", "no_downloads_yet": "Aún no has descargado música"],
        "en": ["play_next": "Play next", "add_to_queue": "Add to queue", "added_to_queue": "Added to queue", "will_play_next": "Will play next", "sleep_timer": "Sleep timer", "sleep_end_of_track": "Will stop at the end of the song", "end_of_track": "End of song", "sleep_in": "Will stop in %@", "up_next": "Up next", "turn_off": "Turn off", "duration": "Duration", "bitrate": "Audio quality", "file_size": "Size", "copy_id": "Copy ID", "copied": "Copied", "share": "Share", "download": "Download", "remove_download": "Remove download", "mix": "Mix", "play_all": "Play", "search_placeholder": "Songs, artists…", "no_downloads_yet": "You haven't downloaded any music yet"],
        "fr": ["play_next": "Lire ensuite", "add_to_queue": "Ajouter à la file", "added_to_queue": "Ajouté à la file", "will_play_next": "Sera lu ensuite", "sleep_timer": "Minuterie", "sleep_end_of_track": "S'arrêtera à la fin du titre", "end_of_track": "Fin du titre", "sleep_in": "S'arrêtera dans %@", "up_next": "À suivre", "turn_off": "Désactiver", "duration": "Durée", "bitrate": "Qualité audio", "file_size": "Taille", "copy_id": "Copier l'ID", "copied": "Copié", "share": "Partager", "download": "Télécharger", "remove_download": "Supprimer le téléchargement", "mix": "Mix", "play_all": "Lire", "search_placeholder": "Titres, artistes…", "no_downloads_yet": "Vous n'avez encore rien téléchargé"],
        "de": ["play_next": "Als Nächstes spielen", "add_to_queue": "Zur Warteschlange", "added_to_queue": "Zur Warteschlange hinzugefügt", "will_play_next": "Wird als Nächstes gespielt", "sleep_timer": "Sleep-Timer", "sleep_end_of_track": "Stoppt am Ende des Songs", "end_of_track": "Ende des Songs", "sleep_in": "Stoppt in %@", "up_next": "Als Nächstes", "turn_off": "Ausschalten", "duration": "Dauer", "bitrate": "Audioqualität", "file_size": "Größe", "copy_id": "ID kopieren", "copied": "Kopiert", "share": "Teilen", "download": "Herunterladen", "remove_download": "Download entfernen", "mix": "Mix", "play_all": "Abspielen", "search_placeholder": "Songs, Künstler…", "no_downloads_yet": "Du hast noch keine Musik heruntergeladen"],
        "it": ["play_next": "Riproduci dopo", "add_to_queue": "Aggiungi alla coda", "added_to_queue": "Aggiunto alla coda", "will_play_next": "Sarà riprodotto dopo", "sleep_timer": "Timer di spegnimento", "sleep_end_of_track": "Si fermerà alla fine del brano", "end_of_track": "Fine del brano", "sleep_in": "Si fermerà tra %@", "up_next": "A seguire", "turn_off": "Disattiva", "duration": "Durata", "bitrate": "Qualità audio", "file_size": "Dimensione", "copy_id": "Copia ID", "copied": "Copiato", "share": "Condividi", "download": "Scarica", "remove_download": "Rimuovi download", "mix": "Mix", "play_all": "Riproduci", "search_placeholder": "Brani, artisti…", "no_downloads_yet": "Non hai ancora scaricato musica"],
        "pt": ["play_next": "Reproduzir a seguir", "add_to_queue": "Adicionar à fila", "added_to_queue": "Adicionada à fila", "will_play_next": "Vai tocar a seguir", "sleep_timer": "Temporizador", "sleep_end_of_track": "Vai parar no fim da música", "end_of_track": "Fim da música", "sleep_in": "Vai parar dentro de %@", "up_next": "A seguir", "turn_off": "Desativar", "duration": "Duração", "bitrate": "Qualidade do áudio", "file_size": "Tamanho", "copy_id": "Copiar ID", "copied": "Copiado", "share": "Partilhar", "download": "Transferir", "remove_download": "Remover transferência", "mix": "Mix", "play_all": "Reproduzir", "search_placeholder": "Músicas, artistas…", "no_downloads_yet": "Ainda não transferiste música"],
        "ru": ["play_next": "Играть следующим", "add_to_queue": "Добавить в очередь", "added_to_queue": "Добавлено в очередь", "will_play_next": "Будет играть следующим", "sleep_timer": "Таймер сна", "sleep_end_of_track": "Остановится в конце песни", "end_of_track": "В конце песни", "sleep_in": "Остановится через %@", "up_next": "Далее", "turn_off": "Выключить", "duration": "Длительность", "bitrate": "Качество звука", "file_size": "Размер", "copy_id": "Копировать ID", "copied": "Скопировано", "share": "Поделиться", "download": "Скачать", "remove_download": "Удалить загрузку", "mix": "Микс", "play_all": "Слушать", "search_placeholder": "Песни, артисты…", "no_downloads_yet": "Вы ещё ничего не скачали"],
        "ja": ["play_next": "次に再生", "add_to_queue": "キューに追加", "added_to_queue": "キューに追加しました", "will_play_next": "次に再生されます", "sleep_timer": "スリープタイマー", "sleep_end_of_track": "曲の終わりで停止します", "end_of_track": "曲の終わり", "sleep_in": "%@後に停止します", "up_next": "次の曲", "turn_off": "オフにする", "duration": "再生時間", "bitrate": "音質", "file_size": "サイズ", "copy_id": "IDをコピー", "copied": "コピーしました", "share": "共有", "download": "ダウンロード", "remove_download": "ダウンロードを削除", "mix": "ミックス", "play_all": "再生", "search_placeholder": "曲、アーティスト…", "no_downloads_yet": "まだダウンロードした曲はありません"],
        "zh": ["play_next": "下一首播放", "add_to_queue": "添加到队列", "added_to_queue": "已添加到队列", "will_play_next": "将在下一首播放", "sleep_timer": "睡眠定时", "sleep_end_of_track": "将在本曲结束时停止", "end_of_track": "本曲结束时", "sleep_in": "将在 %@ 后停止", "up_next": "接下来播放", "turn_off": "关闭", "duration": "时长", "bitrate": "音质", "file_size": "大小", "copy_id": "复制 ID", "copied": "已复制", "share": "分享", "download": "下载", "remove_download": "删除下载", "mix": "混音", "play_all": "播放", "search_placeholder": "歌曲、艺人…", "no_downloads_yet": "你还没有下载任何音乐"],
        "hi": ["play_next": "अगला चलाएँ", "add_to_queue": "कतार में जोड़ें", "added_to_queue": "कतार में जोड़ा गया", "will_play_next": "अगला चलेगा", "sleep_timer": "स्लीप टाइमर", "sleep_end_of_track": "गाना खत्म होने पर रुकेगा", "end_of_track": "गाने के अंत में", "sleep_in": "%@ में रुकेगा", "up_next": "आगे", "turn_off": "बंद करें", "duration": "अवधि", "bitrate": "ऑडियो गुणवत्ता", "file_size": "आकार", "copy_id": "ID कॉपी करें", "copied": "कॉपी हो गया", "share": "शेयर करें", "download": "डाउनलोड करें", "remove_download": "डाउनलोड हटाएँ", "mix": "मिक्स", "play_all": "चलाएँ", "search_placeholder": "गाने, कलाकार…", "no_downloads_yet": "आपने अभी तक कोई संगीत डाउनलोड नहीं किया है"],
        "ar": ["play_next": "تشغيل التالي", "add_to_queue": "إضافة إلى قائمة الانتظار", "added_to_queue": "أُضيفت إلى قائمة الانتظار", "will_play_next": "ستُشغَّل تاليًا", "sleep_timer": "مؤقت النوم", "sleep_end_of_track": "سيتوقف عند نهاية الأغنية", "end_of_track": "نهاية الأغنية", "sleep_in": "سيتوقف بعد %@", "up_next": "التالي", "turn_off": "إيقاف", "duration": "المدة", "bitrate": "جودة الصوت", "file_size": "الحجم", "copy_id": "نسخ المعرف", "copied": "تم النسخ", "share": "مشاركة", "download": "تنزيل", "remove_download": "إزالة التنزيل", "mix": "مزيج", "play_all": "تشغيل", "search_placeholder": "أغانٍ، فنانون…", "no_downloads_yet": "لم تقم بتنزيل أي موسيقى بعد"],
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
