package com.nps.booknest.core.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal const val READER_THEME_MODE_KEY = "booknest_reader_theme_mode"
internal const val READER_FONT_SIZE_KEY = "booknest_reader_font_size"

internal class DefaultReaderPreferencesLocalDataSource(
    private val storage: SettingsStorage,
) : ReaderPreferencesLocalDataSource {
    private val mutablePreferences =
        MutableStateFlow(
            ReaderPreferences(
                themeMode = ReaderThemeMode.fromStorageValue(storage.readString(READER_THEME_MODE_KEY)),
                fontSize = ReaderFontSize.fromStorageValue(storage.readString(READER_FONT_SIZE_KEY)),
            ),
        )

    override val preferences = mutablePreferences.asStateFlow()

    override fun setThemeMode(themeMode: ReaderThemeMode) {
        storage.writeString(READER_THEME_MODE_KEY, themeMode.storageValue)
        mutablePreferences.update { preferences -> preferences.copy(themeMode = themeMode) }
    }

    override fun setFontSize(fontSize: ReaderFontSize) {
        storage.writeString(READER_FONT_SIZE_KEY, fontSize.storageValue)
        mutablePreferences.update { preferences -> preferences.copy(fontSize = fontSize) }
    }
}
