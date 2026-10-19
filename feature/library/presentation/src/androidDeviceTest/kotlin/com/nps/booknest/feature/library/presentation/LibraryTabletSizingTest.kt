@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.library.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.feature.library.presentation.browse.AllBooksRoot
import com.nps.booknest.feature.library.presentation.browse.AllBooksViewModel
import com.nps.booknest.feature.library.presentation.browse.CollectionsRoot
import com.nps.booknest.feature.library.presentation.browse.CollectionsViewModel
import com.nps.booknest.feature.library.presentation.browse.MyBooksRoot
import com.nps.booknest.feature.library.presentation.browse.MyBooksViewModel
import com.nps.booknest.feature.library.presentation.browse.ReadingNowRoot
import com.nps.booknest.feature.library.presentation.browse.ReadingNowViewModel
import com.nps.booknest.feature.library.presentation.overview.LibraryOverviewRoot
import com.nps.booknest.feature.library.presentation.overview.LibraryOverviewViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LibraryTabletSizingTest {
    @Test
    fun screensOptIntoTabletTextAndKeepCompactSplitScreenText() {
        val cases = listOf(360 to 1f, 540 to 1.5f, 600 to 1f, 820 to 1.3f, 840 to 1.5f, 1024 to 1f, 1180 to 1.5f)
        cases.forEach { (width, scale) ->
            TabletLibraryPage.entries.forEach { page ->
                runComposeUiTest {
                    TabletLibraryRobot(this).setContent(page, width, scale)
                    val layouts = mutableListOf<TextLayoutResult>()
                    onAllNodesWithText(page.title, useUnmergedTree = true)
                        .onFirst()
                        .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
                    assertEquals(
                        if (width >=
                            600
                        ) {
                            24f
                        } else {
                            20f
                        },
                        layouts
                            .single()
                            .layoutInput.style.fontSize.value,
                        0.01f,
                    )
                    assertEquals(
                        scale,
                        layouts
                            .single()
                            .layoutInput.density.fontScale,
                        0.01f,
                    )
                }
            }
        }
    }

    @Test
    fun dashboardCoversAndSummaryGrowOnlyOnTablets() {
        listOf(360, 600, 820, 1024, 1180).forEach { width ->
            runComposeUiTest {
                TabletLibraryRobot(this).setContent(TabletLibraryPage.DASHBOARD, width, 1f)
                val cover = onNodeWithContentDescription("Cover of Atomic Habits", true).getUnclippedBoundsInRoot()
                assertEquals(if (width >= 600) 88f else 72f, cover.width.value, 0.5f)
                onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag("library_summary_section"))
                val summary = onNodeWithTag("library_summary_ALL").getUnclippedBoundsInRoot()
                assertTrue(summary.height.value >= if (width >= 600) 111f else 95f)
            }
        }
    }

    @Test
    fun tabletBookRowsUseLargerCovers() {
        listOf(TabletLibraryPage.READING, TabletLibraryPage.MY_BOOKS).forEach { page ->
            runComposeUiTest {
                TabletLibraryRobot(this).setContent(page, 820, 1f)
                val title = if (page == TabletLibraryPage.READING) "Atomic Habits" else "Ikigai"
                val cover = onNodeWithContentDescription("Cover of $title", true).getUnclippedBoundsInRoot()
                assertEquals(if (page == TabletLibraryPage.READING) 72f else 80f, cover.width.value, 0.5f)
            }
        }
    }
}

private enum class TabletLibraryPage(
    val title: String,
) {
    DASHBOARD("My Library"),
    READING("Reading Now"),
    MY_BOOKS("My Books"),
    ALL_BOOKS("All Books"),
    COLLECTIONS("Collections"),
}

private class TabletLibraryRobot(
    private val test: ComposeUiTest,
) {
    fun setContent(
        page: TabletLibraryPage,
        width: Int,
        scale: Float,
    ) {
        val dashboard = LibraryOverviewViewModel()
        val reading = ReadingNowViewModel()
        val books = MyBooksViewModel()
        val all = AllBooksViewModel()
        val collections = CollectionsViewModel()
        test.setContent {
            val density =
                LocalWindowInfo.current.containerSize.width
                    .toFloat() / width
            CompositionLocalProvider(LocalDensity provides Density(density, scale)) {
                BookNestTheme(isDarkTheme = scale == 1.5f) {
                    when (page) {
                        TabletLibraryPage.DASHBOARD -> LibraryOverviewRoot(viewModel = dashboard)
                        TabletLibraryPage.READING -> ReadingNowRoot({}, viewModel = reading)
                        TabletLibraryPage.MY_BOOKS -> MyBooksRoot({}, viewModel = books)
                        TabletLibraryPage.ALL_BOOKS -> AllBooksRoot({}, viewModel = all)
                        TabletLibraryPage.COLLECTIONS -> CollectionsRoot({}, viewModel = collections)
                    }
                }
            }
        }
    }
}
