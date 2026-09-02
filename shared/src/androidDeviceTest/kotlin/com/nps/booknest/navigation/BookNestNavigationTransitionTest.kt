@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.junit.Test

class BookNestNavigationTransitionTest {
    @Test
    fun destinationChangeKeepsBothScenesUntilFadeThroughCompletes() =
        runComposeUiTest {
            val backStack = mutableStateListOf<NavKey>(FirstKey)
            mainClock.autoAdvance = false

            setContent {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    sizeTransform = null,
                    transitionSpec = { bookNestForwardContentTransform() },
                    popTransitionSpec = { bookNestPopContentTransform() },
                    predictivePopTransitionSpec = { _ -> bookNestPopContentTransform() },
                    entryProvider =
                        entryProvider {
                            entry<FirstKey> {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .testTag(FIRST_DESTINATION_TAG),
                                )
                            }
                            entry<SecondKey> {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .testTag(SECOND_DESTINATION_TAG),
                                )
                            }
                        },
                )
            }

            mainClock.advanceTimeByFrame()
            onNodeWithTag(FIRST_DESTINATION_TAG).assertIsDisplayed()

            runOnIdle {
                backStack.clear()
                backStack.add(SecondKey)
            }
            mainClock.advanceTimeByFrame()

            onNodeWithTag(FIRST_DESTINATION_TAG).assertExists()
            onNodeWithTag(SECOND_DESTINATION_TAG).assertExists()

            mainClock.advanceTimeBy(BOOK_NEST_NAVIGATION_ENTER_DURATION_MILLIS.toLong() + 1L)
            onNodeWithTag(FIRST_DESTINATION_TAG).assertDoesNotExist()
            onNodeWithTag(SECOND_DESTINATION_TAG).assertIsDisplayed()
        }
}

private data object FirstKey : NavKey

private data object SecondKey : NavKey

private const val FIRST_DESTINATION_TAG = "first_destination"
private const val SECOND_DESTINATION_TAG = "second_destination"
