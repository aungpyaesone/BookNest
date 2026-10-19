package com.nps.booknest.feature.explore.data

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.network.getApi
import com.nps.booknest.feature.explore.domain.CatalogPage
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.CatalogSort
import com.nps.booknest.feature.explore.domain.ExploreCategory
import com.nps.booknest.feature.explore.domain.ExploreRemoteDataSource
import io.ktor.client.HttpClient

class KtorExploreDataSource(
    private val client: HttpClient,
) : ExploreRemoteDataSource {
    override suspend fun getCategories(): Result<List<ExploreCategory>, DataError.Network> =
        client.getApi<ExploreResponseDto, List<ExploreCategory>>("/api/v1/screens/explore") { it.toCategories() }

    override suspend fun getCatalog(
        query: CatalogQuery,
        page: Int,
    ): Result<CatalogPage, DataError.Network> =
        client.getApi<CatalogPageDto, CatalogPage>(
            "/api/v1/screens/catalog",
            mapOf(
                "categoryId" to query.categoryId.toString(),
                "sort" to query.sort.toApiSort(),
                "page" to page.toString(),
                "pageSize" to query.pageSize.toString(),
            ),
        ) { it.toCatalogPage(query, page) }
}

private fun CatalogSort.toApiSort(): String =
    when (this) {
        CatalogSort.POPULAR -> "popular"
        CatalogSort.RECENT -> "recent"
        CatalogSort.TITLE -> "title"
        CatalogSort.PRICE -> "price"
    }
