package com.nps.booknest.feature.home.domain

data class HomeFeed(
    val banners: List<HomeBanner>,
    val categories: List<HomeCategory>,
    val popularBooks: List<HomeBook>,
)

data class HomeBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val buttonText: String,
    val action: String,
)

data class HomeCategory(
    val id: Long,
    val name: String,
    val slug: String,
    val iconKey: String,
)

data class HomeAuthor(
    val id: Long,
    val name: String,
)

data class HomeBook(
    val id: Long,
    val title: String,
    val coverImageUrl: String,
    val authors: List<HomeAuthor>,
    val purchasePrice: Double,
    val rentalPrice: Double?,
    val currencyCode: String,
    val rentalDays: Int?,
    val isDemo: Boolean,
    val metadata: HomeBookMetadata,
)

data class HomeBookMetadata(
    val pageCount: Int,
    val languageCode: String,
    val fileFormat: String,
    val isPremiumEligible: Boolean,
)
