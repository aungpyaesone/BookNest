package com.nps.booknest.feature.explore.data

import app.cash.turbine.test
import com.nps.booknest.core.database.explore.CatalogSnapshotEntity
import com.nps.booknest.core.database.explore.ExploreTaxonomyEntity
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.network.apiJson
import com.nps.booknest.feature.explore.domain.CatalogPage
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.CatalogSort
import com.nps.booknest.feature.explore.domain.ExploreCategory
import com.nps.booknest.feature.explore.domain.ExploreRemoteDataSource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class OfflineFirstExploreRepositoryTest {
    private val query = CatalogQuery(1)

    @Test
    fun `refresh persists restart offline survives and empty replaces old pages`() =
        runTest {
            val dao = FakeExploreCacheDao()
            val remote = FakeExploreRemoteDataSource()
            val repository = OfflineFirstExploreRepository(remote, RoomExploreDataSource(dao))
            assertIs<Result.Success<*>>(repository.refreshCatalog(query))
            val restarted = OfflineFirstExploreRepository(remote, RoomExploreDataSource(dao))
            remote.shouldFail = true
            assertIs<Result.Error<*>>(restarted.refreshCatalog(query))
            restarted.observeCatalog(query).test {
                assertEquals(
                    2,
                    assertIs<Result.Success<*>>(awaitItem()).let {
                        (it.data as com.nps.booknest.feature.explore.domain.CatalogSnapshot).books.size
                    },
                )
            }
            remote.shouldFail = false
            remote.page = remote.page.copy(total = 0, items = emptyList())
            assertIs<Result.Success<*>>(restarted.refreshCatalog(query))
            val snapshot = assertIs<Result.Success<*>>(RoomExploreDataSource(dao).getCatalog(query)).data
            assertTrue((snapshot as com.nps.booknest.feature.explore.domain.CatalogSnapshot).books.isEmpty())
            assertFalse(snapshot.hasMore)
        }

    @Test
    fun `append deduplicates IDs stores pages and refresh atomically resets`() =
        runTest {
            val dao = FakeExploreCacheDao()
            val remote = FakeExploreRemoteDataSource()
            val local = RoomExploreDataSource(dao)
            val repository = OfflineFirstExploreRepository(remote, local)
            repository.refreshCatalog(query)
            repository.loadNextPage(query)
            val snapshot =
                assertIs<Result.Success<*>>(local.getCatalog(query)).data
                    as com.nps.booknest.feature.explore.domain.CatalogSnapshot
            assertEquals(2, snapshot.pages.size)
            assertEquals(2, snapshot.books.size)
            assertEquals(listOf(1, 2), remote.pages)
            repository.refreshCatalog(query)
            val refreshed =
                assertIs<Result.Success<*>>(local.getCatalog(query)).data
                    as com.nps.booknest.feature.explore.domain.CatalogSnapshot
            assertEquals(1, refreshed.pages.size)
        }

    @Test
    fun `write failure preserves snapshot and query caches are isolated`() =
        runTest {
            val dao = FakeExploreCacheDao()
            val remote = FakeExploreRemoteDataSource()
            val local = RoomExploreDataSource(dao)
            val repository = OfflineFirstExploreRepository(remote, local)
            repository.refreshCatalog(query)
            val before = dao.getCatalog(query.cacheKey())
            dao.shouldFailWrites = true
            remote.page = remote.page.copy(total = 0, items = emptyList())
            assertEquals(Result.Error(DataError.Local.UNKNOWN), repository.refreshCatalog(query))
            assertEquals(before, dao.getCatalog(query.cacheKey()))
            assertEquals(Result.Success(null), local.getCatalog(query.copy(sort = CatalogSort.PRICE)))
        }

    @Test
    fun `corrupt cache keeps observation alive for network repair`() =
        runTest {
            val dao = FakeExploreCacheDao()
            dao.upsertCategories(ExploreTaxonomyEntity(payload = "invalid"))
            dao.upsertCatalog(CatalogSnapshotEntity(query.cacheKey(), "invalid"))
            val repository = OfflineFirstExploreRepository(FakeExploreRemoteDataSource(), RoomExploreDataSource(dao))
            repository.observeCatalog(query).test {
                assertIs<Result.Error<*>>(awaitItem())
                repository.refreshCatalog(query)
                assertIs<Result.Success<*>>(awaitItem())
            }
            repository.observeCategories().test {
                assertIs<Result.Error<*>>(awaitItem())
                repository.refreshCategories()
                assertIs<Result.Success<*>>(awaitItem())
            }
        }

    @Test
    fun `concurrent duplicates share one network operation and append failure retains pages`() =
        runTest {
            val remote = FakeExploreRemoteDataSource()
            val repository = OfflineFirstExploreRepository(remote, RoomExploreDataSource(FakeExploreCacheDao()))
            val release = CompletableDeferred<Unit>()
            remote.awaitRequest = { release.await() }
            val first = async { repository.refreshCatalog(query) }
            runCurrent()
            val duplicate = async { repository.refreshCatalog(query) }
            runCurrent()
            release.complete(Unit)
            assertEquals(first.await(), duplicate.await())
            assertEquals(listOf(1), remote.pages)
            remote.shouldFail = true
            assertIs<Result.Error<*>>(repository.loadNextPage(query))
            repository.observeCatalog(query).test {
                val snapshot =
                    assertIs<Result.Success<*>>(awaitItem()).data
                        as com.nps.booknest.feature.explore.domain.CatalogSnapshot
                assertEquals(1, snapshot.pages.size)
            }
        }

    @Test
    fun `canceled owner does not report stale success to waiting refresh`() =
        runTest {
            val remote = FakeExploreRemoteDataSource()
            remote.awaitRequest = { if (remote.pages.size == 1) awaitCancellation() }
            val repository = OfflineFirstExploreRepository(remote, RoomExploreDataSource(FakeExploreCacheDao()))
            val owner = async { repository.refreshCatalog(query) }
            runCurrent()
            val waiting = async { repository.refreshCatalog(query) }
            runCurrent()
            owner.cancelAndJoin()
            assertIs<Result.Success<*>>(waiting.await())
            assertEquals(listOf(1, 1), remote.pages)
        }

    @Test
    fun `refresh queued behind append still requests page one`() =
        runTest {
            val remote = FakeExploreRemoteDataSource()
            val repository = OfflineFirstExploreRepository(remote, RoomExploreDataSource(FakeExploreCacheDao()))
            repository.refreshCatalog(query)
            val release = CompletableDeferred<Unit>()
            remote.awaitRequest = { release.await() }
            val append = async { repository.loadNextPage(query) }
            runCurrent()
            val refresh = async { repository.refreshCatalog(query) }
            runCurrent()
            release.complete(Unit)
            append.await()
            refresh.await()
            assertEquals(listOf(1, 2, 1), remote.pages)
        }

    @Test
    fun `empty appended page stops loading even if server total is stale`() =
        runTest {
            val remote = FakeExploreRemoteDataSource()
            val local = RoomExploreDataSource(FakeExploreCacheDao())
            val repository = OfflineFirstExploreRepository(remote, local)
            repository.refreshCatalog(query)
            remote.page = remote.page.copy(items = emptyList())
            repository.loadNextPage(query)
            repository.loadNextPage(query)
            assertEquals(listOf(1, 2), remote.pages)
            val snapshot =
                assertIs<Result.Success<*>>(local.getCatalog(query)).data
                    as com.nps.booknest.feature.explore.domain.CatalogSnapshot
            assertEquals(2, snapshot.books.size)
            assertFalse(snapshot.hasMore)
        }
}

private class FakeExploreRemoteDataSource : ExploreRemoteDataSource {
    var shouldFail = false
    var awaitRequest: suspend () -> Unit = {}
    var page =
        apiJson
            .decodeFromString<CatalogPageDto>(catalogResponseFixture)
            .toCatalogPage(CatalogQuery(1), 1)
            .copy(total = 45)
    val pages = mutableListOf<Int>()

    override suspend fun getCategories(): Result<List<ExploreCategory>, DataError.Network> =
        Result.Success(apiJson.decodeFromString<ExploreResponseDto>(taxonomyResponseFixture).toCategories())

    override suspend fun getCatalog(
        query: CatalogQuery,
        page: Int,
    ): Result<CatalogPage, DataError.Network> {
        pages += page
        awaitRequest()
        return if (shouldFail) {
            Result.Error(
                DataError.Network.NO_INTERNET,
            )
        } else {
            Result.Success(this.page.copy(page = page))
        }
    }
}
