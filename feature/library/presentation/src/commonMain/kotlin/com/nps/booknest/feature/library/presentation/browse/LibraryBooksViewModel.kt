package com.nps.booknest.feature.library.presentation.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nps.booknest.feature.library.presentation.overview.libraryOverviewBooks
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReadingNowViewModel : LibraryBooksViewModel(LibraryBooksMode.READING)

class MyBooksViewModel : LibraryBooksViewModel(LibraryBooksMode.MY_BOOKS)

class AllBooksViewModel : LibraryBooksViewModel(LibraryBooksMode.ALL_BOOKS)

abstract class LibraryBooksViewModel(
    mode: LibraryBooksMode,
) : ViewModel() {
    private val samples = libraryOverviewBooks()
    private val mutableState = MutableStateFlow(LibraryBooksState(mode))
    val state = mutableState.asStateFlow()
    private val eventChannel = Channel<LibraryBrowseEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    init {
        updateBooks()
    }

    fun onAction(action: LibraryBooksAction) {
        when (action) {
            is LibraryBooksAction.OnViewModeSelect -> {
                mutableState.update { it.copy(isGridView = action.isGrid) }
            }

            is LibraryBooksAction.OnSearchChange -> {
                mutableState.update { it.copy(query = action.query) }
            }

            is LibraryBooksAction.OnCategorySelect -> {
                mutableState.update { it.copy(category = action.category) }
            }

            is LibraryBooksAction.OnStatusSelect -> {
                mutableState.update { it.copy(status = action.status) }
            }

            is LibraryBooksAction.OnSortSelect -> {
                if (action.sort in state.value.availableSorts()) mutableState.update { it.copy(sort = action.sort) }
            }

            LibraryBooksAction.OnBackClick -> {
                sendEvent(LibraryBrowseEvent.NavigateBack)
            }

            LibraryBooksAction.OnComingSoonClick -> {
                sendEvent(LibraryBrowseEvent.ShowComingSoon)
            }

            else -> {
                updateFilters(action)
            }
        }
        updateBooks()
    }

    private fun updateFilters(action: LibraryBooksAction) {
        when (action) {
            is LibraryBooksAction.OnDraftCategorySelect -> {
                mutableState.update { it.copy(draftCategory = action.category) }
            }

            is LibraryBooksAction.OnDraftStatusSelect -> {
                mutableState.update { it.copy(draftStatus = action.status) }
            }

            LibraryBooksAction.OnSearchClick -> {
                mutableState.update { it.copy(isSearchVisible = true) }
            }

            LibraryBooksAction.OnFilterClick -> {
                mutableState.update {
                    it.copy(isFilterVisible = true, draftCategory = it.category, draftStatus = it.status)
                }
            }

            LibraryBooksAction.OnFilterDismiss -> {
                mutableState.update {
                    it.copy(isFilterVisible = false, draftCategory = it.category, draftStatus = it.status)
                }
            }

            LibraryBooksAction.OnFilterApply -> {
                mutableState.update {
                    it.copy(isFilterVisible = false, category = it.draftCategory, status = it.draftStatus)
                }
            }

            LibraryBooksAction.OnFilterReset -> {
                mutableState.update {
                    it.copy(
                        draftCategory = null,
                        draftStatus = com.nps.booknest.feature.library.presentation.overview.LibraryTab.ALL,
                    )
                }
            }

            else -> {
                return
            }
        }
    }

    private fun updateBooks() {
        val current = state.value
        val source = if (current.mode == LibraryBooksMode.READING) samples.filter { it.isReading } else samples
        val matching =
            source.filter {
                (it.title.contains(current.query.trim(), true) || it.author.contains(current.query.trim(), true)) &&
                    (current.category == null || it.category == current.category)
            }
        val filtered = matching.filter { it.matchesBrowseStatus(current.status) }
        val sorted =
            when (current.sort) {
                LibraryBrowseSort.RECENT_READ -> filtered.sortedBy { it.lastReadOrder }
                LibraryBrowseSort.TITLE -> filtered.sortedBy { it.title.lowercase() }
                LibraryBrowseSort.PROGRESS -> filtered.sortedByDescending { it.progress }
                else -> filtered.sortedBy { it.addedDaysAgo }
            }
        mutableState.update {
            it.copy(
                books = sorted,
                totalBooks = source.size,
                statusCounts =
                    libraryBrowseStatuses.associateWith { tab ->
                        source.count { book -> book.matchesBrowseStatus(tab) }
                    },
            )
        }
    }

    private fun sendEvent(event: LibraryBrowseEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}

internal fun LibraryBooksState.availableSorts(): List<LibraryBrowseSort> =
    if (mode == LibraryBooksMode.READING) {
        listOf(LibraryBrowseSort.RECENT_READ, LibraryBrowseSort.TITLE, LibraryBrowseSort.PROGRESS)
    } else {
        listOf(LibraryBrowseSort.RECENT_ADDED, LibraryBrowseSort.TITLE)
    }
