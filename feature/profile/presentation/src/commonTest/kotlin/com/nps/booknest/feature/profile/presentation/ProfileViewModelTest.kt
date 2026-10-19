package com.nps.booknest.feature.profile.presentation

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @Test
    fun `dashboard routes edit settings and deferred actions without changing samples`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val viewModel = ProfileViewModel()
                viewModel.events.test {
                    viewModel.onAction(ProfileAction.OnEditClick)
                    assertEquals(ProfileEvent.NavigateToEdit, awaitItem())
                    viewModel.onAction(ProfileAction.OnSettingsClick)
                    assertEquals(ProfileEvent.NavigateToSettings, awaitItem())
                    viewModel.onAction(ProfileAction.OnComingSoonClick)
                    assertEquals(ProfileEvent.ShowComingSoon, awaitItem())
                }
                assertEquals(ProfileState(), viewModel.state.value)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `edit updates draft limits bio and never saves dashboard data`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val viewModel = EditProfileViewModel()
                viewModel.onAction(EditProfileAction.OnNameChange("Reader"))
                viewModel.onAction(EditProfileAction.OnBioChange("က".repeat(120)))
                assertEquals("Reader", viewModel.state.value.name)
                assertEquals(100, viewModel.state.value.bio.length)
                assertEquals(ProfileState().email, viewModel.state.value.email)
                viewModel.events.test {
                    viewModel.onAction(EditProfileAction.OnSaveClick)
                    assertEquals(EditProfileEvent.ShowComingSoon, awaitItem())
                    viewModel.onAction(EditProfileAction.OnCameraClick)
                    assertEquals(EditProfileEvent.ShowComingSoon, awaitItem())
                    viewModel.onAction(EditProfileAction.OnBackClick)
                    assertEquals(EditProfileEvent.NavigateBack, awaitItem())
                }
                assertEquals(ProfileState(), ProfileViewModel().state.value)
                assertEquals(EditProfileState(), EditProfileViewModel().state.value)
            } finally {
                Dispatchers.resetMain()
            }
        }
}
