package com.nps.booknest.navigation

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.explore.domain.CatalogPage
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.CatalogSnapshot
import com.nps.booknest.feature.explore.domain.ExploreCategory
import com.nps.booknest.feature.explore.domain.ExploreRepository
import kotlinx.coroutines.flow.flowOf

internal class NavigationExploreRepository : ExploreRepository {
    private val names =
        listOf(
            "Fiction",
            "Education",
            "Business",
            "Biography",
            "History",
            "Science",
            "Technology",
            "Art & Culture",
            "Comics",
            "Travel",
            "Religion",
            "Children",
            "Martial",
            "Self-Help",
            "Health",
            "Cooking",
        )

    override fun observeCategories() =
        flowOf(
            Result.Success(
                names.mapIndexed { index, name ->
                    ExploreCategory(index + 1L, null, name, name.lowercase(), "book-open", null, 0)
                },
            ),
        )

    override fun observeCatalog(query: CatalogQuery) =
        flowOf(
            Result.Success(CatalogSnapshot(query, listOf(CatalogPage(1, 20, 0, emptyList())))),
        )

    override suspend fun refreshCategories(): EmptyResult<DataError> = Result.Success(Unit)

    override suspend fun refreshCatalog(query: CatalogQuery): EmptyResult<DataError> = Result.Success(Unit)

    override suspend fun loadNextPage(query: CatalogQuery): EmptyResult<DataError> = Result.Success(Unit)
}
