package com.nps.booknest.feature.home.data

import com.nps.booknest.core.network.BOOK_NEST_API_URL
import com.nps.booknest.core.network.resolveApiImageUrl
import com.nps.booknest.feature.home.domain.HomeAuthor
import com.nps.booknest.feature.home.domain.HomeBanner
import com.nps.booknest.feature.home.domain.HomeBook
import com.nps.booknest.feature.home.domain.HomeBookMetadata
import com.nps.booknest.feature.home.domain.HomeCategory
import com.nps.booknest.feature.home.domain.HomeFeed

internal const val HOME_API_URL = BOOK_NEST_API_URL

internal fun HomeFeedDto.toHomeFeed(baseUrl: String = HOME_API_URL): HomeFeed {
    require(banners.map { it.bannerId }.distinct().size == banners.size)
    require(categories.map { it.categoryId }.distinct().size == categories.size)
    require(popularBooks.map { it.bookId }.distinct().size == popularBooks.size)
    return HomeFeed(
        banners =
            banners.map { banner ->
                require(banner.bannerId.isNotBlank())
                HomeBanner(
                    banner.bannerId,
                    banner.title,
                    banner.subtitle,
                    resolveHomeImageUrl(baseUrl, banner.imageUrl),
                    banner.buttonText,
                    banner.action,
                )
            },
        categories =
            categories.map { category ->
                require(category.categoryId > 0 && category.name.isNotBlank())
                HomeCategory(category.categoryId, category.name, category.slug, category.iconKey)
            },
        popularBooks = popularBooks.map { it.toHomeBook(baseUrl) },
    )
}

private fun HomeBookDto.toHomeBook(baseUrl: String): HomeBook {
    require(bookId > 0 && title.isNotBlank())
    require(purchasePrice.isFinite() && purchasePrice >= 0)
    require(rentalPrice == null || (rentalPrice.isFinite() && rentalPrice >= 0))
    require(currencyCode.matches(Regex("[A-Z]{3}")))
    require(rentalPrice == null || (rentalDays != null && rentalDays > 0))
    require(authors.all { it.authorId > 0 && it.authorName.isNotBlank() })
    return HomeBook(
        id = bookId,
        title = title,
        coverImageUrl = resolveHomeImageUrl(baseUrl, coverImageUrl),
        authors = authors.map { HomeAuthor(it.authorId, it.authorName) },
        purchasePrice = purchasePrice,
        rentalPrice = rentalPrice,
        currencyCode = currencyCode,
        rentalDays = rentalDays,
        isDemo = isDemo,
        metadata = HomeBookMetadata(pageCount, languageCode, fileFormat, isPremiumEligible),
    )
}

internal fun resolveHomeImageUrl(
    baseUrl: String,
    imageUrl: String,
): String = resolveApiImageUrl(imageUrl, baseUrl).orEmpty()

internal fun HomeFeed.toHomeFeedDto() =
    HomeFeedDto(
        banners = banners.map { HomeBannerDto(it.id, it.title, it.subtitle, it.imageUrl, it.buttonText, it.action) },
        categories = categories.map { HomeCategoryDto(it.id, it.name, it.slug, it.iconKey) },
        popularBooks =
            popularBooks.map { book ->
                HomeBookDto(
                    bookId = book.id,
                    title = book.title,
                    coverImageUrl = book.coverImageUrl,
                    authors = book.authors.map { HomeAuthorDto(it.id, it.name) },
                    purchasePrice = book.purchasePrice,
                    rentalPrice = book.rentalPrice,
                    currencyCode = book.currencyCode,
                    rentalDays = book.rentalDays,
                    pageCount = book.metadata.pageCount,
                    languageCode = book.metadata.languageCode,
                    fileFormat = book.metadata.fileFormat,
                    isPremiumEligible = book.metadata.isPremiumEligible,
                    isDemo = book.isDemo,
                )
            },
    )
