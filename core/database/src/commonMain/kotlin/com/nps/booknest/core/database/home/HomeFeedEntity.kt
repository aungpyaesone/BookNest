package com.nps.booknest.core.database.home

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_feeds")
data class HomeFeedEntity(
    @PrimaryKey
    @ColumnInfo(name = "feed_id")
    val feedId: Int = 1,
    val payload: String,
)
