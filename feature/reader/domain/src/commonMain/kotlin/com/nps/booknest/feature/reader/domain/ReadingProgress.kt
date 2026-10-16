package com.nps.booknest.feature.reader.domain

data class ReadingProgress(
    val bookId: String,
    val chapterId: String,
    val paragraphId: String,
    val overallProgress: Float,
)
