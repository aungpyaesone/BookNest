package com.nps.booknest.core.settings

import kotlinx.coroutines.flow.StateFlow

interface ThemeLocalDataSource {
    val themeMode: StateFlow<ThemeMode>

    fun setThemeMode(themeMode: ThemeMode)
}
