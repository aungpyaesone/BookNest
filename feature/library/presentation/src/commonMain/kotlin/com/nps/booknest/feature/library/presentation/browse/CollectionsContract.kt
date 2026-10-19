package com.nps.booknest.feature.library.presentation.browse

enum class CollectionType { ALL, MY, SHARED, FAVORITES }

data class LibraryCollectionUi(
    val id: String,
    val name: String,
    val bookCount: Int,
    val description: String,
    val isShared: Boolean = false,
    val isFavorite: Boolean = false,
    val updatedDaysAgo: Int,
)

data class CollectionsState(
    val query: String = "",
    val type: CollectionType = CollectionType.ALL,
    val draftType: CollectionType = CollectionType.ALL,
    val sort: LibraryBrowseSort = LibraryBrowseSort.RECENT_UPDATED,
    val isFilterVisible: Boolean = false,
    val collections: List<LibraryCollectionUi> = emptyList(),
)

sealed interface CollectionsAction {
    data class OnSearchChange(
        val query: String,
    ) : CollectionsAction

    data class OnTypeSelect(
        val type: CollectionType,
    ) : CollectionsAction

    data class OnDraftTypeSelect(
        val type: CollectionType,
    ) : CollectionsAction

    data class OnSortSelect(
        val sort: LibraryBrowseSort,
    ) : CollectionsAction

    data object OnFilterClick : CollectionsAction

    data object OnFilterDismiss : CollectionsAction

    data object OnFilterApply : CollectionsAction

    data object OnFilterReset : CollectionsAction

    data object OnBackClick : CollectionsAction

    data object OnComingSoonClick : CollectionsAction
}
