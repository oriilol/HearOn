import SwiftUI

struct SettingsScreen: View {
    @State private var settings = AppSettings.shared
    @State private var confirmClearCache = false
    @State private var updateState: String?
    private let downloads = DownloadManager.shared

    private var version: String { Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "" }

    var body: some View {
        NavigationStack {
            List {
                NavigationLink { appearance } label: {
                    Label { row(t("appearance"), t("appearance_desc")) } icon: { Image(systemName: "paintpalette") }
                }
                NavigationLink { playback } label: {
                    Label { row(t("player_and_sound"), t("player_desc")) } icon: { Image(systemName: "waveform") }
                }
                NavigationLink { storage } label: {
                    Label { row(t("storage_and_data"), t("storage_desc")) } icon: { Image(systemName: "internaldrive") }
                }
                NavigationLink { about } label: {
                    Label { row(t("about_hearon"), "\(t("current_version")) \(version)") } icon: { Image(systemName: "info.circle") }
                }
            }
            .navigationTitle(t("settings"))
        }
    }

    private func row(_ title: String, _ subtitle: String) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(title).appFont(.body, .semibold)
            Text(subtitle).appFont(.subheadline).foregroundStyle(.secondary)
        }
    }

    // MARK: Pages

    private var appearance: some View {
        Form {
            Picker(t("language"), selection: $settings.lang) {
                Text(t("lang_system")).tag("system")
                ForEach(L.supported, id: \.code) { Text($0.name).tag($0.code) }
            }
            Section(t("adaptive_colors")) {
                Toggle(isOn: $settings.dynamicColors) { row(t("dynamic_theme"), t("dynamic_theme_desc")) }
            }
            Section(t("app_theme")) {
                Picker(t("app_theme"), selection: $settings.theme) {
                    Text(t("theme_system")).tag(ThemeMode.system)
                    Text(t("theme_light")).tag(ThemeMode.light)
                    Text(t("theme_dark")).tag(ThemeMode.dark)
                }
                .pickerStyle(.inline)
                .labelsHidden()
            }
        }
        .navigationTitle(t("appearance"))
    }

    private var playback: some View {
        Form {
            Section(t("player")) {
                Picker(t("sound_quality"), selection: $settings.audioQuality) {
                    Text(t("quality_high")).tag(AudioQuality.high)
                    Text(t("quality_normal")).tag(AudioQuality.normal)
                    Text(t("quality_low")).tag(AudioQuality.low)
                }
                Toggle(isOn: $settings.crossfadeEnabled) { row(t("crossfade"), t("crossfade_desc")) }
                VStack(alignment: .leading) {
                    Text(t("crossfade_duration"))
                    Text(settings.crossfadeEnabled ? "\(Int(settings.crossfadeDuration)) s" : t("disabled"))
                        .appFont(.subheadline).foregroundStyle(.secondary)
                    Slider(value: $settings.crossfadeDuration, in: 1...10, step: 1)
                }
                .disabled(!settings.crossfadeEnabled)
            }
        }
        .navigationTitle(t("player_and_sound"))
    }

    private var storage: some View {
        Form {
            Section(t("invisible_audio_cache")) {
                LabeledContent(t("auto_saved_music"),
                               value: downloads.tracks.isEmpty ? "0 MB" : "\(downloads.tracks.count) \(t("tracks")) (\(downloads.totalBytes / 1_048_576) MB)")
                Button(t("clear_audio_cache"), role: .destructive) { confirmClearCache = true }
            }
            Section(t("images_and_data")) {
                VStack(alignment: .leading) {
                    Text(t("cover_limit"))
                    Text("\(Int(settings.maxImageCacheMB)) MB").appFont(.subheadline).foregroundStyle(.secondary)
                    Slider(value: $settings.maxImageCacheMB, in: 128...1024, step: 64)
                }
                Button(t("clear_covers")) { ImageCache.clear() }
                Toggle(isOn: $settings.dataSaver) { row(t("data_saver"), t("data_saver_desc")) }
            }
            Section(t("clear_history")) {
                Button(t("clear_history"), role: .destructive) {
                    LibraryStore.shared.clearHistory()
                    PlayerController.shared.toast = t("history_cleared")
                }
            }
        }
        .navigationTitle(t("storage_and_data"))
        .alert(t("clear_cache_title"), isPresented: $confirmClearCache) {
            Button(t("cancel"), role: .cancel) {}
            Button(t("delete"), role: .destructive) { downloads.clearAll() }
        } message: {
            Text(t("clear_cache_warning"))
        }
    }

    private var about: some View {
        Form {
            Section {
                VStack(spacing: 8) {
                    Image("Logo").resizable().scaledToFit().frame(width: 80, height: 80)
                    Text("HearOn").appFont(.largeTitle, .bold)
                    Text("made w/ love from 🇪🇸 <3").foregroundStyle(.secondary)
                    Link("github.com/oriilol", destination: URL(string: "https://github.com/oriilol")!)
                }
                .frame(maxWidth: .infinity)
                .listRowBackground(Color.clear)
            }
            Section(t("version_and_updates")) {
                LabeledContent(t("current_version"), value: "v\(version)")
                Toggle(t("notify_updates"), isOn: $settings.notifyUpdate)
                Button(updateState ?? t("check_updates")) {
                    Task {
                        updateState = t("searching")
                        let latest = await YTMusicClient.latestVersion()
                        if let latest, latest.compare(version, options: .numeric) == .orderedDescending {
                            updateState = t("update_available") + latest
                        } else {
                            updateState = t("latest_version")
                        }
                        try? await Task.sleep(for: .seconds(3))
                        updateState = nil
                    }
                }
            }
        }
        .navigationTitle(t("about_hearon"))
    }
}
