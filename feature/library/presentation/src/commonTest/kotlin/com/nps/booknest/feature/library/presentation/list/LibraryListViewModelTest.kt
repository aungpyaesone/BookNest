package com.nps.booknest.feature.library.presentation.list

import app.cash.turbine.test
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
class LibraryListViewModelTest {
    @Test
    fun `state exposes books when local data source succeeds`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val dataSource = FakeBookLocalDataSource()
                val book = bookFixture()
                dataSource.books.emit(Result.Success(listOf(book)))

                val viewModel = LibraryListViewModel(dataSource)
                runCurrent()

                assertThat(viewModel.state.value).isEqualTo(
                    LibraryListState(isLoading = false, books = listOf(book)),
                )
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `book click emits navigation event with selected identifier`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val dataSource = FakeBookLocalDataSource()
                val viewModel = LibraryListViewModel(dataSource)

                viewModel.events.test {
                    viewModel.onAction(LibraryListAction.OnBookClick("book-42"))
                    runCurrent()
                    assertThat(awaitItem()).isEqualTo(
                        LibraryListEvent.NavigateToBookDetail("book-42"),
                    )
                }
            } finally {
                Dispatchers.resetMain()
            }
        }
}
