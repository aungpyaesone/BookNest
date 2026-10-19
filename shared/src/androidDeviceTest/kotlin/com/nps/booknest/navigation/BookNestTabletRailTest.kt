@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Assert.assertTrue
import org.junit.Test

class BookNestTabletRailTest {
    @Test
    fun burmeseLibraryLabelStaysInsideRailWithLargeTabletText() {
        listOf(600, 840, 1180).forEach { width ->
            listOf(1f, 1.3f, 1.5f).forEach { fontScale ->
                runComposeUiTest {
                    setContent {
                        val density =
                            LocalWindowInfo.current.containerSize.width
                                .toFloat() / width
                        CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) {
                            BookNestTheme {
                                BookNestNavigationRail(AppDestination.HOME, {})
                            }
                        }
                    }
                    val rail = onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).getUnclippedBoundsInRoot()
                    val label = onNodeWithText("စာကြည့်တိုက်", useUnmergedTree = true).getUnclippedBoundsInRoot()
                    assertTrue(label.left >= rail.left)
                    assertTrue(label.right <= rail.right)
                }
            }
        }
    }
}
