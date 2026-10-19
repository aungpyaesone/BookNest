package com.nps.booknest.feature.library.presentation.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CollectionsViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(CollectionsState())
    val state = mutableState.asStateFlow()
    private val eventChannel = Channel<LibraryBrowseEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    init {
        updateCollections()
    }

    fun onAction(action: CollectionsAction) {
        when (action) {
            is CollectionsAction.OnSearchChange -> {
                mutableState.update { it.copy(query = action.query) }
            }

            is CollectionsAction.OnTypeSelect -> {
                mutableState.update { it.copy(type = action.type) }
            }

            is CollectionsAction.OnDraftTypeSelect -> {
                mutableState.update { it.copy(draftType = action.type) }
            }

            is CollectionsAction.OnSortSelect -> {
                if (action.sort in
                    collectionSorts
                ) {
                    mutableState.update { it.copy(sort = action.sort) }
                }
            }

            CollectionsAction.OnFilterClick -> {
                mutableState.update { it.copy(isFilterVisible = true, draftType = it.type) }
            }

            CollectionsAction.OnFilterDismiss -> {
                mutableState.update { it.copy(isFilterVisible = false, draftType = it.type) }
            }

            CollectionsAction.OnFilterApply -> {
                mutableState.update { it.copy(isFilterVisible = false, type = it.draftType) }
            }

            CollectionsAction.OnFilterReset -> {
                mutableState.update { it.copy(draftType = CollectionType.ALL) }
            }

            CollectionsAction.OnBackClick -> {
                sendEvent(LibraryBrowseEvent.NavigateBack)
            }

            CollectionsAction.OnComingSoonClick -> {
                sendEvent(LibraryBrowseEvent.ShowComingSoon)
            }
        }
        updateCollections()
    }

    private fun updateCollections() {
        val current = state.value
        val matching =
            libraryCollectionSamples().filter {
                it.name.contains(current.query.trim(), true) &&
                    when (current.type) {
                        CollectionType.ALL -> true
                        CollectionType.MY -> !it.isShared
                        CollectionType.SHARED -> it.isShared
                        CollectionType.FAVORITES -> it.isFavorite
                    }
            }
        mutableState.update {
            it.copy(
                collections =
                    if (it.sort ==
                        LibraryBrowseSort.TITLE
                    ) {
                        matching.sortedBy { collection -> collection.name.lowercase() }
                    } else {
                        matching.sortedBy { collection -> collection.updatedDaysAgo }
                    },
            )
        }
    }

    private fun sendEvent(event: LibraryBrowseEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}

internal val collectionSorts = listOf(LibraryBrowseSort.RECENT_UPDATED, LibraryBrowseSort.TITLE)

internal fun libraryCollectionSamples() =
    listOf(
        LibraryCollectionUi(
            "personal",
            "Personal Development",
            12,
            "Build a better you, one book at a time.",
            isFavorite = true,
            updatedDaysAgo = 0,
        ),
        LibraryCollectionUi("business", "Business & Finance", 8, "Ideas for a smarter tomorrow.", updatedDaysAgo = 1),
        LibraryCollectionUi("japanese", "Japanese Learning", 6, "ことばで、もっと広がる世界。", isShared = true, updatedDaysAgo = 2),
        LibraryCollectionUi(
            "fiction",
            "Fiction Favorites",
            10,
            "Great stories, brighter days.",
            isFavorite = true,
            updatedDaysAgo = 3,
        ),
        LibraryCollectionUi(
            "travel",
            "Travel & Culture",
            5,
            "Explore the world through books.",
            isShared = true,
            updatedDaysAgo = 4,
        ),
        LibraryCollectionUi(
            "later",
            "To Read Later",
            18,
            "A longer reading list for brighter days.",
            updatedDaysAgo = 5,
        ),
    )
