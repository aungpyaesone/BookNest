package com.nps.booknest.feature.library.domain

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface BookLocalDataSource {
    fun observeBooks(): Flow<Result<List<Book>, DataError.Local>>

    fun observeBook(bookId: String): Flow<Result<Book?, DataError.Local>>
}
