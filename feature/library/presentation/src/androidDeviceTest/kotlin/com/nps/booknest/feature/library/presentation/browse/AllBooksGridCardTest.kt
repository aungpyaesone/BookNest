@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.designsystem.bookNestCompactTypography
import com.nps.booknest.feature.library.presentation.overview.libraryOverviewBooks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AllBooksGridCardTest {
    @Test
    fun coverAndMenuGlyphKeepAVisibleGapAcrossGridWidthsAndFontScales() {
        listOf(320, 360, 412, 840, 1024).forEach { width ->
            listOf(1f, 1.3f, 1.5f).forEach { scale ->
                listOf(false, true).forEach { isDark ->
                    runComposeUiTest {
                        AllBooksGridCardRobot(this).setContent(width, scale, isDark)
                        val cover = onNodeWithTag("all_books_cover_atomic", true).getUnclippedBoundsInRoot()
                        val icon = onNodeWithTag("all_books_more_icon_atomic", true).getUnclippedBoundsInRoot()
                        val menu = onNodeWithTag("all_books_more_atomic").getUnclippedBoundsInRoot()
                        val card = onNodeWithTag("all_books_card_atomic").getUnclippedBoundsInRoot()
                        assertTrue("$width/$scale gap", icon.left.value - cover.right.value >= 3.5f)
                        assertEquals(48f, menu.width.value, 0.5f)
                        assertEquals(48f, menu.height.value, 0.5f)
                        assertEquals(card.right.value - 6f, menu.right.value, 0.5f)
                        assertEquals(card.top.value + 6f, menu.top.value, 0.5f)
                        assertTrue(icon.right <= menu.right)
                        assertTrue(cover.width.value > 0f)
                        assertEquals(0.68f, cover.width.value / cover.height.value, 0.02f)
                    }
                }
            }
        }
    }

    @Test
    fun menuAndCardDispatchTheirActionOnce() =
        runComposeUiTest {
            var clicks = 0
            AllBooksGridCardRobot(this).setContent(onClick = { clicks++ })
            onNodeWithTag("all_books_more_atomic").performClick()
            runOnIdle { assertEquals(1, clicks) }
            onNodeWithTag("all_books_card_atomic").performClick()
            runOnIdle { assertEquals(2, clicks) }
        }
}

private class AllBooksGridCardRobot(
    private val test: ComposeUiTest,
) {
    fun setContent(
        width: Int = 412,
        scale: Float = 1f,
        isDark: Boolean = false,
        onClick: () -> Unit = {},
    ) {
        val book =
            libraryOverviewBooks().first().copy(
                title = "A longer book title for narrow phone cards",
                author = "First Author, Second Author",
            )
        val contentWidth = width - if (width >= 600) 64f else 32f
        val columns = allBooksGridColumns(contentWidth, width >= 600, scale)
        val cardWidth = (contentWidth - (columns - 1) * 8f) / columns
        test.setContent {
            val density =
                LocalWindowInfo.current.containerSize.width
                    .toFloat() / width
            CompositionLocalProvider(LocalDensity provides Density(density, scale)) {
                BookNestTheme(isDarkTheme = isDark) {
                    MaterialTheme(typography = bookNestCompactTypography) {
                        Box(Modifier.width(cardWidth.dp)) { AllBooksGridCard(book, onClick) }
                    }
                }
            }
        }
    }
}
