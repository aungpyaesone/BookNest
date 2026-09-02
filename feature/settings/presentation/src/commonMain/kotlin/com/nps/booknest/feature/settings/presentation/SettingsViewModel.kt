package com.nps.booknest.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nps.booknest.core.settings.ThemeLocalDataSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val themeLocalDataSource: ThemeLocalDataSource,
) : ViewModel() {
    private val mutableState = MutableStateFlow(SettingsState(themeMode = themeLocalDataSource.themeMode.value))
    val state = mutableState.asStateFlow()

    private val eventChannel = Channel<SettingsEvent>(capacity = Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            themeLocalDataSource.themeMode.collectLatest { themeMode ->
                mutableState.update { it.copy(themeMode = themeMode) }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnThemeModeSelect -> {
                themeLocalDataSource.setThemeMode(action.themeMode)
            }

            SettingsAction.OnBackClick -> {
                viewModelScope.launch {
                    eventChannel.send(SettingsEvent.NavigateBack)
                }
            }
        }
    }
}
