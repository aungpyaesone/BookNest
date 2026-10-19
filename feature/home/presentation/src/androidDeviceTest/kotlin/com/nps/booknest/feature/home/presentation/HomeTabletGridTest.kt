@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.home.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt

class HomeTabletGridTest {
    @Test
    fun portraitUsesFourColumnsAndLandscapeKeepsSix() {
        listOf(820 to 1180, 1024 to 1366, 1180 to 820).forEach { (width, height) ->
            runComposeUiTest {
                val book = homePreviewState().books.first()
                val books = (0..7).map { book.copy(id = "grid-$it", title = "Book $it") }
                val state = homePreviewState().copy(banners = emptyList(), books = books, visibleBooks = books)
                setContent {
                    val original = LocalWindowInfo.current
                    val density = original.containerSize.width.toFloat() / width
                    val window =
                        object : WindowInfo by original {
                            override val containerSize =
                                IntSize(original.containerSize.width, (height * density).roundToInt())
                        }
                    CompositionLocalProvider(LocalDensity provides Density(density), LocalWindowInfo provides window) {
                        BookNestTheme { HomeScreen(state, {}, layoutMode = HomeLayoutMode.EXPANDED) }
                    }
                }
                val columns = if (height >= width) 4 else 6
                onNodeWithTag(HomeTestTags.ADAPTIVE_FEED)
                    .performScrollToNode(hasTestTag(HomeTestTags.popularBookTitle("grid-0")))
                val first = onNodeWithTag(HomeTestTags.popularBookTitle("grid-0")).getUnclippedBoundsInRoot()
                val last =
                    onNodeWithTag(
                        HomeTestTags.popularBookTitle("grid-${columns - 1}"),
                    ).getUnclippedBoundsInRoot()
                assertEquals(first.top.value, last.top.value, 1f)
                val next = onNodeWithTag(HomeTestTags.popularBookTitle("grid-$columns")).getUnclippedBoundsInRoot()
                assertTrue(next.top > first.top)
            }
        }
    }
}
