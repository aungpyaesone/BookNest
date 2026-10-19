package com.nps.booknest.feature.explore.data

import kotlinx.serialization.Serializable

@Serializable
internal data class ExploreResponseDto(
    val categories: List<ExploreCategoryDto>,
)

@Serializable
internal data class ExploreCategoryDto(
    val categoryId: Long,
    val parentCategoryId: Long?,
    val name: String,
    val slug: String,
    val iconKey: String,
    val bannerImageUrl: String?,
    val bookCount: Int,
)

@Serializable
internal data class CatalogPageDto(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val items: List<CatalogCardDto>,
)

@Serializable
internal data class CatalogCardDto(
    val book: CatalogBookDto,
    val authors: List<CatalogAuthorDto>,
    val categories: List<CatalogCategoryDto>,
    val rating: Double?,
    val reviewCount: Int,
    val isDemo: Boolean,
)

@Serializable
internal data class CatalogBookDto(
    val bookId: Long,
    val title: String,
    val coverImageUrl: String?,
    val purchasePrice: Double,
    val rentalPrice: Double?,
    val rentalDays: Int?,
    val currencyCode: String,
)

@Serializable
internal data class CatalogAuthorDto(
    val authorId: Long,
    val authorName: String,
)

@Serializable
internal data class CatalogCategoryDto(
    val categoryId: Long,
    val name: String,
    val slug: String,
    val isPrimary: Boolean,
)

@Serializable
internal data class CatalogCacheDto(
    val pages: List<CatalogPageDto>,
)
