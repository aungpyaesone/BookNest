package com.nps.booknest.feature.home.presentation

import androidx.compose.runtime.Stable
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_category_all
import com.nps.booknest.feature.home.presentation.generated.resources.home_category_more

enum class HomeSortOrder {
    RECOMMENDED,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
}

data class HomeBannerUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val buttonText: String,
    val action: String,
)

data class HomeCategoryUi(
    val id: Long?,
    val label: UiText,
    val iconKey: String,
)

data class HomeBookUi(
    val id: String,
    val title: String,
    val author: String,
    val priceAmount: Double,
    val priceText: String,
    val rentPriceText: String?,
    val coverImageUrl: String,
    val rentalDays: Int?,
    val isDemo: Boolean,
) {
    val isRentable: Boolean get() = rentPriceText != null
}

data class ContinueReadingUi(
    val bookId: String,
    val title: String,
    val author: String,
    val progress: Float,
    val progressText: String,
)

internal val homeAllCategory = HomeCategoryUi(null, UiText.Resource(Res.string.home_category_all), "all")
internal val homeMoreCategory = HomeCategoryUi(null, UiText.Resource(Res.string.home_category_more), "more")

@Stable
data class HomeState(
    val searchQuery: String = "",
    val selectedSortOrder: HomeSortOrder = HomeSortOrder.RECOMMENDED,
    val isRentalOnly: Boolean = false,
    val isFilterSheetVisible: Boolean = false,
    val currentHeroPage: Int = 0,
    val banners: List<HomeBannerUi> = emptyList(),
    val categories: List<HomeCategoryUi> = listOf(homeAllCategory, homeMoreCategory),
    val books: List<HomeBookUi> = emptyList(),
    val visibleBooks: List<HomeBookUi> = emptyList(),
    val continueReading: ContinueReadingUi = continueReadingFixture,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val hasLoadedFeed: Boolean = false,
    val isShowingCachedData: Boolean = false,
    val error: UiText? = null,
)

sealed interface HomeAction {
    data class OnSearchQueryChange(
        val query: String,
    ) : HomeAction

    data class OnCategoryClick(
        val category: HomeCategoryUi,
    ) : HomeAction

    data class OnHeroPageChange(
        val page: Int,
    ) : HomeAction

    data class OnBannerClick(
        val bannerId: String,
    ) : HomeAction

    data class OnSortOrderSelect(
        val sortOrder: HomeSortOrder,
    ) : HomeAction

    data class OnRentalOnlyChange(
        val isRentalOnly: Boolean,
    ) : HomeAction

    data class OnBookClick(
        val bookId: String,
    ) : HomeAction

    data class OnBuyClick(
        val bookId: String,
    ) : HomeAction

    data class OnRentClick(
        val bookId: String,
    ) : HomeAction

    data object OnRefreshClick : HomeAction

    data object OnCategorySeeAllClick : HomeAction

    data object OnFilterClick : HomeAction

    data object OnFilterDismiss : HomeAction

    data object OnApplyFilterClick : HomeAction

    data object OnExploreClick : HomeAction

    data object OnLibraryClick : HomeAction

    data object OnNotificationClick : HomeAction

    data object OnSettingsClick : HomeAction

    data class OnReadNowClick(
        val bookId: String,
    ) : HomeAction

    data object OnUpgradeClick : HomeAction
}

sealed interface HomeEvent {
    data object NavigateToExplore : HomeEvent

    data object NavigateToLibrary : HomeEvent

    data object NavigateToSettings : HomeEvent

    data class NavigateToReader(
        val bookId: String,
    ) : HomeEvent

    data class ShowSnackbar(
        val message: UiText,
    ) : HomeEvent
}

private val continueReadingFixture =
    ContinueReadingUi(
        bookId = "mountain-morning",
        title = "တောင်တန်းနောက်က နံနက်ခင်း",
        author = "သီဟနိုင်",
        progress = 0.45f,
        progressText = "၄၅%",
    )
