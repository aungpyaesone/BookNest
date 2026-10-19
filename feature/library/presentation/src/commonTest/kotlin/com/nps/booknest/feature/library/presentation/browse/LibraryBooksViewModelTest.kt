package com.nps.booknest.feature.library.presentation.browse

import app.cash.turbine.test
import com.nps.booknest.feature.library.presentation.overview.LibraryCategory
import com.nps.booknest.feature.library.presentation.overview.LibraryTab
import com.nps.booknest.feature.library.presentation.overview.libraryOverviewBooks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LibraryBooksViewModelTest {
    @Test
    fun `screens share samples and retain original reading progress`() =
        libraryBrowseTest {
            val reading = ReadingNowViewModel().state.value
            val books = MyBooksViewModel().state.value
            assertEquals(listOf("atomic", "psychology", "rich-dad"), reading.books.map { it.id })
            assertEquals(listOf(38, 72, 45), reading.books.map { it.progress })
            assertEquals(6, books.books.size)
            assertEquals(
                mapOf(
                    LibraryTab.ALL to 6,
                    LibraryTab.READING to 3,
                    LibraryTab.SAVED to 3,
                    LibraryTab.FINISHED to 2,
                    LibraryTab.DOWNLOADS to 3,
                ),
                books.statusCounts,
            )
            assertFalse(reading.isSearchVisible)
            assertTrue(books.isSearchVisible)
        }

    @Test
    fun `search category and status combine without mutating totals`() =
        libraryBrowseTest {
            val viewModel = MyBooksViewModel()
            viewModel.onAction(LibraryBooksAction.OnSearchChange("  JAMES  "))
            assertEquals(
                listOf("atomic"),
                viewModel.state.value.books
                    .map { it.id },
            )
            viewModel.onAction(LibraryBooksAction.OnStatusSelect(LibraryTab.FINISHED))
            assertTrue(
                viewModel.state.value.books
                    .isEmpty(),
            )
            assertEquals(6, viewModel.state.value.statusCounts[LibraryTab.ALL])
            viewModel.onAction(LibraryBooksAction.OnSearchChange(""))
            viewModel.onAction(LibraryBooksAction.OnCategorySelect(LibraryCategory.SCIENCE_FICTION))
            assertEquals(
                listOf("dune"),
                viewModel.state.value.books
                    .map { it.id },
            )
            val reading = ReadingNowViewModel()
            reading.onAction(LibraryBooksAction.OnCategorySelect(LibraryCategory.FICTION))
            assertTrue(
                reading.state.value.books
                    .isEmpty(),
            )
            reading.onAction(LibraryBooksAction.OnCategorySelect(null))
            assertEquals(3, reading.state.value.books.size)
        }

    @Test
    fun `filter apply reset and dismiss keep drafts separate`() =
        libraryBrowseTest {
            val viewModel = MyBooksViewModel()
            viewModel.onAction(LibraryBooksAction.OnFilterClick)
            viewModel.onAction(LibraryBooksAction.OnDraftCategorySelect(LibraryCategory.LIFESTYLE))
            viewModel.onAction(LibraryBooksAction.OnDraftStatusSelect(LibraryTab.FINISHED))
            assertEquals(6, viewModel.state.value.books.size)
            viewModel.onAction(LibraryBooksAction.OnFilterDismiss)
            assertEquals(null, viewModel.state.value.draftCategory)
            viewModel.onAction(LibraryBooksAction.OnFilterClick)
            viewModel.onAction(LibraryBooksAction.OnDraftCategorySelect(LibraryCategory.LIFESTYLE))
            viewModel.onAction(LibraryBooksAction.OnDraftStatusSelect(LibraryTab.FINISHED))
            viewModel.onAction(LibraryBooksAction.OnFilterApply)
            assertEquals(
                listOf("ikigai"),
                viewModel.state.value.books
                    .map { it.id },
            )
            assertFalse(viewModel.state.value.isFilterVisible)
            viewModel.onAction(LibraryBooksAction.OnFilterClick)
            viewModel.onAction(LibraryBooksAction.OnFilterReset)
            assertEquals(1, viewModel.state.value.books.size)
            viewModel.onAction(LibraryBooksAction.OnFilterDismiss)
            assertEquals(LibraryCategory.LIFESTYLE, viewModel.state.value.draftCategory)
            viewModel.onAction(LibraryBooksAction.OnFilterClick)
            viewModel.onAction(LibraryBooksAction.OnFilterReset)
            viewModel.onAction(LibraryBooksAction.OnFilterApply)
            assertEquals(6, viewModel.state.value.books.size)
        }

    @Test
    fun `sort is immediate and accepts only screen choices`() =
        libraryBrowseTest {
            val reading = ReadingNowViewModel()
            reading.onAction(LibraryBooksAction.OnSortSelect(LibraryBrowseSort.PROGRESS))
            assertEquals(
                listOf("psychology", "rich-dad", "atomic"),
                reading.state.value.books
                    .map { it.id },
            )
            reading.onAction(LibraryBooksAction.OnSortSelect(LibraryBrowseSort.RECENT_ADDED))
            assertEquals(LibraryBrowseSort.PROGRESS, reading.state.value.sort)
            val books = MyBooksViewModel()
            assertEquals(
                "ikigai",
                books.state.value.books
                    .first()
                    .id,
            )
            books.onAction(LibraryBooksAction.OnSortSelect(LibraryBrowseSort.TITLE))
            assertEquals(
                listOf(
                    "atomic",
                    "dune",
                    "ikigai",
                    "rich-dad",
                    "midnight",
                    "psychology",
                ),
                books.state.value.books.map {
                    it.id
                },
            )
        }

    @Test
    fun `status precedence does not confuse saved reading finished or downloaded`() {
        val books = libraryOverviewBooks().associateBy { it.id }
        assertEquals(LibraryBookStatus.READING, books.getValue("atomic").browseStatus())
        assertEquals(LibraryBookStatus.FINISHED, books.getValue("ikigai").browseStatus())
        assertEquals(LibraryBookStatus.FINISHED, books.getValue("dune").browseStatus())
        assertEquals(LibraryBookStatus.DOWNLOADED, books.getValue("midnight").browseStatus())
        assertEquals(LibraryBookStatus.SAVED, books.getValue("atomic").copy(isReading = false).browseStatus())
    }

    @Test
    fun `search and deferred events do not mutate samples`() =
        libraryBrowseTest {
            val viewModel = ReadingNowViewModel()
            val before = viewModel.state.value.books
            viewModel.onAction(LibraryBooksAction.OnSearchClick)
            assertTrue(viewModel.state.value.isSearchVisible)
            viewModel.events.test {
                viewModel.onAction(LibraryBooksAction.OnComingSoonClick)
                assertEquals(LibraryBrowseEvent.ShowComingSoon, awaitItem())
                viewModel.onAction(LibraryBooksAction.OnBackClick)
                assertEquals(LibraryBrowseEvent.NavigateBack, awaitItem())
            }
            assertEquals(before, viewModel.state.value.books)
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal fun libraryBrowseTest(block: suspend TestScope.() -> Unit) =
    runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }
