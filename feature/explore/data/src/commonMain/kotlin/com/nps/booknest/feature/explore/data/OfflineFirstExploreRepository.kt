package com.nps.booknest.feature.explore.data

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.CatalogSnapshot
import com.nps.booknest.feature.explore.domain.ExploreLocalDataSource
import com.nps.booknest.feature.explore.domain.ExploreRemoteDataSource
import com.nps.booknest.feature.explore.domain.ExploreRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OfflineFirstExploreRepository(
    private val remote: ExploreRemoteDataSource,
    private val local: ExploreLocalDataSource,
) : ExploreRepository {
    private val taxonomyLock = Mutex()
    private val locksGuard = Mutex()
    private val queryLocks = mutableMapOf<CatalogQuery, QueryGate>()
    private var completedTaxonomyRequests = 0L
    private var lastTaxonomyResult: EmptyResult<DataError> = Result.Success(Unit)

    override fun observeCategories() = local.observeCategories()

    override fun observeCatalog(query: CatalogQuery) = local.observeCatalog(query)

    override suspend fun refreshCategories(): EmptyResult<DataError> {
        val completedBeforeWait = completedTaxonomyRequests
        return taxonomyLock.withLock {
            if (completedTaxonomyRequests > completedBeforeWait) {
                lastTaxonomyResult
            } else {
                when (val result = remote.getCategories()) {
                    is Result.Error -> result
                    is Result.Success -> local.saveCategories(result.data)
                }.also {
                    lastTaxonomyResult = it
                    completedTaxonomyRequests++
                }
            }
        }
    }

    override suspend fun refreshCatalog(query: CatalogQuery): EmptyResult<DataError> =
        withQueryLock(query, isAppend = false) {
            when (val result = remote.getCatalog(query, 1)) {
                is Result.Error -> result
                is Result.Success -> local.saveCatalog(CatalogSnapshot(query, listOf(result.data)))
            }
        }

    override suspend fun loadNextPage(query: CatalogQuery): EmptyResult<DataError> =
        withQueryLock(query, isAppend = true) {
            when (val cached = local.getCatalog(query)) {
                is Result.Error -> {
                    cached
                }

                is Result.Success -> {
                    val snapshot = cached.data
                    if (snapshot == null) {
                        Result.Error(DataError.Local.NOT_FOUND)
                    } else if (!snapshot.hasMore) {
                        Result.Success(Unit)
                    } else {
                        when (val result = remote.getCatalog(query, snapshot.nextPage)) {
                            is Result.Error -> result
                            is Result.Success -> local.saveCatalog(snapshot.copy(pages = snapshot.pages + result.data))
                        }
                    }
                }
            }
        }

    private suspend fun withQueryLock(
        query: CatalogQuery,
        isAppend: Boolean,
        block: suspend () -> EmptyResult<DataError>,
    ): EmptyResult<DataError> {
        val gate = locksGuard.withLock { queryLocks.getOrPut(query) { QueryGate() } }
        val completedBeforeWait = gate.completedRequests
        if (!gate.lock.tryLock()) {
            return gate.lock.withLock {
                if (gate.completedRequests > completedBeforeWait && gate.isLastAppend == isAppend) {
                    gate.lastResult
                } else {
                    block().also {
                        gate.lastResult = it
                        gate.isLastAppend = isAppend
                        gate.completedRequests++
                    }
                }
            }
        }
        return try {
            block().also {
                gate.lastResult = it
                gate.isLastAppend = isAppend
                gate.completedRequests++
            }
        } finally {
            gate.lock.unlock()
        }
    }
}

private class QueryGate {
    val lock = Mutex()
    var completedRequests = 0L
    var isLastAppend = false
    var lastResult: EmptyResult<DataError> = Result.Success(Unit)
}
