package com.nps.booknest.core.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal const val THEME_MODE_KEY = "booknest_theme_mode"

internal interface ThemeSettingsStorage {
    fun readString(key: String): String?

    fun writeString(
        key: String,
        value: String,
    )
}

internal class DefaultThemeLocalDataSource(
    private val storage: ThemeSettingsStorage,
) : ThemeLocalDataSource {
    private val mutableThemeMode =
        MutableStateFlow(
            ThemeMode.fromStorageValue(storage.readString(THEME_MODE_KEY)),
        )

    override val themeMode = mutableThemeMode.asStateFlow()

    override fun setThemeMode(themeMode: ThemeMode) {
        storage.writeString(THEME_MODE_KEY, themeMode.storageValue)
        mutableThemeMode.update { themeMode }
    }
}
