package com.nps.booknest.feature.library.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryOverviewViewModel : ViewModel() {
    private val sampleBooks = libraryOverviewBooks()
    private val mutableState = MutableStateFlow(LibraryOverviewState())
    val state = mutableState.asStateFlow()
    private val eventChannel = Channel<LibraryOverviewEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    init {
        updateBooks()
    }

    fun onAction(action: LibraryOverviewAction) {
        when (action) {
            is LibraryOverviewAction.OnSearchChange -> {
                mutableState.update { it.copy(query = action.query) }
                updateBooks()
            }

            is LibraryOverviewAction.OnTabSelect -> {
                mutableState.update { it.copy(selectedTab = action.tab) }
                updateBooks()
            }

            is LibraryOverviewAction.OnSortSelect -> {
                mutableState.update { it.copy(draftSort = action.sort) }
            }

            LibraryOverviewAction.OnFilterClick -> {
                mutableState.update {
                    it.copy(isFilterVisible = true, draftSort = it.sort)
                }
            }

            LibraryOverviewAction.OnFilterDismiss -> {
                mutableState.update {
                    it.copy(isFilterVisible = false, draftSort = it.sort)
                }
            }

            LibraryOverviewAction.OnFilterApply -> {
                mutableState.update { it.copy(isFilterVisible = false, sort = it.draftSort) }
                updateBooks()
            }

            LibraryOverviewAction.OnFilterReset -> {
                mutableState.update { it.copy(draftSort = LibrarySort.RECENT) }
            }

            LibraryOverviewAction.OnReadingSeeAllClick -> {
                sendEvent(LibraryOverviewEvent.NavigateToReadingNow)
            }

            LibraryOverviewAction.OnCollectionsSeeAllClick -> {
                sendEvent(LibraryOverviewEvent.NavigateToCollections)
            }

            LibraryOverviewAction.OnMyBooksSeeAllClick -> {
                sendEvent(LibraryOverviewEvent.NavigateToMyBooks)
            }

            LibraryOverviewAction.OnComingSoonClick -> {
                viewModelScope.launch { eventChannel.send(LibraryOverviewEvent.ShowComingSoon) }
            }
        }
    }

    private fun sendEvent(event: LibraryOverviewEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }

    private fun updateBooks() {
        val current = state.value
        val matching =
            sampleBooks.filter {
                it.title.contains(current.query.trim(), true) || it.author.contains(current.query.trim(), true)
            }
        val sorted =
            when (current.sort) {
                LibrarySort.RECENT -> matching.sortedBy { it.addedDaysAgo }
                LibrarySort.TITLE -> matching.sortedBy { it.title.lowercase() }
            }
        val recent =
            sampleBooks.sortedBy { it.addedDaysAgo }.take(2).filter { book ->
                matching.any { it.id == book.id }
            }
        mutableState.update {
            it.copy(
                books = sorted.filter { book -> book.matches(it.selectedTab) },
                readingBooks = matching.filter { book -> book.isReading },
                recentlyAdded =
                    if (current.sort == LibrarySort.TITLE) {
                        recent.sortedBy { book -> book.title.lowercase() }
                    } else {
                        recent
                    },
            )
        }
    }
}

private fun LibraryBookUi.matches(tab: LibraryTab): Boolean =
    when (tab) {
        LibraryTab.ALL -> true
        LibraryTab.READING -> isReading
        LibraryTab.SAVED -> isSaved
        LibraryTab.FINISHED -> isFinished
        LibraryTab.DOWNLOADS -> isDownloaded
        LibraryTab.NOTES -> noteCount > 0
    }
