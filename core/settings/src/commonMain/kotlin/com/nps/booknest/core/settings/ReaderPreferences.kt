package com.nps.booknest.core.settings

data class ReaderPreferences(
    val themeMode: ReaderThemeMode = ReaderThemeMode.LIGHT,
    val fontSize: ReaderFontSize = ReaderFontSize.MEDIUM,
)
