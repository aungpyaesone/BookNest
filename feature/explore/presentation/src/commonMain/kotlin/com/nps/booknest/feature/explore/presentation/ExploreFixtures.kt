package com.nps.booknest.feature.explore.presentation

internal fun explorePreviewState() =
    ExploreState(
        categories =
            listOf(
                ExploreCategoryUi(1, "Fiction", "book-open"),
                ExploreCategoryUi(2, "Education", "graduation-cap"),
            ),
        isLoading = false,
        hasLoaded = true,
    )

internal fun categoryBooksPreviewState() =
    CategoryBooksState(
        category = ExploreCategoryUi(1, "Fiction", "book-open"),
        books = listOf(ExploreBookUi(10, "The Midnight Library", "Matt Haig", "¥1,200", null, true)),
        total = 1,
        hasLoaded = true,
        hasShownCatalog = true,
        isLoading = false,
    )
