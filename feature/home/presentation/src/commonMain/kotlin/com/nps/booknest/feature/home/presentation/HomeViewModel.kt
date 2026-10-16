package com.nps.booknest.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_message_coming_soon
import com.nps.booknest.feature.home.presentation.generated.resources.home_message_notifications_empty
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(HomeState())
    val state = mutableState.asStateFlow()

    private val eventChannel = Channel<HomeEvent>(capacity = Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    // Exhaustive MVI dispatch keeps every HomeAction explicit and type-safe in one composition entry point.
    @Suppress("CyclomaticComplexMethod")
    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnSearchQueryChange -> updateFilters { copy(searchQuery = action.query) }

            is HomeAction.OnCategoryClick -> updateFilters { copy(selectedCategory = action.category) }

            is HomeAction.OnSortOrderSelect -> updateFilters { copy(selectedSortOrder = action.sortOrder) }

            is HomeAction.OnRentalOnlyChange -> updateFilters { copy(isRentalOnly = action.isRentalOnly) }

            is HomeAction.OnHeroPageChange -> mutableState.update { it.copy(currentHeroPage = action.page) }

            HomeAction.OnFilterClick -> mutableState.update { it.copy(isFilterSheetVisible = true) }

            HomeAction.OnFilterDismiss -> mutableState.update { it.copy(isFilterSheetVisible = false) }

            HomeAction.OnApplyFilterClick -> mutableState.update { it.copy(isFilterSheetVisible = false) }

            HomeAction.OnExploreClick -> sendEvent(HomeEvent.NavigateToExplore)

            HomeAction.OnLibraryClick -> sendEvent(HomeEvent.NavigateToLibrary)

            HomeAction.OnSettingsClick -> sendEvent(HomeEvent.NavigateToSettings)

            is HomeAction.OnReadNowClick -> sendEvent(HomeEvent.NavigateToReader(action.bookId))

            HomeAction.OnNotificationClick -> showMessage(Res.string.home_message_notifications_empty)

            HomeAction.OnUpgradeClick,
            is HomeAction.OnBookClick,
            is HomeAction.OnBuyClick,
            is HomeAction.OnRentClick,
            -> showMessage(Res.string.home_message_coming_soon)
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
                val matchesCategory = selectedCategory == HomeCategory.ALL || book.category == selectedCategory
                val matchesRental = !isRentalOnly || book.isRentable
                matchesSearch && matchesCategory && matchesRental
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
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}
