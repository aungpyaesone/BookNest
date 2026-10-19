package com.nps.booknest.feature.home.presentation

import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.home.domain.HomeBanner
import com.nps.booknest.feature.home.domain.HomeBook
import com.nps.booknest.feature.home.domain.HomeCategory

internal fun HomeBanner.toHomeBannerUi() = HomeBannerUi(id, title, subtitle, imageUrl, buttonText, action)

internal fun HomeCategory.toHomeCategoryUi() = HomeCategoryUi(id, UiText.DynamicString(name), iconKey)

internal fun HomeBook.toHomeBookUi() =
    HomeBookUi(
        id = id.toString(),
        title = title,
        author = authors.joinToString { it.name },
        priceAmount = purchasePrice,
        priceText = formatHomePrice(purchasePrice, currencyCode),
        rentPriceText = rentalPrice?.let { formatHomePrice(it, currencyCode) },
        coverImageUrl = coverImageUrl,
        rentalDays = rentalDays,
        isDemo = isDemo,
    )

internal expect fun formatHomePrice(
    amount: Double,
    currencyCode: String,
): String
