package com.nps.booknest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nps.booknest.core.settings.ThemeLocalDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    themeLocalDataSource: ThemeLocalDataSource,
) : ViewModel() {
    private val mutableState = MutableStateFlow(AppState(themeMode = themeLocalDataSource.themeMode.value))
    val state = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            themeLocalDataSource.themeMode.collectLatest { themeMode ->
                mutableState.update { it.copy(themeMode = themeMode) }
            }
        }
    }
}
