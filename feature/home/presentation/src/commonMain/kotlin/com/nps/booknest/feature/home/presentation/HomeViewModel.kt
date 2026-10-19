package com.nps.booknest.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.home.domain.HomeFeed
import com.nps.booknest.feature.home.domain.HomeRepository
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_error_network
import com.nps.booknest.feature.home.presentation.generated.resources.home_error_response
import com.nps.booknest.feature.home.presentation.generated.resources.home_error_save
import com.nps.booknest.feature.home.presentation.generated.resources.home_error_timeout
import com.nps.booknest.feature.home.presentation.generated.resources.home_message_coming_soon
import com.nps.booknest.feature.home.presentation.generated.resources.home_message_notifications_empty
import com.nps.booknest.feature.home.presentation.generated.resources.home_message_unavailable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(HomeState())
    val state = mutableState.asStateFlow()
    private val eventChannel = Channel<HomeEvent>(capacity = Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()
    private var hasRefreshed = false
    private var isRefreshRunning = false

    init {
        viewModelScope.launch {
            repository.observeHome().collect { result ->
                when (result) {
                    is Result.Success -> result.data?.let(::showFeed)
                    is Result.Error -> mutableState.update { it.copy(error = result.error.toHomeUiText()) }
                }
            }
        }
        refreshHome()
    }

    // Exhaustive MVI dispatch keeps every HomeAction explicit at the screen's composition entry point.
    @Suppress("CyclomaticComplexMethod")
    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnSearchQueryChange -> {
                updateFilters { copy(searchQuery = action.query) }
            }

            is HomeAction.OnSortOrderSelect -> {
                updateFilters { copy(selectedSortOrder = action.sortOrder) }
            }

            is HomeAction.OnRentalOnlyChange -> {
                updateFilters { copy(isRentalOnly = action.isRentalOnly) }
            }

            is HomeAction.OnCategoryClick -> {
                if (action.category == homeAllCategory) {
                    updateFilters { this }
                } else if (action.category == homeMoreCategory) {
                    sendEvent(HomeEvent.NavigateToExplore)
                } else {
                    showMessage(Res.string.home_message_coming_soon)
                }
            }

            is HomeAction.OnHeroPageChange -> {
                mutableState.update {
                    it.copy(currentHeroPage = action.page.coerceIn(0, (it.banners.size - 1).coerceAtLeast(0)))
                }
            }

            is HomeAction.OnBannerClick -> {
                openBanner(action.bannerId)
            }

            HomeAction.OnRefreshClick -> {
                refreshHome()
            }

            HomeAction.OnFilterClick -> {
                mutableState.update { it.copy(isFilterSheetVisible = true) }
            }

            HomeAction.OnFilterDismiss, HomeAction.OnApplyFilterClick -> {
                mutableState.update { it.copy(isFilterSheetVisible = false) }
            }

            HomeAction.OnExploreClick, HomeAction.OnCategorySeeAllClick -> {
                sendEvent(HomeEvent.NavigateToExplore)
            }

            HomeAction.OnLibraryClick -> {
                sendEvent(HomeEvent.NavigateToLibrary)
            }

            HomeAction.OnSettingsClick -> {
                sendEvent(HomeEvent.NavigateToSettings)
            }

            is HomeAction.OnReadNowClick -> {
                sendEvent(HomeEvent.NavigateToReader(action.bookId))
            }

            HomeAction.OnNotificationClick -> {
                showMessage(Res.string.home_message_notifications_empty)
            }

            HomeAction.OnUpgradeClick,
            is HomeAction.OnBookClick, is HomeAction.OnBuyClick, is HomeAction.OnRentClick,
            -> {
                showMessage(Res.string.home_message_coming_soon)
            }
        }
    }

    private fun refreshHome() {
        if (isRefreshRunning) return
        isRefreshRunning = true
        mutableState.update { it.copy(isLoading = !it.hasLoadedFeed, isRefreshing = true) }
        viewModelScope.launch {
            try {
                when (val result = repository.refreshHome()) {
                    is Result.Success -> {
                        hasRefreshed = true
                        mutableState.update { it.copy(error = null, isShowingCachedData = false) }
                    }

                    is Result.Error -> {
                        mutableState.update {
                            it.copy(error = result.error.toHomeUiText(), isShowingCachedData = it.hasLoadedFeed)
                        }
                    }
                }
            } finally {
                isRefreshRunning = false
                mutableState.update { it.copy(isLoading = false, isRefreshing = false) }
            }
        }
    }

    private fun showFeed(feed: HomeFeed) {
        mutableState.update { current ->
            val books = feed.popularBooks.map { it.toHomeBookUi() }
            val updated =
                current.copy(
                    banners = feed.banners.map { it.toHomeBannerUi() },
                    categories =
                        listOf(homeAllCategory) + feed.categories.map { it.toHomeCategoryUi() } + homeMoreCategory,
                    books = books,
                    currentHeroPage = current.currentHeroPage.coerceIn(0, (feed.banners.size - 1).coerceAtLeast(0)),
                    hasLoadedFeed = true,
                    isLoading = false,
                    isShowingCachedData = !hasRefreshed || current.error != null,
                )
            updated.copy(visibleBooks = updated.filteredBooks())
        }
    }

    private fun openBanner(bannerId: String) {
        val banner = state.value.banners.firstOrNull { it.id == bannerId }
        if (banner?.action == "OPEN_EXPLORE") {
            sendEvent(HomeEvent.NavigateToExplore)
        } else {
            showMessage(Res.string.home_message_unavailable)
        }
    }

    private fun updateFilters(transform: HomeState.() -> HomeState) {
        mutableState.update { current ->
            val updated = current.transform()
            updated.copy(visibleBooks = updated.filteredBooks())
        }
    }

    private fun HomeState.filteredBooks(): List<HomeBookUi> {
        val filtered =
            books.filter { book ->
                val matchesSearch =
                    searchQuery.isBlank() ||
                        book.title.contains(searchQuery, ignoreCase = true) ||
                        book.author.contains(searchQuery, ignoreCase = true)
                matchesSearch && (!isRentalOnly || book.isRentable)
            }
        return when (selectedSortOrder) {
            HomeSortOrder.RECOMMENDED -> filtered
            HomeSortOrder.PRICE_LOW_TO_HIGH -> filtered.sortedBy(HomeBookUi::priceAmount)
            HomeSortOrder.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending(HomeBookUi::priceAmount)
        }
    }

    private fun showMessage(resource: org.jetbrains.compose.resources.StringResource) {
        sendEvent(HomeEvent.ShowSnackbar(UiText.Resource(resource)))
    }

    private fun sendEvent(event: HomeEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}

private fun DataError.toHomeUiText(): UiText =
    UiText.Resource(
        when (this) {
            is DataError.Local -> Res.string.home_error_save
            DataError.Network.REQUEST_TIMEOUT -> Res.string.home_error_timeout
            DataError.Network.SERIALIZATION -> Res.string.home_error_response
            else -> Res.string.home_error_network
        },
    )
