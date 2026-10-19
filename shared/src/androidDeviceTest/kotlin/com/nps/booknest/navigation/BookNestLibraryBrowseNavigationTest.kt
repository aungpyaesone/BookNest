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
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.feature.library.presentation.libraryPresentationModule
import com.nps.booknest.feature.library.presentation.navigation.LibraryOverviewKey
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.compose.KoinIsolatedContext
import org.koin.dsl.koinApplication

class BookNestLibraryBrowseNavigationTest {
    @Test
    fun readingSeeAllHidesChromeAndBackRetainsDashboardSearch() =
        runComposeUiTest {
            LibraryBrowseNavigationRobot(this).setContent(384)
            onNodeWithTag("library_overview_search").performTextInput("James")
            onNode(hasText("See all") and hasAnyAncestor(hasTestTag("library_dashboard_reading"))).performClick()
            onNodeWithTag("library_books_READING").assertIsDisplayed()
            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
            onNodeWithTag("library_browse_back").performClick()
            onNodeWithTag("library_overview_search").assertTextContains("James")
            onNodeWithTag(AppNavigationTestTags.destinationItem(AppDestination.LIBRARY)).assertIsSelected()
        }

    @Test
    fun collectionsSeeAllBackRetainsDashboardScroll() =
        runComposeUiTest {
            LibraryBrowseNavigationRobot(this).setContent(384)
            onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag("library_collections_section"))
            val before = onNodeWithTag("library_collections_section").getUnclippedBoundsInRoot()
            onNode(hasText("See all") and hasAnyAncestor(hasTestTag("library_dashboard_collections"))).performClick()
            onNodeWithTag("collections_screen").assertIsDisplayed()
            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
            onNodeWithTag("library_browse_back").performClick()
            val after = onNodeWithTag("library_collections_section").getUnclippedBoundsInRoot()
            assertEquals(before.top.value, after.top.value, 1f)
        }

    @Test
    fun myBooksAndOtherScreensHideTabletRail() =
        runComposeUiTest {
            LibraryBrowseNavigationRobot(this).setContent(1024)
            listOf(
                "My Books" to "library_books_MY_BOOKS",
                "Reading Now" to "library_books_READING",
                "Collections" to "collections_screen",
            ).forEach { (title, screen) ->
                val section =
                    when (title) {
                        "My Books" -> "library_summary_section"
                        "Collections" -> "library_collections_section"
                        else -> "library_reading_section"
                    }
                onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag(section))
                onNode(
                    hasText("See all") and
                        hasAnyAncestor(
                            hasTestTag(
                                "library_dashboard_" +
                                    when (title) {
                                        "My Books" -> "summary"
                                        "Collections" -> "collections"
                                        else -> "reading"
                                    },
                            ),
                        ),
                ).performClick()
                onNodeWithTag(screen).assertIsDisplayed()
                onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertDoesNotExist()
                onNodeWithTag("library_browse_back").performClick()
                onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertIsDisplayed()
            }
        }
}

private class LibraryBrowseNavigationRobot(
    private val test: ComposeUiTest,
) {
    fun setContent(width: Int) {
        val backStack = NavBackStack<NavKey>(LibraryOverviewKey)
        val application = koinApplication { modules(libraryPresentationModule) }
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
                            entryProvider = entryProvider { libraryBrowseEntries(backStack) },
                        )
                    }
                }
            }
        }
    }
}
