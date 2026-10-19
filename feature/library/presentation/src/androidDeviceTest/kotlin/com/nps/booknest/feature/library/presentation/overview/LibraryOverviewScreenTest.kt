@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.library.presentation.overview

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LibraryOverviewScreenTest {
    @Test
    fun continueButtonHasCompactVisualAndFullTouchTarget() =
        runComposeUiTest {
            LibraryOverviewRobot(this).setContent()
            val touch = onNodeWithTag("library_continue_reading").getUnclippedBoundsInRoot()
            val visual =
                onNodeWithTag("booknest_compact_button_visual", useUnmergedTree = true)
                    .getUnclippedBoundsInRoot()
            assertTrue(touch.height.value >= 47f)
            assertEquals(32f, visual.height.value, 1f)
            assertTrue(visual.width < onNodeWithTag("library_featured_reading").getUnclippedBoundsInRoot().width)
        }

    @Test
    fun compactWidthsAndFontScalesKeepSummaryCardsAligned() {
        listOf(320, 360, 412).forEach { width ->
            listOf(1f, 1.1f, 1.5f).forEach { scale ->
                runComposeUiTest {
                    LibraryOverviewRobot(this).setContent(widthDp = width, fontScale = scale)
                    onNodeWithText("My Library").assertIsDisplayed()
                    onNodeWithTag("library_overview_feed")
                        .performScrollToNode(hasTestTag("library_summary_section"))
                    val cards =
                        listOf("ALL", "READING", "SAVED", "FINISHED", "DOWNLOADS").map {
                            onNodeWithTag("library_summary_$it").fetchSemanticsNode().boundsInRoot
                        }
                    assertTrue(cards.all { it.height > 0f })
                    assertTrue(cards.all { kotlin.math.abs(it.height - cards.first().height) <= 1f })
                    assertTrue(cards.all { kotlin.math.abs(it.top - cards.first().top) <= 1f })
                }
            }
        }
    }

    @Test
    fun tabletWidthsKeepCollectionsVisibleInBothThemes() {
        listOf(840, 1024).forEach { width ->
            listOf(false, true).forEach { dark ->
                runComposeUiTest {
                    LibraryOverviewRobot(this).setContent(widthDp = width, isDarkTheme = dark, fontScale = 1.5f)
                    onNodeWithTag("library_overview_feed")
                        .performScrollToNode(hasTestTag("library_collections_section"))
                    onNodeWithText("Personal Development").assertIsDisplayed()
                    onNodeWithText("Business & Finance").assertIsDisplayed()
                }
            }
        }
    }

    @Test
    fun dashboardHasReferenceSectionsAndNoNotificationBell() =
        runComposeUiTest {
            LibraryOverviewRobot(this).setContent()
            onNodeWithText("My Library").assertIsDisplayed()
            onNodeWithContentDescription("Notifications").assertDoesNotExist()
            onNodeWithTag("library_continue_reading").assertIsDisplayed()
            onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag("library_summary_section"))
            onNodeWithTag("library_summary_ALL").assertIsDisplayed()
            onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag("library_collections_section"))
            onNodeWithText("Personal Development").assertIsDisplayed()
            onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag("library_recent_section"))
            onNodeWithTag("library_book_ikigai").assertIsDisplayed()
            val firstDate =
                onNodeWithTag(
                    "library_added_ikigai",
                    useUnmergedTree = true,
                ).fetchSemanticsNode().boundsInRoot
            val secondDate =
                onNodeWithTag(
                    "library_added_psychology",
                    useUnmergedTree = true,
                ).fetchSemanticsNode().boundsInRoot
            assertEquals(firstDate.bottom, secondDate.bottom, 1f)
        }

    @Test
    fun searchKeepsCountsAndCollectionsAndShowsBookEmptyStates() =
        runComposeUiTest {
            LibraryOverviewRobot(this).setContent()
            onNodeWithTag("library_overview_search").performTextInput("no-match")
            onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag("library_summary_section"))
            onNodeWithText("32").assertIsDisplayed()
            onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag("library_collections_section"))
            onNodeWithText("Personal Development").assertIsDisplayed()
            onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag("library_recent_section"))
            onAllNodesWithTag("library_overview_empty").onLast().assertIsDisplayed()
        }

    @Test
    fun tabsAndSortSheetApplyAndResetDriveViewModel() =
        runComposeUiTest {
            val robot = LibraryOverviewRobot(this).setContent()
            onNodeWithTag("library_tab_READING").performClick().assertIsSelected()
            onNodeWithTag("library_overview_filter").performClick()
            onNodeWithTag("library_sort_TITLE").performClick().assertIsSelected()
            onNodeWithTag("library_filter_apply").performClick()
            runOnIdle { assertEquals(LibrarySort.TITLE, robot.viewModel.state.value.sort) }
            onNodeWithTag("library_filter_sheet").assertDoesNotExist()
            onNodeWithTag("library_overview_filter").performClick()
            onNodeWithTag("library_filter_reset").performClick()
            onNodeWithTag("library_sort_RECENT").assertIsSelected()
            onNodeWithTag("library_filter_apply").performClick()
            runOnIdle { assertEquals(LibrarySort.RECENT, robot.viewModel.state.value.sort) }
        }

    @Test
    fun continueReadingShowsComingSoonWithoutOpeningReader() =
        runComposeUiTest {
            LibraryOverviewRobot(this).setContent()
            onNodeWithTag("library_continue_reading").performClick()
            onNodeWithText("Coming soon").assertIsDisplayed()
            onNodeWithTag("library_overview_screen").assertIsDisplayed()
        }

    @Test
    fun tabletDarkLargeTextKeepsCardsScrollableAndActionable() =
        runComposeUiTest {
            val robot = LibraryOverviewRobot(this).setContent(widthDp = 900, isDarkTheme = true, fontScale = 1.5f)
            onNodeWithTag("library_overview_feed").performScrollToNode(hasTestTag("library_summary_section"))
            onNodeWithTag("library_summary_SAVED").performClick()
            onNodeWithTag("library_tab_SAVED").assertIsSelected()
            onNodeWithTag("library_book_ikigai").assertIsDisplayed()
            onNodeWithTag("library_overview_tabs").performScrollToNode(hasTestTag("library_tab_NOTES"))
            onNodeWithTag("library_tab_NOTES").performClick().assertIsSelected()
            runOnIdle { assertEquals(LibraryTab.NOTES, robot.viewModel.state.value.selectedTab) }
        }
}

private class LibraryOverviewRobot(
    private val test: ComposeUiTest,
) {
    val viewModel = LibraryOverviewViewModel()

    fun setContent(
        widthDp: Int = 384,
        isDarkTheme: Boolean = false,
        fontScale: Float = 1f,
    ) = apply {
        test.setContent {
            val density =
                LocalWindowInfo.current.containerSize.width
                    .toFloat() / widthDp
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) {
                BookNestTheme(isDarkTheme = isDarkTheme) { LibraryOverviewRoot(viewModel = viewModel) }
            }
        }
    }
}
