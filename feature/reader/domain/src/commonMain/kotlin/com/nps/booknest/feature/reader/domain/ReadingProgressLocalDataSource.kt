package com.nps.booknest.feature.reader.domain

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface ReadingProgressLocalDataSource {
    fun observeProgress(bookId: String): Flow<Result<ReadingProgress?, DataError.Local>>

    suspend fun saveProgress(progress: ReadingProgress): EmptyResult<DataError.Local>
}
