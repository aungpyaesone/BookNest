@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.library.presentation.browse

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.designsystem.bookNestCompactTypography
import com.nps.booknest.feature.library.presentation.overview.LibraryBookUi
import com.nps.booknest.feature.library.presentation.overview.LibraryCategory
import com.nps.booknest.feature.library.presentation.overview.libraryOverviewBooks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MyBooksBookCardTest {
    @Test
    fun normalPhonesKeepActionsAtTopAndProgressAcrossTheBody() {
        listOf(360, 412).forEach { width ->
            runComposeUiTest {
                val robot = MyBooksCardRobot(this).setContent(width = width)
                val cover =
                    onNodeWithTag(
                        "my_books_cover_atomic",
                        useUnmergedTree = true,
                    ).fetchSemanticsNode().boundsInRoot
                val body =
                    onNodeWithTag(
                        "my_books_body_atomic",
                        useUnmergedTree = true,
                    ).fetchSemanticsNode().boundsInRoot
                val meta =
                    onNodeWithTag(
                        "my_books_metadata_atomic",
                        useUnmergedTree = true,
                    ).fetchSemanticsNode().boundsInRoot
                val action = onNodeWithTag("library_browse_book_action_atomic").fetchSemanticsNode().boundsInRoot
                val overflow = onNodeWithTag("library_browse_book_more_atomic").fetchSemanticsNode().boundsInRoot
                val progress =
                    onNodeWithTag(
                        "my_books_progress_row_atomic",
                        useUnmergedTree = true,
                    ).fetchSemanticsNode().boundsInRoot
                val percentage =
                    onNodeWithTag(
                        "my_books_percentage_atomic",
                        useUnmergedTree = true,
                    ).fetchSemanticsNode().boundsInRoot
                val chapter =
                    onNodeWithTag(
                        "my_books_chapter_atomic",
                        useUnmergedTree = true,
                    ).fetchSemanticsNode().boundsInRoot
                assertEquals(64f * robot.density, cover.width, 1f)
                assertEquals(cover.top, meta.top, 1f)
                assertEquals(meta.top, action.top, 1f)
                assertEquals(meta.top, overflow.top, 1f)
                assertTrue(meta.right <= action.left)
                assertTrue(progress.top >= maxOf(meta.bottom, action.bottom))
                assertEquals(body.width, progress.width, 1f)
                assertEquals(body.right, percentage.right, 1f)
                assertTrue(chapter.top >= progress.bottom)
                val visual =
                    onNodeWithTag(
                        "my_books_action_visual_atomic",
                        useUnmergedTree = true,
                    ).fetchSemanticsNode().boundsInRoot
                assertEquals(32f * robot.density, visual.height, 1f)
                assertTrue(action.height >= 48f * robot.density - 1f)
                assertTrue(overflow.width >= 48f * robot.density - 1f)
            }
        }
    }

    @Test
    fun narrowAndLargeTextCardsMoveOnlyTheActionBelowMetadata() {
        listOf(320, 360, 412, 840, 1024).forEach { width ->
            listOf(1f, 1.3f, 1.5f).forEach { scale ->
                runComposeUiTest {
                    val book =
                        libraryOverviewBooks().first().copy(
                            title =
                                "A very long book title with မြန်မာစာ " +
                                    "for checking adaptive card text and alignment",
                            author = "An author with a very long name and another coauthor",
                            category = LibraryCategory.SCIENCE_FICTION,
                        )
                    MyBooksCardRobot(this).setContent(book, width, scale, isDarkTheme = scale == 1.5f)
                    val card = onNodeWithTag("library_browse_book_atomic").fetchSemanticsNode().boundsInRoot
                    val meta =
                        onNodeWithTag(
                            "my_books_metadata_atomic",
                            useUnmergedTree = true,
                        ).fetchSemanticsNode().boundsInRoot
                    val action = onNodeWithTag("library_browse_book_action_atomic").fetchSemanticsNode().boundsInRoot
                    val overflow = onNodeWithTag("library_browse_book_more_atomic").fetchSemanticsNode().boundsInRoot
                    val category =
                        onNodeWithTag(
                            "my_books_category_atomic",
                            useUnmergedTree = true,
                        ).fetchSemanticsNode().boundsInRoot
                    val body =
                        onNodeWithTag(
                            "my_books_body_atomic",
                            useUnmergedTree = true,
                        ).fetchSemanticsNode().boundsInRoot
                    val progress =
                        onNodeWithTag(
                            "my_books_progress_row_atomic",
                            useUnmergedTree = true,
                        ).fetchSemanticsNode().boundsInRoot
                    assertTrue(meta.right <= overflow.left)
                    assertTrue(category.right <= meta.right + 1f)
                    assertEquals(meta.top, overflow.top, 1f)
                    if (width == 320 || scale >= 1.3f) assertTrue(action.top >= meta.bottom)
                    assertTrue(progress.top >= action.bottom)
                    assertTrue(body.right <= card.right)
                    assertTrue(progress.bottom <= card.bottom)
                    onNodeWithTag("library_browse_book_action_atomic").assertIsDisplayed()
                }
            }
        }
    }

    @Test
    fun statusVariantsUsePrimaryOrBorderlessTonalActionsInBothThemes() {
        val books = libraryOverviewBooks()
        val samples =
            listOf(
                books.first(),
                books.first { it.id == "rich-dad" }.copy(isReading = false, isSaved = true),
                books.first { it.id == "dune" },
                books.first { it.id == "midnight" },
            )
        listOf(false, true).forEach { dark ->
            samples.forEach { book ->
                runComposeUiTest {
                    val robot = MyBooksCardRobot(this).setContent(book, isDarkTheme = dark)
                    val pixels =
                        onNodeWithTag(
                            "my_books_action_visual_${book.id}",
                            useUnmergedTree = true,
                        ).captureToImage().toPixelMap()
                    val expected = if (book.isReading) robot.primary else robot.tonal
                    assertEquals(expected, pixels[1, pixels.height / 2])
                    val pill =
                        onNodeWithTag(
                            "my_books_category_${book.id}",
                            useUnmergedTree = true,
                        ).captureToImage().toPixelMap()
                    assertEquals(robot.tonal, pill[2, pill.height / 2])
                    if (book.isReading) {
                        onNodeWithText("Continue").assertIsDisplayed()
                        onNodeWithTag("my_books_status_${book.id}", useUnmergedTree = true).assertDoesNotExist()
                    } else {
                        onNodeWithText(if (book.isSaved) "Read" else "View").assertIsDisplayed()
                        onNodeWithTag("my_books_status_${book.id}", useUnmergedTree = true).assertIsDisplayed()
                        onNodeWithTag("my_books_progress_row_${book.id}", useUnmergedTree = true).assertDoesNotExist()
                    }
                    if (!dark) {
                        val context = InstrumentationRegistry.getInstrumentation().context
                        File(context.externalCacheDir, "mybooks-card-${book.id}.png").outputStream().use {
                            onNodeWithTag(
                                "library_browse_book_${book.id}",
                            ).captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun cardActionAndOverflowEachDispatchExactlyOneDeferredAction() =
        runComposeUiTest {
            var clicks = 0
            MyBooksCardRobot(this).setContent(onClick = { clicks += 1 })
            onNodeWithTag("library_browse_book_action_atomic").performClick()
            onNodeWithTag("library_browse_book_more_atomic").performClick()
            onNodeWithTag("my_books_cover_atomic", useUnmergedTree = true).performClick()
            runOnIdle { assertEquals(3, clicks) }
        }

    @Test
    fun readingNowRetainsItsExistingCoverAndCompactButtonStyle() =
        runComposeUiTest {
            val robot = MyBooksCardRobot(this).setContent(mode = LibraryBooksMode.READING)
            onNodeWithTag("my_books_category_atomic", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("my_books_action_visual_atomic", useUnmergedTree = true).assertDoesNotExist()
            val pixels =
                onNodeWithTag(
                    "booknest_compact_button_visual",
                    useUnmergedTree = true,
                ).captureToImage().toPixelMap()
            assertEquals(robot.tonal, pixels[pixels.width / 2, pixels.height - 4])
            onNodeWithText("Page 120 of 320").assertIsDisplayed()
        }
}

private class MyBooksCardRobot(
    private val test: ComposeUiTest,
) {
    var density = 1f
    var primary = Color.Unspecified
    var tonal = Color.Unspecified

    fun setContent(
        book: LibraryBookUi = libraryOverviewBooks().first(),
        width: Int = 412,
        scale: Float = 1f,
        isDarkTheme: Boolean = false,
        mode: LibraryBooksMode = LibraryBooksMode.MY_BOOKS,
        onClick: () -> Unit = {},
    ) = apply {
        test.setContent {
            val simulated =
                LocalWindowInfo.current.containerSize.width
                    .toFloat() / width
            CompositionLocalProvider(LocalDensity provides Density(simulated, scale)) {
                BookNestTheme(isDarkTheme = isDarkTheme) {
                    MaterialTheme(typography = bookNestCompactTypography) {
                        val colors = MaterialTheme.colorScheme
                        SideEffect {
                            density = simulated
                            primary = colors.primary
                            tonal = colors.secondaryContainer
                        }
                        Column(Modifier.fillMaxSize().padding(16.dp)) { LibraryBrowseBookCard(book, mode, onClick) }
                    }
                }
            }
        }
    }
}
