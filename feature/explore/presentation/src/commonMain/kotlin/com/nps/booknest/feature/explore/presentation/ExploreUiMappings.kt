package com.nps.booknest.feature.explore.presentation

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.core.presentation.formatBookPrice
import com.nps.booknest.feature.explore.domain.CatalogBook
import com.nps.booknest.feature.explore.domain.ExploreCategory
import com.nps.booknest.feature.explore.presentation.generated.resources.Res
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_error_network
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_error_response
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_error_save
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_error_timeout

internal fun ExploreCategory.toExploreUi() = ExploreCategoryUi(id, name, iconKey, parentId, slug, bannerUrl, bookCount)

internal fun CatalogBook.toExploreUi() =
    ExploreBookUi(
        id,
        title,
        authors.joinToString {
            it.name
        },
        formatBookPrice(purchasePrice, currencyCode),
        coverUrl,
        isDemo,
    )

internal fun DataError.toExploreUiText(): UiText =
    UiText.Resource(
        when (this) {
            is DataError.Local -> Res.string.explore_error_save
            DataError.Network.REQUEST_TIMEOUT -> Res.string.explore_error_timeout
            DataError.Network.SERIALIZATION -> Res.string.explore_error_response
            else -> Res.string.explore_error_network
        },
    )
