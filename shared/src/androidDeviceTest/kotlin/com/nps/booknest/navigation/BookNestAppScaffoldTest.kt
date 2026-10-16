@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BookNestAppScaffoldTest {
    @Test
    fun expandedAppShellPaintsLightThemeBehindTransparentDestination() =
        verifyExpandedAppShellBackground(
            isDarkTheme = false,
            expectedColor = Color(0xFFFCF8F4),
        )

    @Test
    fun expandedAppShellPaintsDarkThemeBehindTransparentDestination() =
        verifyExpandedAppShellBackground(
            isDarkTheme = true,
            expectedColor = Color(0xFF1B1613),
        )

    @Test
    fun compactLayoutShowsBottomNavigationAndDispatchesSelection() =
        runComposeUiTest {
            var selectedDestination = AppDestination.HOME
            var selectionCount = 0
            setContent {
                val currentDensity = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(currentDensity.density, fontScale = 1.5f),
                ) {
                    BookNestTheme {
                        BookNestAppScaffold(
                            selectedDestination = selectedDestination,
                            onDestinationSelect = {
                                selectionCount += 1
                                selectedDestination = it
                            },
                            forceExpanded = false,
                        ) {
                            Box(modifier = Modifier)
                        }
                    }
                }
            }

            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertIsDisplayed()
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertDoesNotExist()
            onNodeWithText("ကိုယ်ရေး").assertIsDisplayed()
            onNodeWithText("ပင်မ").performClick()
            runOnIdle { assertEquals(0, selectionCount) }
            onNodeWithText("စာကြည့်တိုက်").performClick()
            runOnIdle {
                assertEquals(1, selectionCount)
                assertEquals(AppDestination.LIBRARY, selectedDestination)
            }
        }

    @Test
    fun expandedLayoutShowsNavigationRail() =
        runComposeUiTest {
            setContent {
                BookNestTheme {
                    BookNestAppScaffold(
                        selectedDestination = AppDestination.HOME,
                        onDestinationSelect = {},
                        forceExpanded = true,
                        isSettingsVisible = true,
                    ) {
                        Box(modifier = Modifier)
                    }
                }
            }

            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertIsDisplayed()
            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()

            val homeBounds =
                onNodeWithTag(AppNavigationTestTags.destinationItem(AppDestination.HOME))
                    .getUnclippedBoundsInRoot()
            val exploreBounds =
                onNodeWithTag(AppNavigationTestTags.destinationItem(AppDestination.EXPLORE))
                    .getUnclippedBoundsInRoot()
            assertTrue(exploreBounds.top - homeBounds.bottom >= 8.dp)
        }

    @Test
    fun portraitSettingsHidesBottomNavigation() =
        runComposeUiTest {
            setContent {
                BookNestTheme {
                    BookNestAppScaffold(
                        selectedDestination = AppDestination.HOME,
                        onDestinationSelect = {},
                        modifier = Modifier.width(360.dp).height(800.dp),
                        forceExpanded = false,
                        isSettingsVisible = true,
                    ) {
                        Box(modifier = Modifier)
                    }
                }
            }

            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertDoesNotExist()
        }

    @Test
    fun readerHidesBottomNavigationAndRail() =
        runComposeUiTest {
            setContent {
                BookNestTheme {
                    BookNestAppScaffold(
                        selectedDestination = AppDestination.HOME,
                        onDestinationSelect = {},
                        isReaderVisible = true,
                    ) {
                        Box(modifier = Modifier)
                    }
                }
            }

            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertDoesNotExist()
        }

    private fun verifyExpandedAppShellBackground(
        isDarkTheme: Boolean,
        expectedColor: Color,
    ) = runComposeUiTest {
        setContent {
            BookNestTheme(isDarkTheme = isDarkTheme) {
                BookNestAppScaffold(
                    selectedDestination = AppDestination.EXPLORE,
                    onDestinationSelect = {},
                    forceExpanded = true,
                ) {
                    Box(modifier = Modifier)
                }
            }
        }

        val image = onNodeWithTag(AppNavigationTestTags.APP_SCAFFOLD).captureToImage()
        val pixels = image.toPixelMap()
        val backgroundColor = pixels[pixels.width - 1, pixels.height / 2]

        assertEquals(expectedColor.toArgb(), backgroundColor.toArgb())
    }
}
