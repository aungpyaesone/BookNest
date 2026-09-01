package com.nps.booknest.feature.library.presentation.list

import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.library.domain.Book

data class LibraryListState(
    val isLoading: Boolean = true,
    val books: List<Book> = emptyList(),
)

sealed interface LibraryListAction {
    data class OnBookClick(
        val bookId: String,
    ) : LibraryListAction

    data object OnRetryClick : LibraryListAction
}

sealed interface LibraryListEvent {
    data class NavigateToBookDetail(
        val bookId: String,
    ) : LibraryListEvent

    data class ShowSnackbar(
        val message: UiText,
    ) : LibraryListEvent
}
