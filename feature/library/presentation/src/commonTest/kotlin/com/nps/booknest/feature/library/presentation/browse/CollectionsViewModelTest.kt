package com.nps.booknest.feature.library.presentation.browse

import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CollectionsViewModelTest {
    @Test
    fun `collection types search and count use six reference samples`() =
        libraryBrowseTest {
            val viewModel = CollectionsViewModel()
            assertEquals(
                listOf(12, 8, 6, 10, 5, 18),
                viewModel.state.value.collections
                    .map { it.bookCount },
            )
            viewModel.onAction(CollectionsAction.OnTypeSelect(CollectionType.SHARED))
            assertEquals(
                listOf("japanese", "travel"),
                viewModel.state.value.collections
                    .map { it.id },
            )
            viewModel.onAction(CollectionsAction.OnSearchChange(" JAPANESE "))
            assertEquals(
                listOf("japanese"),
                viewModel.state.value.collections
                    .map { it.id },
            )
            viewModel.onAction(CollectionsAction.OnTypeSelect(CollectionType.MY))
            assertTrue(
                viewModel.state.value.collections
                    .isEmpty(),
            )
            viewModel.onAction(CollectionsAction.OnSearchChange(""))
            assertEquals(4, viewModel.state.value.collections.size)
            viewModel.onAction(CollectionsAction.OnTypeSelect(CollectionType.FAVORITES))
            assertEquals(
                listOf("personal", "fiction"),
                viewModel.state.value.collections
                    .map { it.id },
            )
        }

    @Test
    fun `collection draft apply dismiss reset and immediate sort stay independent`() =
        libraryBrowseTest {
            val viewModel = CollectionsViewModel()
            viewModel.onAction(CollectionsAction.OnFilterClick)
            viewModel.onAction(CollectionsAction.OnDraftTypeSelect(CollectionType.SHARED))
            assertEquals(6, viewModel.state.value.collections.size)
            viewModel.onAction(CollectionsAction.OnFilterDismiss)
            assertEquals(CollectionType.ALL, viewModel.state.value.draftType)
            viewModel.onAction(CollectionsAction.OnFilterClick)
            viewModel.onAction(CollectionsAction.OnDraftTypeSelect(CollectionType.SHARED))
            viewModel.onAction(CollectionsAction.OnFilterApply)
            assertEquals(2, viewModel.state.value.collections.size)
            assertFalse(viewModel.state.value.isFilterVisible)
            viewModel.onAction(CollectionsAction.OnFilterClick)
            viewModel.onAction(CollectionsAction.OnFilterReset)
            assertEquals(2, viewModel.state.value.collections.size)
            viewModel.onAction(CollectionsAction.OnFilterApply)
            viewModel.onAction(CollectionsAction.OnSortSelect(LibraryBrowseSort.TITLE))
            assertEquals(
                "business",
                viewModel.state.value.collections
                    .first()
                    .id,
            )
            viewModel.onAction(CollectionsAction.OnSortSelect(LibraryBrowseSort.PROGRESS))
            assertEquals(LibraryBrowseSort.TITLE, viewModel.state.value.sort)
        }

    @Test
    fun `create card and overflow are deferred without changing collections`() =
        libraryBrowseTest {
            val viewModel = CollectionsViewModel()
            val before = viewModel.state.value
            viewModel.events.test {
                viewModel.onAction(CollectionsAction.OnComingSoonClick)
                assertEquals(LibraryBrowseEvent.ShowComingSoon, awaitItem())
                viewModel.onAction(CollectionsAction.OnBackClick)
                assertEquals(LibraryBrowseEvent.NavigateBack, awaitItem())
            }
            assertEquals(before, viewModel.state.value)
        }
}
