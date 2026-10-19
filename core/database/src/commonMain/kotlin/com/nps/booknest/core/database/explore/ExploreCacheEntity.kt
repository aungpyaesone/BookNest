package com.nps.booknest.core.database.explore

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "explore_taxonomy")
data class ExploreTaxonomyEntity(
    @PrimaryKey @ColumnInfo(name = "feed_id") val feedId: Int = 1,
    val payload: String,
)

@Entity(tableName = "catalog_snapshots")
data class CatalogSnapshotEntity(
    @PrimaryKey @ColumnInfo(name = "query_key") val queryKey: String,
    val payload: String,
)
