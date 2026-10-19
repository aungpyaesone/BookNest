package com.nps.booknest.feature.explore.presentation

import androidx.compose.runtime.Stable
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.explore.domain.CatalogSort

data class ExploreCategoryUi(
    val id: Long,
    val name: String,
    val iconKey: String,
    val parentId: Long? = null,
    val slug: String = "",
    val bannerUrl: String? = null,
    val bookCount: Int = 0,
)

data class ExploreBookUi(
    val id: Long,
    val title: String,
    val author: String,
    val priceText: String,
    val coverUrl: String?,
    val isDemo: Boolean = false,
)

@Stable
data class ExploreState(
    val query: String = "",
    val categories: List<ExploreCategoryUi> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val isShowingCachedData: Boolean = false,
    val error: UiText? = null,
)

sealed interface ExploreAction {
    data class OnSearchChange(
        val query: String,
    ) : ExploreAction

    data class OnCategoryClick(
        val categoryId: Long,
    ) : ExploreAction

    data object OnRefreshClick : ExploreAction
}

sealed interface ExploreEvent {
    data class NavigateToCategory(
        val categoryId: String,
    ) : ExploreEvent
}

@Stable
data class CategoryBooksState(
    val category: ExploreCategoryUi? = null,
    val books: List<ExploreBookUi> = emptyList(),
    val subcategories: List<ExploreCategoryUi> = emptyList(),
    val selectedSubcategoryId: Long? = null,
    val sort: CatalogSort = CatalogSort.POPULAR,
    val isSortVisible: Boolean = false,
    val total: Int = 0,
    val hasMore: Boolean = false,
    val queryVersion: Int = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isAppending: Boolean = false,
    val hasLoaded: Boolean = false,
    val hasShownCatalog: Boolean = false,
    val isShowingCachedData: Boolean = false,
    val error: UiText? = null,
    val appendError: UiText? = null,
)

sealed interface CategoryBooksAction {
    data class OnSubcategoryClick(
        val id: Long?,
    ) : CategoryBooksAction

    data class OnSortSelect(
        val sort: CatalogSort,
    ) : CategoryBooksAction

    data object OnSortClick : CategoryBooksAction

    data object OnSortDismiss : CategoryBooksAction

    data object OnRefreshClick : CategoryBooksAction

    data object OnLoadNextPage : CategoryBooksAction

    data object OnRetryAppend : CategoryBooksAction

    data object OnBackClick : CategoryBooksAction

    data object OnBookClick : CategoryBooksAction
}

sealed interface CategoryBooksEvent {
    data object NavigateBack : CategoryBooksEvent

    data object ShowComingSoon : CategoryBooksEvent
}
