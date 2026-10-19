package com.nps.booknest.feature.explore.data

import com.nps.booknest.core.database.explore.CatalogSnapshotEntity
import com.nps.booknest.core.database.explore.ExploreCacheDao
import com.nps.booknest.core.database.explore.ExploreTaxonomyEntity
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeExploreCacheDao : ExploreCacheDao {
    val taxonomy = MutableStateFlow<ExploreTaxonomyEntity?>(null)
    private val snapshots = mutableMapOf<String, MutableStateFlow<CatalogSnapshotEntity?>>()
    var shouldFailWrites = false

    override fun observeCategories() = taxonomy

    override fun observeCatalog(key: String) = snapshots.getOrPut(key) { MutableStateFlow(null) }

    override suspend fun getCatalog(key: String) = observeCatalog(key).value

    override suspend fun upsertCategories(entity: ExploreTaxonomyEntity) {
        check(!shouldFailWrites)
        taxonomy.value = entity
    }

    override suspend fun upsertCatalog(entity: CatalogSnapshotEntity) {
        check(!shouldFailWrites)
        observeCatalog(entity.queryKey).value = entity
    }
}
