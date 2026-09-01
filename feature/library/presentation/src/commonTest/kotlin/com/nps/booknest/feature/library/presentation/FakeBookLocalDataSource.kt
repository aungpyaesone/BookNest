package com.nps.booknest.feature.library.presentation

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.library.domain.Book
import com.nps.booknest.feature.library.domain.BookLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeBookLocalDataSource : BookLocalDataSource {
    val books = MutableSharedFlow<Result<List<Book>, DataError.Local>>(replay = 1)
    val selectedBook = MutableSharedFlow<Result<Book?, DataError.Local>>(replay = 1)

    override fun observeBooks(): Flow<Result<List<Book>, DataError.Local>> = books

    override fun observeBook(bookId: String): Flow<Result<Book?, DataError.Local>> = selectedBook
}

fun bookFixture(
    id: String = "book-1",
    title: String = "Dune",
    author: String = "Frank Herbert",
): Book = Book(id = id, title = title, author = author)
