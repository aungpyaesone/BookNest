@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.settings.presentation

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.settings.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsScreenTest {
    @Test
    fun themeOptionsShowCurrentSelectionAndDispatchChange() =
        runComposeUiTest {
            var selectedThemeMode: ThemeMode? = null
            setContent {
                BookNestTheme {
                    SettingsScreen(
                        state = SettingsState(themeMode = ThemeMode.LIGHT),
                        onAction = { action ->
                            if (action is SettingsAction.OnThemeModeSelect) {
                                selectedThemeMode = action.themeMode
                            }
                        },
                    )
                }
            }

            onNodeWithTag(SettingsTestTags.SCREEN).assertIsDisplayed()
            onNodeWithText("အမှောင်").performClick()
            runOnIdle { assertEquals(ThemeMode.DARK, selectedThemeMode) }
        }

    @Test
    fun appBarUsesSingleSafeAreaAndDispatchesBack() =
        runComposeUiTest {
            var backClickCount = 0
            setContent {
                BookNestTheme {
                    SettingsScreen(
                        state = SettingsState(themeMode = ThemeMode.LIGHT),
                        onAction = { action ->
                            if (action == SettingsAction.OnBackClick) backClickCount += 1
                        },
                    )
                }
            }

            onNodeWithTag(SettingsTestTags.APP_BAR)
                .assertIsDisplayed()
                .assertHeightIsEqualTo(64.dp)
            onNodeWithTag(SettingsTestTags.TITLE).assertIsDisplayed()
            onNodeWithTag(SettingsTestTags.BACK_ACTION).performClick()
            runOnIdle { assertEquals(1, backClickCount) }
        }
}
