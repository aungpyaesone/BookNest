@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.settings.ThemeLocalDataSource
import com.nps.booknest.core.settings.ThemeMode
import com.nps.booknest.feature.profile.presentation.profilePresentationModule
import com.nps.booknest.feature.settings.presentation.settingsPresentationModule
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import org.koin.compose.KoinIsolatedContext
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class BookNestProfileNavigationTest {
    @Test
    fun editHidesChromeAndPopDiscardsDraft() =
        runComposeUiTest {
            ProfileNavigationRobot(this).setContent(384)
            onNodeWithTag("profile_edit").performClick()
            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
            onNodeWithTag("edit_profile_name").performTextReplacement("Draft reader")
            onNodeWithTag("edit_profile_back").performClick()
            onNodeWithText("Monkey D Luffy").assertIsDisplayed()
            onNodeWithTag(AppNavigationTestTags.destinationItem(AppDestination.PROFILE)).assertIsSelected()
            onNodeWithTag("profile_edit").performClick()
            onNodeWithTag("edit_profile_name").assertTextContains("Monkey D Luffy")
        }

    @Test
    fun tabletEditHidesRailAndSettingsBackRestoresProfile() =
        runComposeUiTest {
            ProfileNavigationRobot(this).setContent(900)
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertIsDisplayed()
            onNodeWithTag("profile_settings").performClick()
            onNodeWithTag(AppNavigationTestTags.destinationItem(AppDestination.PROFILE)).assertIsSelected()
            onNodeWithTag("settings_back_action").performClick()
            onNodeWithTag("profile_screen").assertIsDisplayed()
            onNodeWithTag("profile_edit").performClick()
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertDoesNotExist()
            onNodeWithTag("edit_profile_back").performClick()
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertIsDisplayed()
        }

    @Test
    fun editBackRestoresScrolledDashboard() =
        runComposeUiTest {
            ProfileNavigationRobot(this).setContent(384)
            onNodeWithTag("profile_feed").performScrollToNode(hasText("Personal Information"))
            onNodeWithText("Personal Information").performClick()
            onNodeWithTag("edit_profile_back").performClick()
            onNodeWithText("Personal Information").assertIsDisplayed()
        }
}

private class ProfileNavigationRobot(
    private val test: ComposeUiTest,
) {
    fun setContent(width: Int) {
        val backStack = NavBackStack<NavKey>(ProfileKey)
        val settingsModule =
            module {
                single<ThemeLocalDataSource> { ProfileTestThemeSource() }
            }
        val application =
            koinApplication { modules(profilePresentationModule, settingsPresentationModule, settingsModule) }
        test.setContent {
            KoinIsolatedContext(context = application) {
                val density =
                    LocalWindowInfo.current.containerSize.width
                        .toFloat() / width
                CompositionLocalProvider(LocalDensity provides Density(density)) {
                    BookNestTheme {
                        val chrome = remember { BookNestChromeSceneStrategy({ it.toAppDestination() }, {}) }
                        NavDisplay(
                            backStack = backStack,
                            onBack = { backStack.removeLastOrNull() },
                            entryDecorators =
                                listOf(
                                    rememberSaveableStateHolderNavEntryDecorator(),
                                    rememberViewModelStoreNavEntryDecorator(),
                                ),
                            sceneDecoratorStrategies = listOf(chrome),
                            entryProvider = entryProvider { profileEntries(backStack) },
                        )
                    }
                }
            }
        }
    }
}

private class ProfileTestThemeSource : ThemeLocalDataSource {
    override val themeMode = MutableStateFlow(ThemeMode.LIGHT)

    override fun setThemeMode(themeMode: ThemeMode) {
        this.themeMode.value = themeMode
    }
}
