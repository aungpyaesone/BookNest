@file:OptIn(
    androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class,
    androidx.compose.ui.test.ExperimentalTestApi::class,
)

package com.nps.booknest.feature.library.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.junit.Test

class LibraryAdaptiveNavigationTest {
    @Test
    fun compactLayoutShowsOnlyCurrentDetailPane() =
        runComposeUiTest {
            setContent {
                LibraryNavigationTestContent(
                    directive = PaneScaffoldDirective.Default,
                )
            }

            onNodeWithTag(LIST_PANE_TAG).assertDoesNotExist()
            onNodeWithTag(DETAIL_PANE_TAG).assertIsDisplayed()
        }

    @Test
    fun expandedLayoutShowsListAndDetailPanes() =
        runComposeUiTest {
            setContent {
                LibraryNavigationTestContent(
                    directive =
                        PaneScaffoldDirective.Default.copy(
                            maxHorizontalPartitions = 2,
                        ),
                )
            }

            onNodeWithTag(LIST_PANE_TAG).assertIsDisplayed()
            onNodeWithTag(DETAIL_PANE_TAG).assertIsDisplayed()
        }
}

@Composable
private fun LibraryNavigationTestContent(directive: PaneScaffoldDirective) {
    val backStack =
        remember {
            mutableStateListOf<NavKey>(LibraryListKey, BookDetailKey(bookId = "book-42"))
        }
    val sceneStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(sceneStrategy),
        entryProvider =
            entryProvider {
                entry<LibraryListKey>(
                    metadata = ListDetailSceneStrategy.listPane(),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .testTag(LIST_PANE_TAG),
                    )
                }
                entry<BookDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane(),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .testTag(DETAIL_PANE_TAG),
                    )
                }
            },
    )
}

private const val LIST_PANE_TAG = "library_list_pane"
private const val DETAIL_PANE_TAG = "book_detail_pane"
