package com.nps.booknest.feature.reader.domain

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result

interface BookContentDataSource {
    suspend fun getBook(bookId: String): Result<ReadableBook, DataError.Local>
}
