package com.nps.booknest.feature.explore.data

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.network.BOOK_NEST_API_URL
import com.nps.booknest.core.network.apiJson
import com.nps.booknest.core.network.createBookNestHttpClient
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.CatalogSort
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KtorExploreDataSourceTest {
    @Test
    fun `catalog contract preserves long IDs authors decimals nulls and relative SVG`() =
        runTest {
            val client =
                createBookNestHttpClient(
                    MockEngine { request ->
                        assertEquals(null, request.headers["Authorization"])
                        assertEquals("/api/v1/screens/catalog", request.url.encodedPath)
                        assertEquals("1", request.url.parameters["categoryId"])
                        assertEquals("20", request.url.parameters["pageSize"])
                        assertEquals("1", request.url.parameters["page"])
                        respond(catalogResponseFixture)
                    },
                )
            try {
                val page = assertIs<Result.Success<*>>(KtorExploreDataSource(client).getCatalog(CatalogQuery(1), 1))
                val mapped = assertIs<com.nps.booknest.feature.explore.domain.CatalogPage>(page.data)
                val book = mapped.items.first()
                assertEquals(9_007_199_254_740_993L, book.id)
                assertEquals(9_007_199_254_740_994L, book.authors.first().id)
                assertEquals(listOf("One", "Two"), book.authors.map { it.name })
                assertEquals(12.75, book.purchasePrice)
                assertEquals("USD", book.currencyCode)
                assertEquals("$BOOK_NEST_API_URL/images/cover.svg", book.coverUrl)
                assertNull(book.rating)
                assertTrue(book.isDemo)
            } finally {
                client.close()
            }
        }

    @Test
    fun `all supported sorts send server values`() =
        runTest {
            val sent = mutableListOf<String?>()
            val client =
                createBookNestHttpClient(
                    MockEngine { request ->
                        sent += request.url.parameters["sort"]
                        respond(catalogResponseFixture)
                    },
                )
            try {
                for (sort in CatalogSort.entries) KtorExploreDataSource(client).getCatalog(CatalogQuery(1, sort), 1)
                assertEquals<List<String?>>(listOf("popular", "recent", "title", "price"), sent)
            } finally {
                client.close()
            }
        }

    @Test
    fun `taxonomy retains parent relationships nullable banners and unknown icon`() {
        val dto = apiJson.decodeFromString<ExploreResponseDto>(taxonomyResponseFixture)
        val changed = dto.copy(categories = dto.categories.map { it.copy(iconKey = "future-icon") })
        val categories = changed.toCategories()
        assertEquals(16, categories.count { it.parentId == null })
        assertTrue(categories.any { it.parentId != null })
        assertTrue(categories.all { it.iconKey == "future-icon" })
        assertTrue(categories.any { it.bannerUrl == null })
    }

    @Test
    fun `non JSON problem details malformed and timeout produce typed errors`() =
        runTest {
            val scenarios =
                listOf(
                    Triple("not JSON", HttpStatusCode.Unauthorized, DataError.Network.UNAUTHORIZED),
                    Triple(
                        "{\"title\":\"Unavailable\"}",
                        HttpStatusCode.ServiceUnavailable,
                        DataError.Network.SERVICE_UNAVAILABLE,
                    ),
                    Triple("{}", HttpStatusCode.OK, DataError.Network.SERIALIZATION),
                    Triple("invalid", HttpStatusCode.OK, DataError.Network.SERIALIZATION),
                )
            for ((body, status, error) in scenarios) {
                val client = createBookNestHttpClient(MockEngine { respond(body, status) })
                try {
                    assertEquals(Result.Error(error), KtorExploreDataSource(client).getCategories())
                } finally {
                    client.close()
                }
            }
            val client = createBookNestHttpClient(MockEngine { throw HttpRequestTimeoutException(it) })
            try {
                assertEquals(
                    Result.Error(DataError.Network.REQUEST_TIMEOUT),
                    KtorExploreDataSource(client).getCategories(),
                )
            } finally {
                client.close()
            }
        }

    @Test
    fun `cancellation propagates and invalid page is rejected`() =
        runTest {
            val client = createBookNestHttpClient(MockEngine { throw CancellationException("cancelled") })
            try {
                assertFailsWith<CancellationException> { KtorExploreDataSource(client).getCategories() }
            } finally {
                client.close()
            }
            val dto = apiJson.decodeFromString<CatalogPageDto>(catalogResponseFixture)
            assertFailsWith<IllegalArgumentException> { dto.toCatalogPage(CatalogQuery(1), 2) }
            val nullCover = dto.items.first().let { it.copy(book = it.book.copy(coverImageUrl = null)) }
            assertNull(
                dto
                    .copy(items = listOf(nullCover))
                    .toCatalogPage(CatalogQuery(1), 1)
                    .items
                    .single()
                    .coverUrl,
            )
        }
}
