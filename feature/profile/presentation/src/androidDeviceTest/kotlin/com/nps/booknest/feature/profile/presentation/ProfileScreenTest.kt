@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.profile.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileScreenTest {
    @Test
    fun dashboardRendersAllSectionsAndDispatchesNavigation() =
        runComposeUiTest {
            val actions = mutableListOf<ProfileAction>()
            setContent { BookNestTheme { ProfileScreen(ProfileState(), actions::add) } }
            onNodeWithText("Monkey D Luffy").assertIsDisplayed()
            onNodeWithTag("profile_edit").performClick()
            onNodeWithTag("profile_settings").performClick()
            onNodeWithTag("profile_feed").performScrollToNode(hasText("Personal Information"))
            onNodeWithText("Personal Information").performClick()
            onNodeWithTag("profile_feed").performScrollToNode(hasText("About BookNest"))
            onNodeWithText("About BookNest").performClick()
            runOnIdle {
                assertEquals(
                    listOf(
                        ProfileAction.OnEditClick,
                        ProfileAction.OnSettingsClick,
                        ProfileAction.OnEditClick,
                        ProfileAction.OnComingSoonClick,
                    ),
                    actions,
                )
            }
        }

    @Test
    fun formEditsDraftAndKeepsEmailReadOnly() =
        runComposeUiTest {
            val actions = mutableListOf<EditProfileAction>()
            setContent {
                var state by remember { mutableStateOf(EditProfileState()) }
                BookNestTheme {
                    EditProfileScreen(state, { action ->
                        actions += action
                        state =
                            when (action) {
                                is EditProfileAction.OnNameChange -> state.copy(name = action.name)
                                is EditProfileAction.OnBioChange -> state.copy(bio = action.bio.take(100))
                                else -> state
                            }
                    })
                }
            }
            onNodeWithTag("edit_profile_name").performTextReplacement("Reader")
            onNodeWithTag("edit_profile_bio").performScrollTo().performTextReplacement("Reading every day")
            onNodeWithText("17/100").assertIsDisplayed()
            onNodeWithTag("edit_profile_email").assertTextContains(ProfileState().email)
            onNodeWithTag("edit_profile_save").performScrollTo().performClick()
            runOnIdle { assertEquals(EditProfileAction.OnSaveClick, actions.last()) }
        }

    @Test
    fun dashboardAndFormAdaptToPhoneTabletAndLargeText() {
        listOf(320, 360, 412, 840, 1024).forEach { width ->
            listOf(1f, 1.3f, 1.5f).forEach { scale ->
                runComposeUiTest {
                    setContent {
                        val density =
                            LocalWindowInfo.current.containerSize.width
                                .toFloat() / width
                        CompositionLocalProvider(LocalDensity provides Density(density, scale)) {
                            BookNestTheme(isDarkTheme = scale == 1.5f) { ProfileScreen(ProfileState(), {}) }
                        }
                    }
                    onNodeWithText("Monkey D Luffy").assertIsDisplayed()
                    onNodeWithTag("profile_feed").performScrollToNode(hasText("About BookNest"))
                    onNodeWithText("About BookNest").assertIsDisplayed()
                }
                runComposeUiTest {
                    setContent {
                        val density =
                            LocalWindowInfo.current.containerSize.width
                                .toFloat() / width
                        CompositionLocalProvider(LocalDensity provides Density(density, scale)) {
                            BookNestTheme(isDarkTheme = scale == 1.5f) { EditProfileScreen(EditProfileState(), {}) }
                        }
                    }
                    onNodeWithTag("edit_profile_save").performScrollTo().assertIsDisplayed()
                }
            }
        }
    }
}
