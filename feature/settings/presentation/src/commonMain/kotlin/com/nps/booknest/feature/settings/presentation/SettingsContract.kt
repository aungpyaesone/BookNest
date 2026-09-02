package com.nps.booknest.feature.settings.presentation

import com.nps.booknest.core.settings.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
)

sealed interface SettingsAction {
    data class OnThemeModeSelect(
        val themeMode: ThemeMode,
    ) : SettingsAction

    data object OnBackClick : SettingsAction
}

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
}
