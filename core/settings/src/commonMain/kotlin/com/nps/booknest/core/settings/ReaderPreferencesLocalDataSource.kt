package com.nps.booknest.core.settings

import kotlinx.coroutines.flow.StateFlow

interface ReaderPreferencesLocalDataSource {
    val preferences: StateFlow<ReaderPreferences>

    fun setThemeMode(themeMode: ReaderThemeMode)

    fun setFontSize(fontSize: ReaderFontSize)
}
