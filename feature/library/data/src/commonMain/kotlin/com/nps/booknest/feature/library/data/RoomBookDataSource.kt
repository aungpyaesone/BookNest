package com.nps.booknest.feature.library.data

import com.nps.booknest.core.database.book.BookDao
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.library.domain.Book
import com.nps.booknest.feature.library.domain.BookLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class RoomBookDataSource(
    private val bookDao: BookDao,
) : BookLocalDataSource {
    override fun observeBooks(): Flow<Result<List<Book>, DataError.Local>> =
        bookDao
            .observeBooks()
            .map { books ->
                val result: Result<List<Book>, DataError.Local> =
                    Result.Success(books.map { book -> book.toBook() })
                result
            }.catch { emit(Result.Error(DataError.Local.UNKNOWN)) }

    override fun observeBook(bookId: String): Flow<Result<Book?, DataError.Local>> =
        bookDao
            .observeBook(bookId)
            .map { book ->
                val result: Result<Book?, DataError.Local> = Result.Success(book?.toBook())
                result
            }.catch { emit(Result.Error(DataError.Local.UNKNOWN)) }
}
