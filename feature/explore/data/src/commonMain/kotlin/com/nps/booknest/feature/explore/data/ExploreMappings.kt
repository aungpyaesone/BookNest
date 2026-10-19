package com.nps.booknest.feature.explore.data

import com.nps.booknest.core.network.resolveApiImageUrl
import com.nps.booknest.feature.explore.domain.CatalogAuthor
import com.nps.booknest.feature.explore.domain.CatalogBook
import com.nps.booknest.feature.explore.domain.CatalogCategory
import com.nps.booknest.feature.explore.domain.CatalogPage
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.CatalogSnapshot
import com.nps.booknest.feature.explore.domain.ExploreCategory

internal fun ExploreResponseDto.toCategories(): List<ExploreCategory> {
    val ids = categories.map { it.categoryId }.toSet()
    require(ids.size == categories.size)
    return categories.map {
        require(it.categoryId > 0 && it.name.isNotBlank() && it.bookCount >= 0)
        require(it.parentCategoryId == null || (it.parentCategoryId in ids && it.parentCategoryId != it.categoryId))
        ExploreCategory(
            it.categoryId,
            it.parentCategoryId,
            it.name,
            it.slug,
            it.iconKey,
            resolveApiImageUrl(it.bannerImageUrl),
            it.bookCount,
        )
    }
}

internal fun CatalogPageDto.toCatalogPage(
    query: CatalogQuery,
    requestedPage: Int,
): CatalogPage {
    require(page == requestedPage && pageSize == query.pageSize && page > 0 && total >= 0)
    require(items.size <= pageSize)
    return CatalogPage(page, pageSize, total, items.map { it.toCatalogBook() })
}

private fun CatalogCardDto.toCatalogBook(): CatalogBook {
    require(book.bookId > 0 && book.title.isNotBlank())
    require(book.purchasePrice.isFinite() && book.purchasePrice >= 0)
    require(book.rentalPrice == null || (book.rentalPrice.isFinite() && book.rentalPrice >= 0))
    require(book.currencyCode.matches(Regex("[A-Z]{3}")))
    require(reviewCount >= 0 && (rating == null || (rating.isFinite() && rating in 0.0..5.0)))
    require(authors.all { it.authorId > 0 && it.authorName.isNotBlank() })
    require(categories.all { it.categoryId > 0 })
    return CatalogBook(
        book.bookId,
        book.title,
        authors.map { CatalogAuthor(it.authorId, it.authorName) },
        categories.map { CatalogCategory(it.categoryId, it.name, it.slug, it.isPrimary) },
        resolveApiImageUrl(book.coverImageUrl),
        book.purchasePrice,
        book.rentalPrice,
        book.rentalDays,
        book.currencyCode,
        rating,
        reviewCount,
        isDemo,
    )
}

internal fun List<ExploreCategory>.toExploreDto() =
    ExploreResponseDto(
        map {
            ExploreCategoryDto(it.id, it.parentId, it.name, it.slug, it.iconKey, it.bannerUrl, it.bookCount)
        },
    )

internal fun CatalogSnapshot.toCacheDto() =
    CatalogCacheDto(
        pages.map { page ->
            CatalogPageDto(
                page.page,
                page.pageSize,
                page.total,
                page.items.map { book ->
                    CatalogCardDto(
                        CatalogBookDto(
                            book.id,
                            book.title,
                            book.coverUrl,
                            book.purchasePrice,
                            book.rentalPrice,
                            book.rentalDays,
                            book.currencyCode,
                        ),
                        book.authors.map { CatalogAuthorDto(it.id, it.name) },
                        book.categories.map { CatalogCategoryDto(it.id, it.name, it.slug, it.isPrimary) },
                        book.rating,
                        book.reviewCount,
                        book.isDemo,
                    )
                },
            )
        },
    )

internal fun CatalogCacheDto.toSnapshot(query: CatalogQuery): CatalogSnapshot {
    require(pages.isNotEmpty())
    return CatalogSnapshot(query, pages.mapIndexed { index, dto -> dto.toCatalogPage(query, index + 1) })
}

internal fun CatalogQuery.cacheKey(): String = "$categoryId:${sort.name}:$pageSize"
