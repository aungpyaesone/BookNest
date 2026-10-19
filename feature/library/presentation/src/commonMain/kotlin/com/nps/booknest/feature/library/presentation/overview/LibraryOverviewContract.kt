package com.nps.booknest.feature.library.presentation.overview

import androidx.compose.runtime.Stable

enum class LibraryTab { ALL, READING, SAVED, FINISHED, DOWNLOADS, NOTES }

enum class LibrarySort { RECENT, TITLE }

enum class LibraryCategory { FICTION, EDUCATION, BUSINESS, SELF_HELP, FINANCE, LIFESTYLE, SCIENCE_FICTION }

data class LibraryBookUi(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val addedDaysAgo: Int,
    val progress: Int = 0,
    val chapter: Int = 0,
    val currentPage: Int = 0,
    val pageCount: Int = 0,
    val isReading: Boolean = false,
    val isSaved: Boolean = false,
    val isFinished: Boolean = false,
    val isDownloaded: Boolean = false,
    val noteCount: Int = 0,
    val category: LibraryCategory = LibraryCategory.FICTION,
    val chapterCount: Int = 0,
    val lastReadOrder: Int = 0,
)

@Stable
data class LibraryOverviewState(
    val query: String = "",
    val selectedTab: LibraryTab = LibraryTab.ALL,
    val sort: LibrarySort = LibrarySort.RECENT,
    val draftSort: LibrarySort = LibrarySort.RECENT,
    val isFilterVisible: Boolean = false,
    val books: List<LibraryBookUi> = emptyList(),
    val readingBooks: List<LibraryBookUi> = emptyList(),
    val recentlyAdded: List<LibraryBookUi> = emptyList(),
)

sealed interface LibraryOverviewAction {
    data class OnSearchChange(
        val query: String,
    ) : LibraryOverviewAction

    data class OnTabSelect(
        val tab: LibraryTab,
    ) : LibraryOverviewAction

    data class OnSortSelect(
        val sort: LibrarySort,
    ) : LibraryOverviewAction

    data object OnFilterClick : LibraryOverviewAction

    data object OnFilterDismiss : LibraryOverviewAction

    data object OnFilterApply : LibraryOverviewAction

    data object OnFilterReset : LibraryOverviewAction

    data object OnReadingSeeAllClick : LibraryOverviewAction

    data object OnCollectionsSeeAllClick : LibraryOverviewAction

    data object OnMyBooksSeeAllClick : LibraryOverviewAction

    data object OnComingSoonClick : LibraryOverviewAction
}

sealed interface LibraryOverviewEvent {
    data object NavigateToReadingNow : LibraryOverviewEvent

    data object NavigateToCollections : LibraryOverviewEvent

    data object NavigateToMyBooks : LibraryOverviewEvent

    data object ShowComingSoon : LibraryOverviewEvent
}
