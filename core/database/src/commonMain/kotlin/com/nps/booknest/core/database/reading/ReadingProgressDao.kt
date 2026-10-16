package com.nps.booknest.core.database.reading

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE book_id = :bookId LIMIT 1")
    fun observeProgress(bookId: String): Flow<ReadingProgressEntity?>

    @Upsert
    suspend fun upsertProgress(progress: ReadingProgressEntity)
}
