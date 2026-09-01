package com.nps.booknest.feature.library.data

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nps.booknest.core.database.book.BookDao
import com.nps.booknest.core.database.book.BookEntity
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.library.domain.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RoomBookDataSourceTest {
    @Test
    fun `observeBooks maps database entities to domain books`() =
        runTest {
            val bookDao =
                FakeBookDao(
                    initialBooks = listOf(BookEntity(bookId = "1", title = "Dune", author = "Frank Herbert")),
                )
            val dataSource = RoomBookDataSource(bookDao)

            dataSource.observeBooks().test {
                assertThat(awaitItem()).isEqualTo(
                    Result.Success(
                        listOf(Book(id = "1", title = "Dune", author = "Frank Herbert")),
                    ),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `observeBooks emits typed local error when database fails`() =
        runTest {
            val dataSource = RoomBookDataSource(FailingBookDao)

            dataSource.observeBooks().test {
                assertThat(awaitItem()).isEqualTo(Result.Error(DataError.Local.UNKNOWN))
                awaitComplete()
            }
        }
}

private class FakeBookDao(
    initialBooks: List<BookEntity>,
) : BookDao {
    private val books = MutableStateFlow(initialBooks)

    override fun observeBooks(): Flow<List<BookEntity>> = books

    override fun observeBook(bookId: String): Flow<BookEntity?> =
        books.map { currentBooks ->
            currentBooks.firstOrNull { book -> book.bookId == bookId }
        }

    override suspend fun upsertBook(book: BookEntity) {
        books.value = books.value.filterNot { existingBook -> existingBook.bookId == book.bookId } + book
    }
}

private object FailingBookDao : BookDao {
    override fun observeBooks(): Flow<List<BookEntity>> = flow { error("database unavailable") }

    override fun observeBook(bookId: String): Flow<BookEntity?> = flow { error("database unavailable") }

    override suspend fun upsertBook(book: BookEntity) = Unit
}
