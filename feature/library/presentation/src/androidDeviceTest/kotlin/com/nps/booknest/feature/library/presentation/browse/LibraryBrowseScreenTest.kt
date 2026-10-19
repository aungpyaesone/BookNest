@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.library.presentation.browse

import android.graphics.Bitmap
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LibraryBrowseScreenTest {
    @Test
    fun readingSearchFiltersAndProgressSortWork() =
        runComposeUiTest {
            LibraryBrowseRobot(this).setContent(LibraryBooksMode.READING)
            onNodeWithText("3 Books").assertIsDisplayed()
            onNodeWithText("38%").assertIsDisplayed()
            onNodeWithTag("library_browse_search").assertDoesNotExist()
            onNodeWithTag("library_browse_sort").performClick()
            onNodeWithTag("library_browse_sort_PROGRESS").performClick()
            onNodeWithTag("library_browse_book_psychology").assertIsDisplayed()
            onNodeWithTag("library_browse_category_EDUCATION").performClick()
            onNodeWithTag("library_browse_empty").assertIsDisplayed()
            onNodeWithTag("library_browse_category_ALL").performClick()
            onNodeWithTag("library_browse_search_action").performClick()
            onNodeWithTag("library_browse_search").performTextInput("Morgan")
            onNodeWithText("1 Book").assertIsDisplayed()
            onNodeWithTag("library_browse_book_action_psychology").performClick()
            onNodeWithText("Coming soon").assertIsDisplayed()
        }

    @Test
    fun bookFilterDraftApplyAndResetWork() =
        runComposeUiTest {
            LibraryBrowseRobot(this).setContent(LibraryBooksMode.MY_BOOKS)
            onNodeWithTag("library_browse_filter").performClick()
            onNodeWithTag("library_filter_category_LIFESTYLE").performClick()
            onNodeWithTag("library_filter_status_FINISHED").performClick()
            onNodeWithTag("library_browse_filter_apply").performClick()
            onNodeWithText("1 Book").assertIsDisplayed()
            onNodeWithTag("library_browse_book_ikigai").assertIsDisplayed()
            onNodeWithTag("library_browse_filter").performClick()
            onNodeWithTag("library_browse_filter_reset").performClick()
            onNodeWithTag("library_browse_filter_apply").performClick()
            onNodeWithText("6 Books").assertIsDisplayed()
        }

    @Test
    fun collectionTypeFilterSearchSortAndCreateWork() =
        runComposeUiTest {
            LibraryBrowseRobot(this).setContent(null)
            onNodeWithText("6 collections").assertIsDisplayed()
            onNodeWithTag("collections_filter").performClick()
            onNodeWithTag("collection_filter_SHARED").performClick()
            onNodeWithTag("library_browse_filter_apply").performClick()
            onNodeWithText("2 collections").assertIsDisplayed()
            onNodeWithTag("collections_search").performTextInput("Japanese")
            onNodeWithText("1 collection").assertIsDisplayed()
            onNodeWithTag("library_browse_sort").performClick()
            onNodeWithTag("library_browse_sort_TITLE").performClick()
            onNodeWithTag("collections_create").performClick()
            onNodeWithText("Coming soon").assertIsDisplayed()
        }

    @Test
    fun searchFromScrolledBooksReturnsToVisibleField() =
        runComposeUiTest {
            LibraryBrowseRobot(this).setContent(LibraryBooksMode.MY_BOOKS, width = 320)
            onNodeWithTag("library_browse_grid").performScrollToNode(hasTestTag("library_browse_book_dune"))
            onNodeWithTag("library_browse_search_action").performClick()
            onNodeWithTag("library_browse_search").assertIsDisplayed().performTextInput("no-match")
            onNodeWithTag("library_browse_empty").assertIsDisplayed()
        }

    @Test
    fun phoneTabletAndLargeTextKeepListsCardsAndControlsScrollable() {
        listOf(320, 360, 412, 840, 1024).forEach { width ->
            listOf(1f, 1.3f, 1.5f).forEach { scale ->
                listOf(LibraryBooksMode.READING, LibraryBooksMode.MY_BOOKS, null).forEach { mode ->
                    runComposeUiTest {
                        LibraryBrowseRobot(this).setContent(mode, width, scale)
                        onNodeWithTag("library_browse_back").assertIsDisplayed()
                        val gridTag = if (mode == null) "collections_grid" else "library_browse_grid"
                        val cardTag =
                            when (mode) {
                                null -> "collection_later"
                                LibraryBooksMode.READING -> "library_browse_book_rich-dad"
                                else -> "library_browse_book_dune"
                            }
                        onNodeWithTag(gridTag).performScrollToNode(hasTestTag(cardTag))
                        val card = onNodeWithTag(cardTag).getUnclippedBoundsInRoot()
                        val grid = onNodeWithTag(gridTag).getUnclippedBoundsInRoot()
                        assertTrue(card.left.value >= grid.left.value - 1f)
                        assertTrue(card.right.value <= grid.right.value + 1f)
                        if (mode == null) onNodeWithTag("collections_create").assertIsDisplayed()
                    }
                }
            }
        }
    }

    @Test
    fun captureCompactReferenceScreensAndCheckTwoColumnCollections() {
        listOf(LibraryBooksMode.READING, LibraryBooksMode.MY_BOOKS, null).forEach { mode ->
            runComposeUiTest {
                LibraryBrowseRobot(this).setContent(mode, width = 412)
                if (mode == null) {
                    val first = onNodeWithTag("collection_personal").fetchSemanticsNode().boundsInRoot
                    val second = onNodeWithTag("collection_business").fetchSemanticsNode().boundsInRoot
                    assertEquals(first.top, second.top, 1f)
                }
                val tag = if (mode == null) "collections_screen" else "library_books_${mode.name}"
                val context = InstrumentationRegistry.getInstrumentation().context
                val file = File(context.externalCacheDir, "seeall-${mode?.name ?: "COLLECTIONS"}.png")
                file.outputStream().use {
                    onNodeWithTag(tag).captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
        }
    }
}

private class LibraryBrowseRobot(
    private val test: ComposeUiTest,
) {
    fun setContent(
        mode: LibraryBooksMode?,
        width: Int = 384,
        scale: Float = 1f,
    ) = apply {
        val reading = ReadingNowViewModel()
        val books = MyBooksViewModel()
        val allBooks = AllBooksViewModel()
        val collections = CollectionsViewModel()
        test.setContent {
            val density =
                LocalWindowInfo.current.containerSize.width
                    .toFloat() / width
            CompositionLocalProvider(LocalDensity provides Density(density, scale)) {
                BookNestTheme(isDarkTheme = scale == 1.5f) {
                    when (mode) {
                        LibraryBooksMode.READING -> ReadingNowRoot({}, viewModel = reading)
                        LibraryBooksMode.MY_BOOKS -> MyBooksRoot({}, viewModel = books)
                        LibraryBooksMode.ALL_BOOKS -> AllBooksRoot({}, viewModel = allBooks)
                        null -> CollectionsRoot({}, viewModel = collections)
                    }
                }
            }
        }
    }
}
