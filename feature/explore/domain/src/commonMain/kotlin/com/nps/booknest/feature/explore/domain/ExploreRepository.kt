package com.nps.booknest.feature.explore.domain

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface ExploreRepository {
    fun observeCategories(): Flow<Result<List<ExploreCategory>?, DataError>>

    fun observeCatalog(query: CatalogQuery): Flow<Result<CatalogSnapshot?, DataError>>

    suspend fun refreshCategories(): EmptyResult<DataError>

    suspend fun refreshCatalog(query: CatalogQuery): EmptyResult<DataError>

    suspend fun loadNextPage(query: CatalogQuery): EmptyResult<DataError>
}

interface ExploreRemoteDataSource {
    suspend fun getCategories(): Result<List<ExploreCategory>, DataError.Network>

    suspend fun getCatalog(
        query: CatalogQuery,
        page: Int,
    ): Result<CatalogPage, DataError.Network>
}

interface ExploreLocalDataSource {
    fun observeCategories(): Flow<Result<List<ExploreCategory>?, DataError>>

    fun observeCatalog(query: CatalogQuery): Flow<Result<CatalogSnapshot?, DataError>>

    suspend fun getCatalog(query: CatalogQuery): Result<CatalogSnapshot?, DataError>

    suspend fun saveCategories(categories: List<ExploreCategory>): EmptyResult<DataError.Local>

    suspend fun saveCatalog(snapshot: CatalogSnapshot): EmptyResult<DataError.Local>
}
