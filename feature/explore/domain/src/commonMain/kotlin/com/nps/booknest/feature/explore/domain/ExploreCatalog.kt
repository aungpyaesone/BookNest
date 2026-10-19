package com.nps.booknest.feature.explore.domain

data class ExploreCategory(
    val id: Long,
    val parentId: Long?,
    val name: String,
    val slug: String,
    val iconKey: String,
    val bannerUrl: String?,
    val bookCount: Int,
)

data class CatalogAuthor(
    val id: Long,
    val name: String,
)

data class CatalogCategory(
    val id: Long,
    val name: String,
    val slug: String,
    val isPrimary: Boolean,
)

data class CatalogBook(
    val id: Long,
    val title: String,
    val authors: List<CatalogAuthor>,
    val categories: List<CatalogCategory>,
    val coverUrl: String?,
    val purchasePrice: Double,
    val rentalPrice: Double?,
    val rentalDays: Int?,
    val currencyCode: String,
    val rating: Double?,
    val reviewCount: Int,
    val isDemo: Boolean,
)

enum class CatalogSort { POPULAR, RECENT, TITLE, PRICE }

data class CatalogQuery(
    val categoryId: Long,
    val sort: CatalogSort = CatalogSort.POPULAR,
    val pageSize: Int = 20,
)

data class CatalogPage(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val items: List<CatalogBook>,
)

data class CatalogSnapshot(
    val query: CatalogQuery,
    val pages: List<CatalogPage>,
) {
    val books: List<CatalogBook> get() = pages.flatMap { it.items }.distinctBy { it.id }
    val total: Int get() = pages.lastOrNull()?.total ?: 0
    val nextPage: Int get() = (pages.lastOrNull()?.page ?: 0) + 1
    val hasMore: Boolean get() =
        pages.lastOrNull()?.let {
            it.items.isNotEmpty() &&
                it.page.toLong() * it.pageSize < it.total
        }
            ?: false
}
