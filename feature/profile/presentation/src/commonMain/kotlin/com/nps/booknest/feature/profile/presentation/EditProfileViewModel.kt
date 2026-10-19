package com.nps.booknest.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(EditProfileState())
    val state = mutableState.asStateFlow()
    private val eventChannel = Channel<EditProfileEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.OnNameChange -> mutableState.update { it.copy(name = action.name) }
            is EditProfileAction.OnBioChange -> mutableState.update { it.copy(bio = action.bio.take(100)) }
            EditProfileAction.OnBackClick -> sendEvent(EditProfileEvent.NavigateBack)
            EditProfileAction.OnSaveClick, EditProfileAction.OnCameraClick -> sendEvent(EditProfileEvent.ShowComingSoon)
        }
    }

    private fun sendEvent(event: EditProfileEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}
