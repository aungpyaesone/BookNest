package com.nps.booknest.feature.library.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.presentation.toUiText
import com.nps.booknest.feature.library.domain.BookLocalDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LibraryListViewModel(
    private val bookLocalDataSource: BookLocalDataSource,
) : ViewModel() {
    private val mutableState = MutableStateFlow(LibraryListState())
    val state = mutableState.asStateFlow()

    private val eventChannel = Channel<LibraryListEvent>(capacity = Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var observationJob: Job? = null

    init {
        observeBooks()
    }

    fun onAction(action: LibraryListAction) {
        when (action) {
            is LibraryListAction.OnBookClick -> {
                viewModelScope.launch {
                    eventChannel.send(LibraryListEvent.NavigateToBookDetail(action.bookId))
                }
            }

            LibraryListAction.OnRetryClick -> {
                observeBooks()
            }
        }
    }

    private fun observeBooks() {
        observationJob?.cancel()
        mutableState.value = mutableState.value.copy(isLoading = true)
        observationJob =
            viewModelScope.launch {
                bookLocalDataSource.observeBooks().collect { result ->
                    when (result) {
                        is Result.Success -> {
                            mutableState.value =
                                LibraryListState(
                                    isLoading = false,
                                    books = result.data,
                                )
                        }

                        is Result.Error -> {
                            mutableState.value = mutableState.value.copy(isLoading = false)
                            eventChannel.send(LibraryListEvent.ShowSnackbar(result.error.toUiText()))
                        }
                    }
                }
            }
    }
}
