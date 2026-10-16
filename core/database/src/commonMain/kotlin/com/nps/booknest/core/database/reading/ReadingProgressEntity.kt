package com.nps.booknest.core.database.reading

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "book_id")
    val bookId: String,
    @ColumnInfo(name = "chapter_id")
    val chapterId: String,
    @ColumnInfo(name = "paragraph_id")
    val paragraphId: String,
    @ColumnInfo(name = "overall_progress")
    val overallProgress: Float,
)
