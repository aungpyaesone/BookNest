package com.nps.booknest.feature.profile.presentation

data class ProfileState(
    val name: String = "Monkey D Luffy",
    val email: String = "luffy.reader@gmail.com",
    val bio: String = "So many books, so little time.",
)

sealed interface ProfileAction {
    data object OnEditClick : ProfileAction

    data object OnSettingsClick : ProfileAction

    data object OnComingSoonClick : ProfileAction
}

sealed interface ProfileEvent {
    data object NavigateToEdit : ProfileEvent

    data object NavigateToSettings : ProfileEvent

    data object ShowComingSoon : ProfileEvent
}

data class EditProfileState(
    val name: String = ProfileState().name,
    val email: String = ProfileState().email,
    val bio: String = ProfileState().bio,
)

sealed interface EditProfileAction {
    data class OnNameChange(
        val name: String,
    ) : EditProfileAction

    data class OnBioChange(
        val bio: String,
    ) : EditProfileAction

    data object OnBackClick : EditProfileAction

    data object OnSaveClick : EditProfileAction

    data object OnCameraClick : EditProfileAction
}

sealed interface EditProfileEvent {
    data object NavigateBack : EditProfileEvent

    data object ShowComingSoon : EditProfileEvent
}
