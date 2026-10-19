package com.nps.booknest.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(ProfileState())
    val state = mutableState.asStateFlow()
    private val eventChannel = Channel<ProfileEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ProfileAction) {
        val event =
            when (action) {
                ProfileAction.OnEditClick -> ProfileEvent.NavigateToEdit
                ProfileAction.OnSettingsClick -> ProfileEvent.NavigateToSettings
                ProfileAction.OnComingSoonClick -> ProfileEvent.ShowComingSoon
            }
        viewModelScope.launch { eventChannel.send(event) }
    }
}
