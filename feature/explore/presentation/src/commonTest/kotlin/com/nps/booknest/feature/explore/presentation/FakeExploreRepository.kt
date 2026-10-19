package com.nps.booknest.feature.explore.presentation

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.explore.domain.CatalogPage
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.CatalogSnapshot
import com.nps.booknest.feature.explore.domain.ExploreCategory
import com.nps.booknest.feature.explore.domain.ExploreRepository
import kotlinx.coroutines.flow.MutableStateFlow

internal val taxonomyFixture =
    listOf(
        ExploreCategory(1, null, "Fiction", "fiction", "book-open", null, 45),
        ExploreCategory(2, 1, "Fantasy", "fantasy", "unknown", null, 12),
        ExploreCategory(3, null, "Education", "education", "graduation-cap", null, 0),
    )

internal class FakeExploreRepository : ExploreRepository {
    val categories = MutableStateFlow<Result<List<ExploreCategory>?, DataError>>(Result.Success(taxonomyFixture))
    val catalogs = mutableMapOf<CatalogQuery, MutableStateFlow<Result<CatalogSnapshot?, DataError>>>()
    val refreshedQueries = mutableListOf<CatalogQuery>()
    var taxonomyRefreshes = 0
    var appends = 0
    var refreshResult: EmptyResult<DataError> = Result.Success(Unit)
    var appendResult: EmptyResult<DataError> = Result.Success(Unit)
    var beforeRefresh: suspend (CatalogQuery) -> Unit = {}

    override fun observeCategories() = categories

    override fun observeCatalog(query: CatalogQuery) = catalog(query)

    fun catalog(query: CatalogQuery) = catalogs.getOrPut(query) { MutableStateFlow(Result.Success(null)) }

    override suspend fun refreshCategories(): EmptyResult<DataError> {
        taxonomyRefreshes++
        return refreshResult
    }

    override suspend fun refreshCatalog(query: CatalogQuery): EmptyResult<DataError> {
        refreshedQueries += query
        beforeRefresh(query)
        return refreshResult
    }

    override suspend fun loadNextPage(query: CatalogQuery): EmptyResult<DataError> {
        appends++
        return appendResult
    }

    fun emitPage(
        query: CatalogQuery,
        total: Int = 45,
    ) {
        catalog(query).value = Result.Success(CatalogSnapshot(query, listOf(CatalogPage(1, 20, total, emptyList()))))
    }
}
