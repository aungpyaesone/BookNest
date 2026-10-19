@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AllBooksScreenTest {
    @Test
    fun togglingLayoutKeepsSearchAndFilterAndReturnsToTop() =
        runComposeUiTest {
            AllBooksRobot(this).setContent()
            onNodeWithText("6 books in your library").assertIsDisplayed()
            onNodeWithTag("all_books_grid_toggle").assertIsOn()
            onNodeWithTag("library_browse_status_READING").performScrollTo().performClick()
            onNodeWithText("3 Books").assertIsDisplayed()
            onNodeWithTag("library_browse_search").performTextInput("James")
            onNodeWithText("1 Book").assertIsDisplayed()
            onNodeWithTag("all_books_list_toggle").performClick()
            onNodeWithTag("library_browse_book_atomic").assertIsDisplayed()
            onNodeWithTag("all_books_grid_toggle").performClick()
            onNodeWithTag("all_books_card_atomic").assertIsDisplayed()
            onNodeWithText("6 books in your library").assertIsDisplayed()
            onNodeWithTag("all_books_more_atomic").performClick()
            onNodeWithText("Coming soon").assertIsDisplayed()
        }

    @Test
    fun filterAndSortWorkAndToolbarSearchReturnsToField() =
        runComposeUiTest {
            AllBooksRobot(this).setContent()
            onNodeWithTag("library_browse_filter").performClick()
            onNodeWithTag("library_filter_status_FINISHED").performClick()
            onNodeWithTag("library_browse_filter_apply").performClick()
            onNodeWithText("2 Books").assertIsDisplayed()
            onNodeWithTag("library_browse_sort").performClick()
            onNodeWithTag("library_browse_sort_TITLE").performClick()
            onNodeWithTag("all_books_card_dune").assertIsDisplayed()
            onNodeWithTag("library_browse_search_action").performClick()
            onNodeWithTag("library_browse_search").performTextInput("no match")
            onNodeWithTag("library_browse_empty").assertIsDisplayed()
            onNodeWithText("0 Books").assertIsDisplayed()
        }

    @Test
    fun phoneTabletAndLargeTextKeepGridAndListWithinContent() {
        listOf(320, 360, 412, 840, 1024).forEach { width ->
            listOf(1f, 1.3f, 1.5f).forEach { scale ->
                runComposeUiTest {
                    AllBooksRobot(this).setContent(width, scale)
                    onNodeWithText("All").assertIsDisplayed()
                    onNodeWithText("All\n6").assertDoesNotExist()
                    val grid = onNodeWithTag("library_browse_grid").getUnclippedBoundsInRoot()
                    val first = onNodeWithTag("all_books_card_ikigai").getUnclippedBoundsInRoot()
                    val second = onNodeWithTag("all_books_card_psychology").getUnclippedBoundsInRoot()
                    assertEquals(first.top.value, second.top.value, 1f)
                    assertTrue(first.left >= grid.left && second.right <= grid.right)
                    if (width < 600 && scale < 1.3f) {
                        val third = onNodeWithTag("all_books_card_atomic").getUnclippedBoundsInRoot()
                        assertEquals(first.top.value, third.top.value, 1f)
                    }
                    onNodeWithTag("library_browse_grid").performScrollToNode(hasTestTag("all_books_card_dune"))
                    onNodeWithTag("all_books_card_dune").assertIsDisplayed()
                    onNodeWithTag("library_browse_search_action").performClick()
                    onNodeWithTag("all_books_list_toggle").performClick()
                    onNodeWithTag("library_browse_book_ikigai").assertIsDisplayed()
                    val row = onNodeWithTag("library_browse_book_ikigai").getUnclippedBoundsInRoot()
                    assertTrue(row.left >= grid.left && row.right <= grid.right)
                }
            }
        }
    }
}

private class AllBooksRobot(
    private val test: ComposeUiTest,
) {
    fun setContent(
        width: Int = 412,
        scale: Float = 1f,
    ) {
        val viewModel = AllBooksViewModel()
        test.setContent {
            val density =
                LocalWindowInfo.current.containerSize.width
                    .toFloat() / width
            CompositionLocalProvider(LocalDensity provides Density(density, scale)) {
                BookNestTheme(isDarkTheme = scale == 1.5f) {
                    AllBooksRoot({}, viewModel = viewModel)
                }
            }
        }
    }
}
