package com.nps.booknest.feature.library.presentation.detail

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.library.presentation.FakeBookLocalDataSource
import com.nps.booknest.feature.library.presentation.bookFixture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {
    @Test
    fun `state exposes selected book when local data source succeeds`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val dataSource = FakeBookLocalDataSource()
                val book = bookFixture(id = "book-42")
                dataSource.selectedBook.emit(Result.Success(book))

                val viewModel = BookDetailViewModel(bookId = book.id, bookLocalDataSource = dataSource)
                runCurrent()

                assertThat(viewModel.state.value).isEqualTo(
                    BookDetailState(isLoading = false, book = book),
                )
            } finally {
                Dispatchers.resetMain()
            }
        }
}
