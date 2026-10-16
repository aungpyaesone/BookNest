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
import com.nps.booknest.feature.home.presentation.navigation.HomeKey
import com.nps.booknest.feature.reader.presentation.navigation.ReaderKey
import org.junit.Assert.assertEquals
import org.junit.Test

class BookNestReaderTransitionTest {
    @Test
    fun readerBoundsStayFullscreenWhileHomeChromeReturns() =
        runComposeUiTest {
            val backStack = mutableStateListOf<NavKey>(HomeKey)
            val chromeStrategy = BookNestChromeSceneStrategy({ AppDestination.HOME }, {})
            mainClock.autoAdvance = false
            setContent {
                BookNestTheme {
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        sceneDecoratorStrategies = listOf(chromeStrategy),
                        sizeTransform = null,
                        transitionSpec = { bookNestForwardContentTransform() },
                        popTransitionSpec = { bookNestPopContentTransform() },
                        entryProvider =
                            entryProvider {
                                entry<HomeKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to HomeKey)) {
                                    Box(Modifier.fillMaxSize().testTag("home_content"))
                                }
                                entry<ReaderKey>(metadata = mapOf(BOOK_NEST_READER_SCENE to true)) {
                                    Box(Modifier.fillMaxSize().testTag("reader_content"))
                                }
                            },
                    )
                }
            }
            mainClock.advanceTimeByFrame()
            val homeBounds = onNodeWithTag("home_content").getUnclippedBoundsInRoot()
            runOnIdle { backStack.add(ReaderKey("mountain-morning")) }
            mainClock.advanceTimeBy(300)
            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
            val readerBounds = onNodeWithTag("reader_content").getUnclippedBoundsInRoot()

            runOnIdle { backStack.removeLastOrNull() }
            // Scene calculation and AnimatedContent need separate frames before both scenes are laid out.
            mainClock.advanceTimeBy(64)
            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertExists()
            val outgoingBounds = onNodeWithTag("reader_content").getUnclippedBoundsInRoot()
            // The intentional scale animation shifts the drawn origin; layout dimensions must not shrink.
            assertEquals(readerBounds.width.value, outgoingBounds.width.value, 0.01f)
            assertEquals(readerBounds.height.value, outgoingBounds.height.value, 0.01f)
            assertEquals(homeBounds, onNodeWithTag("home_content").getUnclippedBoundsInRoot())

            mainClock.advanceTimeBy(300)
            onNodeWithTag("reader_content").assertDoesNotExist()
            assertEquals(listOf(HomeKey), backStack.toList())
        }
}
