package com.nps.booknest.feature.explore.presentation

import app.cash.turbine.test
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.explore.domain.CatalogBook
import com.nps.booknest.feature.explore.domain.CatalogPage
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.CatalogSnapshot
import com.nps.booknest.feature.explore.domain.CatalogSort
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryBooksViewModelTest {
    @Test
    fun `legacy slug resolves parent children server sort and total`() =
        exploreTest {
            val repository = FakeExploreRepository()
            val viewModel = CategoryBooksViewModel("fiction", repository)
            runCurrent()
            assertEquals(
                1L,
                viewModel.state.value.category
                    ?.id,
            )
            assertEquals(
                listOf(2L),
                viewModel.state.value.subcategories
                    .map { it.id },
            )
            assertEquals(listOf(CatalogQuery(1)), repository.refreshedQueries)
            repository.emitPage(CatalogQuery(1), 124)
            runCurrent()
            assertEquals(124, viewModel.state.value.total)
            viewModel.onAction(CategoryBooksAction.OnSubcategoryClick(2))
            runCurrent()
            viewModel.onAction(CategoryBooksAction.OnSortSelect(CatalogSort.RECENT))
            runCurrent()
            assertEquals(CatalogQuery(2, CatalogSort.RECENT), repository.refreshedQueries.last())
            viewModel.onAction(CategoryBooksAction.OnSubcategoryClick(null))
            runCurrent()
            assertEquals(CatalogQuery(1, CatalogSort.RECENT), repository.refreshedQueries.last())
            assertEquals(4, viewModel.state.value.queryVersion)
        }

    @Test
    fun `unknown restored route becomes unavailable after taxonomy refresh`() =
        exploreTest {
            val repository = FakeExploreRepository()
            val viewModel = CategoryBooksViewModel("old-missing-slug", repository)
            runCurrent()
            assertNotNull(viewModel.state.value.error)
            assertFalse(viewModel.state.value.isLoading)
            assertTrue(repository.refreshedQueries.isEmpty())
        }

    @Test
    fun `append failure requires explicit retry and keeps loaded books`() =
        exploreTest {
            val repository = FakeExploreRepository()
            val query = CatalogQuery(1)
            val book = CatalogBook(10, "Book", emptyList(), emptyList(), null, 12.5, null, null, "USD", null, 0, true)
            repository.catalog(query).value =
                Result.Success(
                    CatalogSnapshot(query, listOf(CatalogPage(1, 20, 45, listOf(book)))),
                )
            repository.appendResult = Result.Error(DataError.Network.NO_INTERNET)
            val viewModel = CategoryBooksViewModel("1", repository)
            runCurrent()
            viewModel.onAction(CategoryBooksAction.OnLoadNextPage)
            runCurrent()
            assertNotNull(viewModel.state.value.appendError)
            assertEquals(1, viewModel.state.value.books.size)
            repeat(3) { viewModel.onAction(CategoryBooksAction.OnLoadNextPage) }
            runCurrent()
            assertEquals(1, repository.appends)
            repository.appendResult = Result.Success(Unit)
            viewModel.onAction(CategoryBooksAction.OnRetryAppend)
            runCurrent()
            assertEquals(2, repository.appends)
            assertEquals(null, viewModel.state.value.appendError)
        }

    @Test
    fun `late canceled response cannot replace active sort state`() =
        exploreTest {
            val repository = FakeExploreRepository()
            val release = CompletableDeferred<Unit>()
            repository.beforeRefresh = { query ->
                if (query.sort == CatalogSort.POPULAR) withContext(NonCancellable) { release.await() }
            }
            val viewModel = CategoryBooksViewModel("1", repository)
            runCurrent()
            viewModel.onAction(CategoryBooksAction.OnSortSelect(CatalogSort.PRICE))
            runCurrent()
            repository.emitPage(CatalogQuery(1, CatalogSort.PRICE), 8)
            runCurrent()
            release.complete(Unit)
            repository.emitPage(CatalogQuery(1), 999)
            runCurrent()
            assertEquals(CatalogSort.PRICE, viewModel.state.value.sort)
            assertEquals(8, viewModel.state.value.total)
            assertFalse(viewModel.state.value.isRefreshing)
        }

    @Test
    fun `back and book events retain coming soon behavior`() =
        exploreTest {
            val viewModel = CategoryBooksViewModel("1", FakeExploreRepository())
            runCurrent()
            viewModel.events.test {
                viewModel.onAction(CategoryBooksAction.OnBookClick)
                viewModel.onAction(CategoryBooksAction.OnBackClick)
                runCurrent()
                assertEquals(CategoryBooksEvent.ShowComingSoon, awaitItem())
                assertEquals(CategoryBooksEvent.NavigateBack, awaitItem())
            }
        }

    @Test
    fun `uncached filter preserves screen chrome while clearing query books and count`() =
        exploreTest {
            val repository = FakeExploreRepository()
            repository.catalog(CatalogQuery(1)).value = Result.Success(loadedCatalog(CatalogQuery(1)))
            val viewModel = CategoryBooksViewModel("1", repository)
            runCurrent()
            assertTrue(viewModel.state.value.hasShownCatalog)
            val original = viewModel.state.value
            val release = CompletableDeferred<Unit>()
            repository.beforeRefresh = { if (it.categoryId == 2L) release.await() }
            viewModel.onAction(CategoryBooksAction.OnSubcategoryClick(2))
            runCurrent()
            val pending = viewModel.state.value
            assertEquals(original.category, pending.category)
            assertEquals(original.subcategories, pending.subcategories)
            assertTrue(pending.hasShownCatalog)
            assertFalse(pending.hasLoaded)
            assertTrue(pending.books.isEmpty())
            assertEquals(0, pending.total)
            assertTrue(pending.isLoading)
            assertEquals(2L, pending.selectedSubcategoryId)
            release.complete(Unit)
        }

    @Test
    fun `cached filter displays matching snapshot while refresh remains pending`() =
        exploreTest {
            val repository = FakeExploreRepository()
            repository.catalog(CatalogQuery(1)).value = Result.Success(loadedCatalog(CatalogQuery(1)))
            val child = CatalogQuery(2)
            repository.catalog(child).value = Result.Success(loadedCatalog(child))
            val viewModel = CategoryBooksViewModel("1", repository)
            runCurrent()
            val release = CompletableDeferred<Unit>()
            repository.beforeRefresh = { if (it == child) release.await() }
            viewModel.onAction(CategoryBooksAction.OnSubcategoryClick(2))
            runCurrent()
            val cached = viewModel.state.value
            assertTrue(cached.hasShownCatalog)
            assertTrue(cached.hasLoaded)
            assertFalse(cached.isLoading)
            assertTrue(cached.isRefreshing)
            assertEquals(listOf(2L), cached.books.map { it.id })
            assertEquals(1, cached.total)
            release.complete(Unit)
        }

    @Test
    fun `failed new query retains chrome and permits retry or another sort`() =
        exploreTest {
            val repository = FakeExploreRepository()
            repository.catalog(CatalogQuery(1)).value = Result.Success(loadedCatalog(CatalogQuery(1)))
            val viewModel = CategoryBooksViewModel("1", repository)
            runCurrent()
            repository.refreshResult = Result.Error(DataError.Network.NO_INTERNET)
            viewModel.onAction(CategoryBooksAction.OnSubcategoryClick(2))
            runCurrent()
            assertTrue(viewModel.state.value.hasShownCatalog)
            assertFalse(viewModel.state.value.hasLoaded)
            assertNotNull(viewModel.state.value.error)
            val release = CompletableDeferred<Unit>()
            repository.beforeRefresh = { release.await() }
            viewModel.onAction(CategoryBooksAction.OnRefreshClick)
            runCurrent()
            assertTrue(viewModel.state.value.isRefreshing)
            viewModel.onAction(CategoryBooksAction.OnSortSelect(CatalogSort.PRICE))
            runCurrent()
            assertTrue(viewModel.state.value.hasShownCatalog)
            assertEquals(CatalogSort.PRICE, viewModel.state.value.sort)
            assertEquals(null, viewModel.state.value.error)
            repository.emitPage(CatalogQuery(2, CatalogSort.PRICE), 0)
            runCurrent()
            assertTrue(viewModel.state.value.hasLoaded)
            assertTrue(
                viewModel.state.value.books
                    .isEmpty(),
            )
            assertEquals(0, viewModel.state.value.total)
            release.complete(Unit)
        }

    @Test
    fun `initial load and unavailable category do not retain screen catalog flag`() =
        exploreTest {
            val repository = FakeExploreRepository()
            val viewModel = CategoryBooksViewModel("1", repository)
            runCurrent()
            assertFalse(viewModel.state.value.hasShownCatalog)
            repository.catalog(CatalogQuery(1)).value = Result.Success(loadedCatalog(CatalogQuery(1)))
            runCurrent()
            assertTrue(viewModel.state.value.hasShownCatalog)
            repository.categories.value = Result.Success(emptyList())
            runCurrent()
            assertFalse(viewModel.state.value.hasShownCatalog)
            assertEquals(null, viewModel.state.value.category)
        }
}

private fun loadedCatalog(query: CatalogQuery) =
    CatalogSnapshot(
        query,
        listOf(
            CatalogPage(
                1,
                20,
                1,
                listOf(
                    CatalogBook(
                        query.categoryId,
                        "Book",
                        emptyList(),
                        emptyList(),
                        null,
                        12.5,
                        null,
                        null,
                        "USD",
                        null,
                        0,
                        true,
                    ),
                ),
            ),
        ),
    )
