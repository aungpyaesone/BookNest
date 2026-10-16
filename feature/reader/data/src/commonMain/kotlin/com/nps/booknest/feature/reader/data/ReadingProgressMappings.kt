package com.nps.booknest.feature.reader.data

import com.nps.booknest.core.database.reading.ReadingProgressEntity
import com.nps.booknest.feature.reader.domain.ReadingProgress

fun ReadingProgressEntity.toReadingProgress(): ReadingProgress =
    ReadingProgress(
        bookId = bookId,
        chapterId = chapterId,
        paragraphId = paragraphId,
        overallProgress = overallProgress,
    )

fun ReadingProgress.toEntity(): ReadingProgressEntity =
    ReadingProgressEntity(
        bookId = bookId,
        chapterId = chapterId,
        paragraphId = paragraphId,
        overallProgress = overallProgress,
    )
