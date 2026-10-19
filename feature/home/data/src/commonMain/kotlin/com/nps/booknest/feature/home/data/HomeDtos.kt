package com.nps.booknest.feature.home.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class HomeFeedDto(
    val banners: List<HomeBannerDto>,
    val categories: List<HomeCategoryDto>,
    val popularBooks: List<HomeBookDto>,
)

@Serializable
internal data class HomeBannerDto(
    val bannerId: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val buttonText: String,
    val action: String,
)

@Serializable
internal data class HomeCategoryDto(
    val categoryId: Long,
    val name: String,
    val slug: String,
    val iconKey: String,
)

@Serializable
internal data class HomeAuthorDto(
    val authorId: Long,
    val authorName: String,
)

// The flat constructor mirrors the published JSON contract; metadata is grouped in the domain model.
@Suppress("LongParameterList")
@Serializable
internal data class HomeBookDto(
    val bookId: Long,
    val title: String,
    val coverImageUrl: String,
    val authors: List<HomeAuthorDto>,
    val purchasePrice: Double,
    val rentalPrice: Double? = null,
    val currencyCode: String,
    val rentalDays: Int? = null,
    val pageCount: Int,
    val languageCode: String,
    val fileFormat: String,
    @SerialName("premiumEligible")
    val isPremiumEligible: Boolean,
    val isDemo: Boolean,
)
