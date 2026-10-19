package com.nps.booknest.core.database.explore

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ExploreCacheDao {
    @Query("SELECT * FROM explore_taxonomy WHERE feed_id = 1")
    fun observeCategories(): Flow<ExploreTaxonomyEntity?>

    @Query("SELECT * FROM catalog_snapshots WHERE query_key = :key")
    fun observeCatalog(key: String): Flow<CatalogSnapshotEntity?>

    @Query("SELECT * FROM catalog_snapshots WHERE query_key = :key")
    suspend fun getCatalog(key: String): CatalogSnapshotEntity?

    @Upsert
    suspend fun upsertCategories(entity: ExploreTaxonomyEntity)

    @Upsert
    suspend fun upsertCatalog(entity: CatalogSnapshotEntity)
}
