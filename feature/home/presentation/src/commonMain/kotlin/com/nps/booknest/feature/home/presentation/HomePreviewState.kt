package com.nps.booknest.feature.home.presentation

// Explicit preview data is never used as a network or cache fallback.
internal fun homePreviewState(): HomeState {
    val book =
        HomeBookUi(
            id = "quiet-pages",
            title = "တိတ်ဆိတ်သော စာမျက်နှာများ",
            author = "မေသဇင်",
            priceAmount = 1200.0,
            priceText = "JPY 1,200",
            rentPriceText = "JPY 300",
            coverImageUrl = "",
            rentalDays = 30,
            isDemo = true,
        )
    return HomeState(
        banners =
            listOf(
                HomeBannerUi(
                    "preview",
                    "Good Books.\nGreat moments.",
                    "Buy, rent and read.",
                    "",
                    "Explore Now",
                    "OPEN_EXPLORE",
                ),
            ),
        books = listOf(book),
        visibleBooks = listOf(book),
        isLoading = false,
        hasLoadedFeed = true,
    )
}
