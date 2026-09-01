package com.nps.booknest.core.database.book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    @ColumnInfo(name = "book_id")
    val bookId: String,
    val title: String,
    val author: String,
)
