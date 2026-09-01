package com.nps.booknest.core.database.book

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE")
    fun observeBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE book_id = :bookId LIMIT 1")
    fun observeBook(bookId: String): Flow<BookEntity?>

    @Upsert
    suspend fun upsertBook(book: BookEntity)
}
