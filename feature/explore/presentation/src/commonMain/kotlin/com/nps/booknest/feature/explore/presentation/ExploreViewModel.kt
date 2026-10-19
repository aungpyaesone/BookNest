package com.nps.booknest.feature.explore.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.explore.domain.ExploreCategory
import com.nps.booknest.feature.explore.domain.ExploreRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val repository: ExploreRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(ExploreState())
    val state = mutableState.asStateFlow()
    private val eventChannel = Channel<ExploreEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()
    private var categories = emptyList<ExploreCategory>()
    private var hasRefreshed = false
    private var isRefreshing = false

    init {
        viewModelScope.launch {
            repository.observeCategories().collect { result ->
                when (result) {
                    is Result.Error -> {
                        mutableState.update { it.copy(error = result.error.toExploreUiText()) }
                    }

                    is Result.Success -> {
                        result.data?.let { feed ->
                            categories = feed
                            mutableState.update {
                                it.copy(
                                    categories = filteredCategories(it.query),
                                    hasLoaded = true,
                                    isLoading = false,
                                    isShowingCachedData =
                                        !hasRefreshed || it.error != null,
                                )
                            }
                        }
                    }
                }
            }
        }
        refresh()
    }

    fun onAction(action: ExploreAction) {
        when (action) {
            is ExploreAction.OnSearchChange -> {
                mutableState.update { it.copy(query = action.query, categories = filteredCategories(action.query)) }
            }

            is ExploreAction.OnCategoryClick -> {
                if (categories.any { it.id == action.categoryId && it.parentId == null }) {
                    viewModelScope.launch {
                        eventChannel.send(
                            ExploreEvent.NavigateToCategory(action.categoryId.toString()),
                        )
                    }
                }
            }

            ExploreAction.OnRefreshClick -> {
                refresh()
            }
        }
    }

    private fun filteredCategories(query: String) =
        categories
            .filter {
                it.parentId == null &&
                    it.name.contains(query.trim(), true)
            }.map { it.toExploreUi() }

    private fun refresh() {
        if (isRefreshing) return
        isRefreshing = true
        mutableState.update { it.copy(isLoading = !it.hasLoaded, isRefreshing = true) }
        viewModelScope.launch {
            try {
                when (val result = repository.refreshCategories()) {
                    is Result.Error -> {
                        mutableState.update {
                            it.copy(
                                error = result.error.toExploreUiText(),
                                isShowingCachedData = it.hasLoaded,
                            )
                        }
                    }

                    is Result.Success -> {
                        hasRefreshed = true
                        mutableState.update { it.copy(error = null, isShowingCachedData = false) }
                    }
                }
            } finally {
                isRefreshing = false
                mutableState.update { it.copy(isLoading = false, isRefreshing = false) }
            }
        }
    }
}
