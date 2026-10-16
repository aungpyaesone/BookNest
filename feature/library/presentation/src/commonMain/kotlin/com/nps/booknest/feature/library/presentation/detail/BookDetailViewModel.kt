package com.nps.booknest.feature.library.presentation.detail

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

class BookDetailViewModel(
    private val bookId: String,
    private val bookLocalDataSource: BookLocalDataSource,
) : ViewModel() {
    private val mutableState = MutableStateFlow(BookDetailState())
    val state = mutableState.asStateFlow()

    private val eventChannel = Channel<BookDetailEvent>(capacity = Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var observationJob: Job? = null

    init {
        observeBook()
    }

    fun onAction(action: BookDetailAction) {
        when (action) {
            BookDetailAction.OnRetryClick -> observeBook()
            BookDetailAction.OnReadClick -> sendEvent(BookDetailEvent.NavigateToReader(bookId))
        }
    }

    private fun observeBook() {
        observationJob?.cancel()
        mutableState.value = mutableState.value.copy(isLoading = true)
        observationJob =
            viewModelScope.launch {
                bookLocalDataSource.observeBook(bookId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            mutableState.value =
                                BookDetailState(
                                    isLoading = false,
                                    book = result.data,
                                )
                        }

                        is Result.Error -> {
                            mutableState.value = mutableState.value.copy(isLoading = false)
                            eventChannel.send(BookDetailEvent.ShowSnackbar(result.error.toUiText()))
                        }
                    }
                }
            }
    }

    private fun sendEvent(event: BookDetailEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}
