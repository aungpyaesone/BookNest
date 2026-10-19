package com.nps.booknest.feature.library.presentation.browse

import com.nps.booknest.feature.library.presentation.overview.LibraryBookUi
import com.nps.booknest.feature.library.presentation.overview.LibraryCategory
import com.nps.booknest.feature.library.presentation.overview.LibraryTab

enum class LibraryBooksMode { READING, MY_BOOKS, ALL_BOOKS }

enum class LibraryBrowseSort { RECENT_READ, RECENT_ADDED, RECENT_UPDATED, TITLE, PROGRESS }

enum class LibraryBookStatus { READING, FINISHED, SAVED, DOWNLOADED, AVAILABLE }

data class LibraryBooksState(
    val mode: LibraryBooksMode,
    val query: String = "",
    val category: LibraryCategory? = null,
    val status: LibraryTab = LibraryTab.ALL,
    val draftCategory: LibraryCategory? = null,
    val draftStatus: LibraryTab = LibraryTab.ALL,
    val sort: LibraryBrowseSort =
        if (mode ==
            LibraryBooksMode.READING
        ) {
            LibraryBrowseSort.RECENT_READ
        } else {
            LibraryBrowseSort.RECENT_ADDED
        },
    val isSearchVisible: Boolean = mode != LibraryBooksMode.READING,
    val isGridView: Boolean = mode == LibraryBooksMode.ALL_BOOKS,
    val totalBooks: Int = 0,
    val isFilterVisible: Boolean = false,
    val books: List<LibraryBookUi> = emptyList(),
    val statusCounts: Map<LibraryTab, Int> = emptyMap(),
)

sealed interface LibraryBooksAction {
    data class OnSearchChange(
        val query: String,
    ) : LibraryBooksAction

    data class OnCategorySelect(
        val category: LibraryCategory?,
    ) : LibraryBooksAction

    data class OnStatusSelect(
        val status: LibraryTab,
    ) : LibraryBooksAction

    data class OnSortSelect(
        val sort: LibraryBrowseSort,
    ) : LibraryBooksAction

    data class OnDraftCategorySelect(
        val category: LibraryCategory?,
    ) : LibraryBooksAction

    data class OnDraftStatusSelect(
        val status: LibraryTab,
    ) : LibraryBooksAction

    data class OnViewModeSelect(
        val isGrid: Boolean,
    ) : LibraryBooksAction

    data object OnSearchClick : LibraryBooksAction

    data object OnFilterClick : LibraryBooksAction

    data object OnFilterDismiss : LibraryBooksAction

    data object OnFilterApply : LibraryBooksAction

    data object OnFilterReset : LibraryBooksAction

    data object OnBackClick : LibraryBooksAction

    data object OnComingSoonClick : LibraryBooksAction
}

sealed interface LibraryBrowseEvent {
    data object NavigateBack : LibraryBrowseEvent

    data object ShowComingSoon : LibraryBrowseEvent
}

internal val libraryBrowseStatuses =
    listOf(LibraryTab.ALL, LibraryTab.READING, LibraryTab.SAVED, LibraryTab.FINISHED, LibraryTab.DOWNLOADS)

internal fun LibraryBookUi.matchesBrowseStatus(status: LibraryTab): Boolean =
    when (status) {
        LibraryTab.ALL -> true
        LibraryTab.READING -> isReading
        LibraryTab.SAVED -> isSaved
        LibraryTab.FINISHED -> isFinished
        LibraryTab.DOWNLOADS -> isDownloaded
        LibraryTab.NOTES -> noteCount > 0
    }

internal fun LibraryBookUi.browseStatus(): LibraryBookStatus =
    when {
        isReading -> LibraryBookStatus.READING
        isFinished -> LibraryBookStatus.FINISHED
        isSaved -> LibraryBookStatus.SAVED
        isDownloaded -> LibraryBookStatus.DOWNLOADED
        else -> LibraryBookStatus.AVAILABLE
    }
