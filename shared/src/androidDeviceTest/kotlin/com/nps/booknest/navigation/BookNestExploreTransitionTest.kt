@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.feature.explore.presentation.navigation.CategoryBooksKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BookNestExploreTransitionTest {
    @Test
    fun detailViewportKeepsItsSizeWhileMainChromeReturns() =
        runComposeUiTest {
            val backStack = mutableStateListOf<NavKey>(ExploreKey)
            val chrome = BookNestChromeSceneStrategy({ AppDestination.EXPLORE }, {})
            mainClock.autoAdvance = false
            setContent {
                BookNestTheme {
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        sceneDecoratorStrategies = listOf(chrome),
                        sizeTransform = null,
                        transitionSpec = { bookNestForwardContentTransform() },
                        popTransitionSpec = { bookNestPopContentTransform() },
                        entryProvider =
                            entryProvider {
                                entry<ExploreKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to ExploreKey)) {
                                    Box(Modifier.fillMaxSize().testTag("explore_main_bounds"))
                                }
                                entry<CategoryBooksKey>(metadata = categoryBooksSceneMetadata) {
                                    Box(Modifier.fillMaxSize().testTag("explore_detail_bounds"))
                                }
                            },
                    )
                }
            }
            mainClock.advanceTimeByFrame()
            val mainBounds = onNodeWithTag("explore_main_bounds").getUnclippedBoundsInRoot()
            runOnIdle { backStack.add(CategoryBooksKey("fiction")) }
            mainClock.advanceTimeBy(300)
            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertDoesNotExist()
            val detailBounds = onNodeWithTag("explore_detail_bounds").getUnclippedBoundsInRoot()
            assertTrue(detailBounds.height.value >= mainBounds.height.value)
            runOnIdle { backStack.removeLastOrNull() }
            mainClock.advanceTimeBy(64)
            val outgoingBounds = onNodeWithTag("explore_detail_bounds").getUnclippedBoundsInRoot()
            assertEquals(detailBounds.width.value, outgoingBounds.width.value, 0.01f)
            assertEquals(detailBounds.height.value, outgoingBounds.height.value, 0.01f)
            mainClock.advanceTimeBy(300)
            assertEquals(mainBounds, onNodeWithTag("explore_main_bounds").getUnclippedBoundsInRoot())
        }
}
