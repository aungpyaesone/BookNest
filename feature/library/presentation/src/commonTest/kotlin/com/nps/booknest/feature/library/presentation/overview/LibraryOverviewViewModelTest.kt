package com.nps.booknest.feature.library.presentation.overview

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryOverviewViewModelTest {
    @Test
    fun `all dashboard has reference progress and recently added ordering`() =
        libraryOverviewTest {
            val state = LibraryOverviewViewModel().state.value
            assertEquals(listOf(38, 72, 45), state.readingBooks.map { it.progress })
            assertEquals(listOf("ikigai", "psychology"), state.recentlyAdded.map { it.id })
            assertEquals(listOf(32, 6, 8, 12, 4), librarySummaryCounts.values.toList())
        }

    @Test
    fun `search matches title author ignoring case and whitespace`() =
        libraryOverviewTest {
            val viewModel = LibraryOverviewViewModel()
            viewModel.onAction(LibraryOverviewAction.OnSearchChange(" MORGAN "))
            assertEquals(
                listOf("psychology"),
                viewModel.state.value.books
                    .map { it.id },
            )
            assertEquals(1, viewModel.state.value.readingBooks.size)
            viewModel.onAction(LibraryOverviewAction.OnSearchChange("not-found"))
            assertTrue(
                viewModel.state.value.books
                    .isEmpty(),
            )
            assertTrue(
                viewModel.state.value.readingBooks
                    .isEmpty(),
            )
            assertTrue(
                viewModel.state.value.recentlyAdded
                    .isEmpty(),
            )
            assertEquals(32, librarySummaryCounts[LibraryTab.ALL])
        }

    @Test
    fun `status tabs filter book flags and notes without changing the query`() =
        libraryOverviewTest {
            val viewModel = LibraryOverviewViewModel()
            val expected =
                mapOf(
                    LibraryTab.ALL to 6,
                    LibraryTab.READING to 3,
                    LibraryTab.SAVED to 3,
                    LibraryTab.FINISHED to 2,
                    LibraryTab.DOWNLOADS to 3,
                    LibraryTab.NOTES to 2,
                )
            for ((tab, count) in expected) {
                viewModel.onAction(LibraryOverviewAction.OnTabSelect(tab))
                assertEquals(count, viewModel.state.value.books.size)
            }
            viewModel.onAction(LibraryOverviewAction.OnSearchChange("James"))
            viewModel.onAction(LibraryOverviewAction.OnTabSelect(LibraryTab.FINISHED))
            assertEquals("James", viewModel.state.value.query)
            assertTrue(
                viewModel.state.value.books
                    .isEmpty(),
            )
        }

    @Test
    fun `sort applies only on apply while dismissal discards draft`() =
        libraryOverviewTest {
            val viewModel = LibraryOverviewViewModel()
            val initial = viewModel.state.value.books
            viewModel.onAction(LibraryOverviewAction.OnFilterClick)
            viewModel.onAction(LibraryOverviewAction.OnSortSelect(LibrarySort.TITLE))
            assertEquals(initial, viewModel.state.value.books)
            viewModel.onAction(LibraryOverviewAction.OnFilterDismiss)
            assertEquals(LibrarySort.RECENT, viewModel.state.value.draftSort)
            viewModel.onAction(LibraryOverviewAction.OnFilterClick)
            viewModel.onAction(LibraryOverviewAction.OnSortSelect(LibrarySort.TITLE))
            viewModel.onAction(LibraryOverviewAction.OnFilterApply)
            assertEquals(
                listOf("atomic", "dune", "ikigai", "rich-dad", "midnight", "psychology"),
                viewModel.state.value.books
                    .map { it.id },
            )
            assertEquals(
                listOf("ikigai", "psychology"),
                viewModel.state.value.recentlyAdded
                    .map { it.id },
            )
            assertFalse(viewModel.state.value.isFilterVisible)
        }

    @Test
    fun `reset is draft only and can be canceled or applied`() =
        libraryOverviewTest {
            val viewModel = LibraryOverviewViewModel()
            viewModel.onAction(LibraryOverviewAction.OnFilterClick)
            viewModel.onAction(LibraryOverviewAction.OnSortSelect(LibrarySort.TITLE))
            viewModel.onAction(LibraryOverviewAction.OnFilterApply)
            viewModel.onAction(LibraryOverviewAction.OnFilterClick)
            viewModel.onAction(LibraryOverviewAction.OnFilterReset)
            assertEquals(LibrarySort.TITLE, viewModel.state.value.sort)
            assertEquals(LibrarySort.RECENT, viewModel.state.value.draftSort)
            viewModel.onAction(LibraryOverviewAction.OnFilterDismiss)
            assertEquals(LibrarySort.TITLE, viewModel.state.value.draftSort)
            viewModel.onAction(LibraryOverviewAction.OnFilterClick)
            viewModel.onAction(LibraryOverviewAction.OnFilterReset)
            viewModel.onAction(LibraryOverviewAction.OnFilterApply)
            assertEquals(LibrarySort.RECENT, viewModel.state.value.sort)
        }

    @Test
    fun `see all actions emit distinct navigation without selecting a dashboard tab`() =
        libraryOverviewTest {
            val viewModel = LibraryOverviewViewModel()
            viewModel.events.test {
                viewModel.onAction(LibraryOverviewAction.OnReadingSeeAllClick)
                assertEquals(LibraryOverviewEvent.NavigateToReadingNow, awaitItem())
                viewModel.onAction(LibraryOverviewAction.OnCollectionsSeeAllClick)
                assertEquals(LibraryOverviewEvent.NavigateToCollections, awaitItem())
                viewModel.onAction(LibraryOverviewAction.OnMyBooksSeeAllClick)
                assertEquals(LibraryOverviewEvent.NavigateToMyBooks, awaitItem())
            }
            assertEquals(LibraryTab.ALL, viewModel.state.value.selectedTab)
        }

    @Test
    fun `placeholder action emits coming soon without navigation or mutation`() =
        libraryOverviewTest {
            val viewModel = LibraryOverviewViewModel()
            val initial = viewModel.state.value
            viewModel.events.test {
                viewModel.onAction(LibraryOverviewAction.OnComingSoonClick)
                runCurrent()
                assertEquals(LibraryOverviewEvent.ShowComingSoon, awaitItem())
            }
            assertEquals(initial, viewModel.state.value)
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun libraryOverviewTest(block: suspend TestScope.() -> Unit) =
    runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }
