package com.nps.booknest.feature.settings.presentation

import com.nps.booknest.core.settings.ThemeMode

object SettingsTestTags {
    const val SCREEN = "settings_screen"
    const val APP_BAR = "settings_app_bar"
    const val BACK_ACTION = "settings_back_action"
    const val TITLE = "settings_title"

    fun themeOption(themeMode: ThemeMode) = "settings_theme_${themeMode.name.lowercase()}"
}
