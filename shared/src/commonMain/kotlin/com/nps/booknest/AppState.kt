package com.nps.booknest

import com.nps.booknest.core.settings.ThemeMode

data class AppState(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
)
