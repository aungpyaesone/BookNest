package com.nps.booknest.feature.library.presentation.browse

import com.nps.booknest.feature.library.presentation.overview.LibraryCategory
import com.nps.booknest.feature.library.presentation.overview.LibraryTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AllBooksViewModelTest {
    @Test
    fun `layout switching retains combined filters sort and full sample total`() =
        runTest {
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            try {
                val viewModel = AllBooksViewModel()
                assertTrue(viewModel.state.value.isGridView)
                assertTrue(viewModel.state.value.isSearchVisible)
                assertEquals(6, viewModel.state.value.totalBooks)
                viewModel.onAction(LibraryBooksAction.OnSearchChange("James"))
                viewModel.onAction(LibraryBooksAction.OnStatusSelect(LibraryTab.READING))
                viewModel.onAction(LibraryBooksAction.OnCategorySelect(LibraryCategory.SELF_HELP))
                viewModel.onAction(LibraryBooksAction.OnSortSelect(LibraryBrowseSort.TITLE))
                viewModel.onAction(LibraryBooksAction.OnViewModeSelect(false))
                val state = viewModel.state.value
                assertFalse(state.isGridView)
                assertEquals(listOf("atomic"), state.books.map { it.id })
                assertEquals(6, state.totalBooks)
                assertEquals("James", state.query)
                assertEquals(LibraryBrowseSort.TITLE, state.sort)
                viewModel.onAction(LibraryBooksAction.OnSearchChange("no match"))
                assertTrue(
                    viewModel.state.value.books
                        .isEmpty(),
                )
                assertEquals(6, viewModel.state.value.totalBooks)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `filter drafts apply reset and dismiss reuse library semantics`() =
        runTest {
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            try {
                val viewModel = AllBooksViewModel()
                viewModel.onAction(LibraryBooksAction.OnFilterClick)
                viewModel.onAction(LibraryBooksAction.OnDraftStatusSelect(LibraryTab.FINISHED))
                viewModel.onAction(LibraryBooksAction.OnFilterDismiss)
                assertEquals(6, viewModel.state.value.books.size)
                viewModel.onAction(LibraryBooksAction.OnFilterClick)
                viewModel.onAction(LibraryBooksAction.OnDraftStatusSelect(LibraryTab.FINISHED))
                viewModel.onAction(LibraryBooksAction.OnFilterApply)
                assertEquals(2, viewModel.state.value.books.size)
                viewModel.onAction(LibraryBooksAction.OnFilterClick)
                viewModel.onAction(LibraryBooksAction.OnFilterReset)
                assertEquals(2, viewModel.state.value.books.size)
                viewModel.onAction(LibraryBooksAction.OnFilterApply)
                assertEquals(6, viewModel.state.value.books.size)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `grid columns follow phone font threshold and available tablet width`() {
        assertEquals(3, allBooksGridColumns(288f, false, 1f))
        assertEquals(2, allBooksGridColumns(380f, false, 1.3f))
        assertEquals(2, allBooksGridColumns(288f, false, 1.5f))
        assertEquals(4, allBooksGridColumns(776f, true, 1f))
        assertEquals(3, allBooksGridColumns(776f, true, 1.5f))
        assertEquals(5, allBooksGridColumns(960f, true, 1f))
    }
}
