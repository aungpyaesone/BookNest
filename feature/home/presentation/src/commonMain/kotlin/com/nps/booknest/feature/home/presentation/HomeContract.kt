package com.nps.booknest.feature.home.presentation

import androidx.compose.runtime.Stable
import com.nps.booknest.core.presentation.UiText

enum class HomeCategory {
    ALL,
    FICTION,
    EDUCATION,
    BUSINESS,
    SELF_HELP,
    MORE,
}

enum class HomeSortOrder {
    RECOMMENDED,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
}

enum class BookCoverStyle {
    SAND,
    FOREST,
    PLUM,
    SKY,
}

data class HomeBookUi(
    val id: String,
    val title: String,
    val author: String,
    val priceAmount: Int,
    val priceText: String,
    val rentPriceText: String,
    val category: HomeCategory,
    val coverStyle: BookCoverStyle,
    val isRentable: Boolean,
)

data class ContinueReadingUi(
    val bookId: String,
    val title: String,
    val author: String,
    val progress: Float,
    val progressText: String,
)

@Stable
data class HomeState(
    val searchQuery: String = "",
    val selectedCategory: HomeCategory = HomeCategory.ALL,
    val selectedSortOrder: HomeSortOrder = HomeSortOrder.RECOMMENDED,
    val isRentalOnly: Boolean = false,
    val isFilterSheetVisible: Boolean = false,
    val currentHeroPage: Int = 0,
    val books: List<HomeBookUi> = homeBookFixtures,
    val visibleBooks: List<HomeBookUi> = homeBookFixtures,
    val continueReading: ContinueReadingUi = continueReadingFixture,
)

sealed interface HomeAction {
    data class OnSearchQueryChange(
        val query: String,
    ) : HomeAction

    data class OnCategoryClick(
        val category: HomeCategory,
    ) : HomeAction

    data class OnHeroPageChange(
        val page: Int,
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

    data object OnFilterClick : HomeAction

    data object OnFilterDismiss : HomeAction

    data object OnApplyFilterClick : HomeAction

    data object OnExploreClick : HomeAction

    data object OnLibraryClick : HomeAction

    data object OnNotificationClick : HomeAction

    data object OnSettingsClick : HomeAction

    data object OnReadNowClick : HomeAction
}

sealed interface HomeEvent {
    data object NavigateToExplore : HomeEvent

    data object NavigateToLibrary : HomeEvent

    data object NavigateToSettings : HomeEvent

    data class ShowSnackbar(
        val message: UiText,
    ) : HomeEvent
}

internal const val HERO_PAGE_COUNT = 3

private val homeBookFixtures =
    listOf(
        HomeBookUi(
            id = "quiet-pages",
            title = "တိတ်ဆိတ်သော စာမျက်နှာများ",
            author = "မေသဇင်",
            priceAmount = 12_000,
            priceText = "၁၂,၀၀၀ ကျပ်",
            rentPriceText = "ငှားရန် ၃,၀၀၀ ကျပ်",
            category = HomeCategory.FICTION,
            coverStyle = BookCoverStyle.SAND,
            isRentable = true,
        ),
        HomeBookUi(
            id = "mindful-money",
            title = "ငွေကြေးနှင့် စိတ်ပညာ",
            author = "လင်းထက်",
            priceAmount = 10_000,
            priceText = "၁၀,၀၀၀ ကျပ်",
            rentPriceText = "ငှားရန် ၂,၅၀၀ ကျပ်",
            category = HomeCategory.BUSINESS,
            coverStyle = BookCoverStyle.FOREST,
            isRentable = true,
        ),
        HomeBookUi(
            id = "brave-thoughts",
            title = "ရဲရင့်သော အတွေးများ",
            author = "ခန့်ဇော်",
            priceAmount = 9_000,
            priceText = "၉,၀၀၀ ကျပ်",
            rentPriceText = "ငှားရန် ၂,၀၀၀ ကျပ်",
            category = HomeCategory.SELF_HELP,
            coverStyle = BookCoverStyle.PLUM,
            isRentable = true,
        ),
        HomeBookUi(
            id = "meaningful-days",
            title = "နေ့ရက်များ၏ အဓိပ္ပာယ်",
            author = "နန်းယုယု",
            priceAmount = 8_000,
            priceText = "၈,၀၀၀ ကျပ်",
            rentPriceText = "ငှားရန် ၂,၀၀၀ ကျပ်",
            category = HomeCategory.EDUCATION,
            coverStyle = BookCoverStyle.SKY,
            isRentable = false,
        ),
    )

private val continueReadingFixture =
    ContinueReadingUi(
        bookId = "mountain-morning",
        title = "တောင်တန်းနောက်က နံနက်ခင်း",
        author = "သီဟနိုင်",
        progress = 0.45f,
        progressText = "၄၅%",
    )
