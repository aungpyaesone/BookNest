package com.nps.booknest.core.database.home

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeFeedDao {
    @Query("SELECT * FROM home_feeds WHERE feed_id = 1")
    fun observeHome(): Flow<HomeFeedEntity?>

    @Upsert
    suspend fun upsertHome(feed: HomeFeedEntity)
}
