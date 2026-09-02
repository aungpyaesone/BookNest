@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Test

class HomeScreenTest {
    @Test
    fun compactLayoutUsesMobileFeed() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(
                layoutMode = HomeLayoutMode.COMPACT,
                width = 360.dp,
            )

            onNodeWithTag(HomeTestTags.COMPACT_FEED).assertIsDisplayed()
            onNodeWithTag(HomeTestTags.ADAPTIVE_FEED).assertDoesNotExist()
        }

    @Test
    fun expandedLayoutUsesAdaptiveFeed() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(
                layoutMode = HomeLayoutMode.EXPANDED,
                width = 900.dp,
            )

            onNodeWithTag(HomeTestTags.ADAPTIVE_FEED).assertIsDisplayed()
            onNodeWithTag(HomeTestTags.COMPACT_FEED).assertDoesNotExist()
        }

    @Test
    fun visibleFilterStateShowsFilterSheet() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(
                layoutMode = HomeLayoutMode.COMPACT,
                state = HomeState(isFilterSheetVisible = true),
                width = 384.dp,
            )

            onNodeWithTag(HomeTestTags.FILTER_SHEET).assertIsDisplayed()
        }

    @Test
    fun compactTypographyKeepsActionsVisibleAtDefaultFontScale() =
        verifyCompactTypography(fontScale = 1f, width = 360.dp, isDarkTheme = false)

    @Test
    fun compactTypographyKeepsActionsVisibleAtSamsungFontScale() =
        verifyCompactTypography(fontScale = 1.1f, width = 384.dp, isDarkTheme = true)

    @Test
    fun compactTypographyKeepsActionsVisibleAtLargeFontScale() =
        verifyCompactTypography(fontScale = 1.3f, width = 360.dp, isDarkTheme = false)

    @Test
    fun compactTypographyKeepsActionsVisibleAtExtraLargeFontScale() =
        verifyCompactTypography(fontScale = 1.5f, width = 384.dp, isDarkTheme = true)

    @Test
    fun mediumTypographyUsesAdaptiveFeedAtLargeFontScale() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(
                layoutMode = HomeLayoutMode.MEDIUM,
                width = 600.dp,
                fontScale = 1.3f,
            )

            onNodeWithTag(HomeTestTags.ADAPTIVE_FEED).assertIsDisplayed()
            onNodeWithTag(HomeTestTags.COMPACT_FEED).assertDoesNotExist()
        }

    private fun verifyCompactTypography(
        fontScale: Float,
        width: Dp,
        isDarkTheme: Boolean,
    ) = runComposeUiTest {
        HomeScreenRobot(this).setContent(
            layoutMode = HomeLayoutMode.COMPACT,
            width = width,
            fontScale = fontScale,
            isDarkTheme = isDarkTheme,
        )

        onNodeWithTag(HomeTestTags.HERO_CARD).assertIsDisplayed()
        onNodeWithTag(HomeTestTags.HERO_ACTION).assertIsDisplayed()

        onNodeWithTag(HomeTestTags.COMPACT_FEED)
            .performScrollToNode(hasTestTag(HomeTestTags.CONTINUE_READING_CARD))
        onNodeWithTag(HomeTestTags.CONTINUE_READING_CARD).assertIsDisplayed()
        onNodeWithTag(HomeTestTags.READ_NOW_ACTION).assertIsDisplayed()

        onNodeWithTag(HomeTestTags.COMPACT_FEED)
            .performScrollToNode(hasTestTag(HomeTestTags.PREMIUM_BANNER))
        onNodeWithTag(HomeTestTags.PREMIUM_BANNER).assertIsDisplayed()
        onNodeWithTag(HomeTestTags.UPGRADE_ACTION).assertIsDisplayed()
    }
}

internal class HomeScreenRobot(
    private val testScope: androidx.compose.ui.test.ComposeUiTest,
) {
    fun setContent(
        layoutMode: HomeLayoutMode,
        state: HomeState = HomeState(),
        width: Dp,
        fontScale: Float = 1f,
        isDarkTheme: Boolean = false,
        onAction: (HomeAction) -> Unit = {},
    ) = apply {
        testScope.setContent {
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(currentDensity.density, fontScale),
            ) {
                BookNestTheme(isDarkTheme = isDarkTheme) {
                    Box(modifier = Modifier.width(width).fillMaxHeight()) {
                        HomeScreen(
                            state = state,
                            onAction = onAction,
                            layoutMode = layoutMode,
                        )
                    }
                }
            }
        }
    }
}
