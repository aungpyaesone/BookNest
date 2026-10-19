package com.nps.booknest.feature.explore.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.ExploreCategory
import com.nps.booknest.feature.explore.domain.ExploreRepository
import com.nps.booknest.feature.explore.presentation.generated.resources.Res
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_category_unavailable
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryBooksViewModel(
    private val categoryId: String,
    private val repository: ExploreRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(CategoryBooksState())
    val state = mutableState.asStateFlow()
    private val eventChannel = Channel<CategoryBooksEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()
    private var activeQuery: CatalogQuery? = null
    private var observation: Job? = null
    private var request: Job? = null
    private var taxonomyRequest: Job? = null
    private var hasRequestedTaxonomy = false
    private var lastTaxonomy: List<ExploreCategory>? = null
    private var generation = 0
    private var hasRefreshed = false

    init {
        viewModelScope.launch {
            repository.observeCategories().collect { result ->
                when (result) {
                    is Result.Error -> {
                        if (activeQuery == null) mutableState.update { it.copy(error = result.error.toExploreUiText()) }
                        ensureTaxonomy()
                    }

                    is Result.Success -> {
                        val taxonomy = result.data
                        if (taxonomy == null) {
                            ensureTaxonomy()
                        } else {
                            lastTaxonomy = taxonomy
                            showTaxonomy(taxonomy)
                        }
                    }
                }
            }
        }
    }

    // Every UI action is dispatched explicitly; async query changes cancel the preceding generation.
    @Suppress("CyclomaticComplexMethod")
    fun onAction(action: CategoryBooksAction) {
        when (action) {
            is CategoryBooksAction.OnSubcategoryClick -> {
                if (action.id == null || state.value.subcategories.any { it.id == action.id }) {
                    mutableState.update { it.copy(selectedSubcategoryId = action.id) }
                    selectQuery()
                }
            }

            is CategoryBooksAction.OnSortSelect -> {
                mutableState.update { it.copy(sort = action.sort, isSortVisible = false) }
                selectQuery()
            }

            CategoryBooksAction.OnSortClick -> {
                mutableState.update { it.copy(isSortVisible = true) }
            }

            CategoryBooksAction.OnSortDismiss -> {
                mutableState.update { it.copy(isSortVisible = false) }
            }

            CategoryBooksAction.OnRefreshClick -> {
                if (activeQuery ==
                    null
                ) {
                    ensureTaxonomy(force = true)
                } else {
                    fetch(append = false)
                }
            }

            CategoryBooksAction.OnLoadNextPage -> {
                if (state.value.appendError == null) fetch(append = true)
            }

            CategoryBooksAction.OnRetryAppend -> {
                fetch(append = true)
            }

            CategoryBooksAction.OnBackClick -> {
                sendEvent(CategoryBooksEvent.NavigateBack)
            }

            CategoryBooksAction.OnBookClick -> {
                sendEvent(CategoryBooksEvent.ShowComingSoon)
            }
        }
    }

    private fun showTaxonomy(categories: List<ExploreCategory>) {
        val root = categories.firstOrNull { it.id == categoryId.toLongOrNull() || it.slug == categoryId }
        if (root == null) {
            clearUnavailableCategory()
            if (!hasRequestedTaxonomy) {
                ensureTaxonomy()
            } else {
                mutableState.update {
                    it.copy(isLoading = false, error = UiText.Resource(Res.string.explore_category_unavailable))
                }
            }
        } else {
            val children = categories.filter { it.parentId == root.id }
            mutableState.update {
                it.copy(
                    category = root.toExploreUi(),
                    subcategories = children.map { child -> child.toExploreUi() },
                    selectedSubcategoryId =
                        it.selectedSubcategoryId?.takeIf { id ->
                            children.any { child ->
                                child.id ==
                                    id
                            }
                        },
                )
            }
            selectQuery()
        }
    }

    private fun clearUnavailableCategory() {
        if (activeQuery == null) return
        observation?.cancel()
        request?.cancel()
        activeQuery = null
        generation++
        mutableState.update {
            it.copy(
                category = null,
                subcategories = emptyList(),
                selectedSubcategoryId = null,
                books = emptyList(),
                total = 0,
                hasLoaded = false,
                hasShownCatalog = false,
                hasMore = false,
                isRefreshing = false,
                isAppending = false,
                isShowingCachedData = false,
                appendError = null,
            )
        }
    }

    private fun ensureTaxonomy(force: Boolean = false) {
        if ((!force && hasRequestedTaxonomy) || taxonomyRequest?.isActive == true) return
        hasRequestedTaxonomy = true
        mutableState.update { it.copy(isLoading = true, error = null) }
        taxonomyRequest =
            viewModelScope.launch {
                when (val result = repository.refreshCategories()) {
                    is Result.Error -> {
                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                error = result.error.toExploreUiText(),
                            )
                        }
                    }

                    is Result.Success -> {
                        lastTaxonomy?.let { if (activeQuery == null) showTaxonomy(it) }
                    }
                }
            }
    }

    private fun selectQuery() {
        val root = state.value.category ?: return
        val query = CatalogQuery(state.value.selectedSubcategoryId ?: root.id, state.value.sort)
        if (activeQuery == query) return
        observation?.cancel()
        request?.cancel()
        activeQuery = query
        generation++
        hasRefreshed = false
        val version = generation
        mutableState.update {
            it.copy(
                books = emptyList(),
                total = 0,
                hasMore = false,
                hasLoaded = false,
                isShowingCachedData = false,
                isLoading = true,
                isRefreshing = false,
                isAppending = false,
                error = null,
                appendError = null,
                queryVersion = version,
            )
        }
        observation =
            viewModelScope.launch {
                repository.observeCatalog(query).collect { result ->
                    if (version == generation) {
                        when (result) {
                            is Result.Error -> {
                                mutableState.update { it.copy(error = result.error.toExploreUiText()) }
                            }

                            is Result.Success -> {
                                result.data?.let { snapshot ->
                                    mutableState.update {
                                        it.copy(
                                            books =
                                                snapshot.books.map { book ->
                                                    book.toExploreUi()
                                                },
                                            total = snapshot.total,
                                            hasMore = snapshot.hasMore,
                                            hasLoaded = true,
                                            hasShownCatalog = true,
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
            }
        fetch(append = false)
    }

    private fun fetch(append: Boolean) {
        val query = activeQuery ?: return
        if (request?.isActive == true) return
        if (append && (!state.value.hasLoaded || !state.value.hasMore)) return
        val version = generation
        mutableState.update {
            it.copy(
                isLoading = !it.hasLoaded,
                isRefreshing = !append,
                isAppending = append,
                appendError = if (append) null else it.appendError,
            )
        }
        request =
            viewModelScope.launch {
                try {
                    val result = if (append) repository.loadNextPage(query) else repository.refreshCatalog(query)
                    if (version == generation) showRequestResult(result, append)
                } finally {
                    if (version ==
                        generation
                    ) {
                        mutableState.update { it.copy(isLoading = false, isRefreshing = false, isAppending = false) }
                    }
                }
            }
    }

    private fun showRequestResult(
        result: com.nps.booknest.core.domain.EmptyResult<com.nps.booknest.core.domain.DataError>,
        append: Boolean,
    ) {
        when (result) {
            is Result.Success -> {
                hasRefreshed = true
                mutableState.update { it.copy(error = null, appendError = null, isShowingCachedData = false) }
            }

            is Result.Error -> {
                mutableState.update {
                    if (append) {
                        it.copy(appendError = result.error.toExploreUiText())
                    } else {
                        it.copy(error = result.error.toExploreUiText(), isShowingCachedData = it.hasLoaded)
                    }
                }
            }
        }
    }

    private fun sendEvent(event: CategoryBooksEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}
