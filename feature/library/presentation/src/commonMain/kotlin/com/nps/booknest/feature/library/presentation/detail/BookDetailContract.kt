package com.nps.booknest.feature.library.presentation.detail

import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.library.domain.Book

data class BookDetailState(
    val isLoading: Boolean = true,
    val book: Book? = null,
)

sealed interface BookDetailAction {
    data object OnRetryClick : BookDetailAction
}

sealed interface BookDetailEvent {
    data class ShowSnackbar(
        val message: UiText,
    ) : BookDetailEvent
}
