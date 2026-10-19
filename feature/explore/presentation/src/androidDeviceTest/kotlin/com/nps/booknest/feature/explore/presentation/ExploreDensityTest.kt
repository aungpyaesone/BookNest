@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.explore.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.floor
import kotlin.math.roundToInt

class ExploreDensityTest {
    @Test
    fun categoriesUseFourPhoneColumnsAndThreeAtLargeText() {
        listOf(320, 360, 412, 600, 840, 1024, 1600).forEach { width ->
            listOf(1f, 1.1f, 1.3f, 1.5f).forEach { scale ->
                runComposeUiTest {
                    var renderedDensity = 1f
                    setContent {
                        val density =
                            LocalWindowInfo.current.containerSize.width
                                .toFloat() / width
                        SideEffect { renderedDensity = density }
                        CompositionLocalProvider(LocalDensity provides Density(density, scale)) {
                            BookNestTheme(isDarkTheme = scale == 1.5f) {
                                ExploreScreen(
                                    explorePreviewState().copy(
                                        categories =
                                            (1L..16L).map {
                                                ExploreCategoryUi(
                                                    it,
                                                    if (it ==
                                                        4L
                                                    ) {
                                                        "Art & Culture"
                                                    } else {
                                                        "Category $it"
                                                    },
                                                    "unknown",
                                                )
                                            },
                                    ),
                                    {},
                                )
                            }
                        }
                    }
                    val first = onNodeWithTag("explore_category_1").fetchSemanticsNode().boundsInRoot
                    val second = onNodeWithTag("explore_category_2").fetchSemanticsNode().boundsInRoot
                    val grid = onNodeWithTag("explore_category_grid").fetchSemanticsNode().boundsInRoot
                    val density = renderedDensity
                    val contentDp = grid.width / density
                    val columns = expectedColumns(width, contentDp, scale, 96)
                    assertEquals(first.top, second.top, 1f)
                    assertEquals(
                        "Categories width=$width scale=$scale columns=$columns",
                        (grid.width - (columns - 1) * 8f * density) / columns,
                        first.width,
                        2f,
                    )
                    assertTrue(first.height > 0f)
                }
            }
        }
    }

    @Test
    fun booksUseFourPhoneColumnsAndAdaptiveTabletWidths() {
        listOf(320, 360, 412, 600, 840, 1024, 1600).forEach { width ->
            listOf(1f, 1.3f, 1.5f).forEach { scale ->
                runComposeUiTest {
                    var density = 1f
                    setContent {
                        val simulated =
                            LocalWindowInfo.current.containerSize.width
                                .toFloat() / width
                        SideEffect { density = simulated }
                        CompositionLocalProvider(LocalDensity provides Density(simulated, scale)) {
                            BookNestTheme(isDarkTheme = scale == 1.5f) {
                                val books =
                                    (1L..16L).map {
                                        ExploreBookUi(
                                            it,
                                            "A long book title $it",
                                            "First Author, Second Author",
                                            "¥1,200",
                                            null,
                                            it == 1L,
                                        )
                                    }
                                CategoryBooksScreen(
                                    categoryBooksPreviewState().copy(books = books, total = books.size),
                                    {},
                                )
                            }
                        }
                    }
                    val grid = onNodeWithTag("category_books_grid").fetchSemanticsNode().boundsInRoot
                    val first = onNodeWithTag("explore_book_1").fetchSemanticsNode().boundsInRoot
                    val padding = if (width < 600) 16 else 32
                    val contentDp = (grid.width - (padding * density).roundToInt() * 2) / density
                    val columns = expectedColumns(width, contentDp, scale, 112)
                    assertEquals(
                        "Books width=$width scale=$scale columns=$columns",
                        (contentDp - (columns - 1) * 8f) * density / columns,
                        first.width,
                        2f,
                    )
                    assertTrue(first.height > 0f)
                }
            }
        }
    }

    @Test
    fun selectedChipUsesThemePrimaryAndDispatchesSelectionInBothThemes() {
        listOf(false, true).forEach { dark ->
            runComposeUiTest {
                var primary = Color.Unspecified
                var density = 1f
                setContent {
                    var selected by remember { mutableStateOf<Long?>(null) }
                    density = LocalDensity.current.density
                    BookNestTheme(isDarkTheme = dark) {
                        val color = MaterialTheme.colorScheme.primary
                        SideEffect { primary = color }
                        CategoryBooksScreen(
                            categoryBooksPreviewState().copy(
                                selectedSubcategoryId = selected,
                                subcategories = listOf(ExploreCategoryUi(2, "Fantasy", "book-open", 1)),
                            ),
                            {
                                if (it is CategoryBooksAction.OnSubcategoryClick) selected = it.id
                            },
                        )
                    }
                }
                val all = onNodeWithTag("category_chip_all").captureToImage().toPixelMap()
                assertEquals(primary, all[(4f * density).toInt(), all.height / 2])
                onNodeWithTag("category_chip_2").performClick()
                mainClock.advanceTimeBy(500)
                waitForIdle()
                // Android's native ripple uses real frame time rather than the Compose test clock.
                waitUntil(timeoutMillis = 3_000) {
                    val pixels = onNodeWithTag("category_chip_2").captureToImage().toPixelMap()
                    pixels[(4f * density).toInt(), pixels.height / 2] == primary
                }
                val child = onNodeWithTag("category_chip_2").captureToImage().toPixelMap()
                assertEquals(primary, child[(4f * density).toInt(), child.height / 2])
                val deselected = onNodeWithTag("category_chip_all").captureToImage().toPixelMap()
                assertNotEquals(primary, deselected[(4f * density).toInt(), deselected.height / 2])
            }
        }
    }

    private fun expectedColumns(
        width: Int,
        content: Float,
        scale: Float,
        minimum: Int,
    ): Int =
        if (width < 600) {
            if (scale >= 1.3f) 3 else 4
        } else {
            floor((content + 8f) / (minimum * scale.coerceAtLeast(1f) + 8f)).toInt().coerceAtLeast(1)
        }
}
