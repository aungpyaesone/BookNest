package com.nps.booknest.feature.home.data

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
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
import kotlin.test.assertTrue

class KtorHomeDataSourceTest {
    @Test
    fun `provided response maps authors prices and relative artwork in one public request`() =
        runTest {
            var requests = 0
            val client =
                createHomeHttpClient(
                    MockEngine { request ->
                        requests++
                        assertEquals("/api/v1/home", request.url.encodedPath)
                        assertEquals(null, request.headers["Authorization"])
                        respond(homeResponseFixture)
                    },
                )
            try {
                val feed = assertIs<Result.Success<*>>(KtorHomeDataSource(client).getHome()).data
                val home = assertIs<com.nps.booknest.feature.home.domain.HomeFeed>(feed)
                assertEquals(1, requests)
                assertEquals(4, home.popularBooks.size)
                assertEquals(
                    "James Clear",
                    home.popularBooks
                        .first()
                        .authors
                        .first()
                        .name,
                )
                assertEquals(1200.0, home.popularBooks.first().purchasePrice)
                assertEquals("JPY", home.popularBooks.first().currencyCode)
                assertEquals("$HOME_API_URL/images/home/reading-banner.svg", home.banners.first().imageUrl)
                assertTrue(home.popularBooks.first().isDemo)
            } finally {
                client.close()
            }
        }

    @Test
    fun `mapping preserves Int64 identifiers and multiple authors`() {
        val dto = homeJson.decodeFromString<HomeFeedDto>(homeResponseFixture)
        val book =
            dto.popularBooks.first().copy(
                bookId = 9_007_199_254_740_993L,
                authors = listOf(HomeAuthorDto(1, "One"), HomeAuthorDto(2, "Two")),
            )
        val mapped = dto.copy(popularBooks = listOf(book)).toHomeFeed()
        assertEquals(9_007_199_254_740_993L, mapped.popularBooks.single().id)
        assertEquals(
            listOf("One", "Two"),
            mapped.popularBooks
                .single()
                .authors
                .map { it.name },
        )
    }

    @Test
    fun `problem details response maps status without decoding as home`() =
        runTest {
            val client =
                createHomeHttpClient(
                    MockEngine { respond("{\"title\":\"Unavailable\"}", HttpStatusCode.ServiceUnavailable) },
                )
            try {
                assertEquals(Result.Error(DataError.Network.SERVICE_UNAVAILABLE), KtorHomeDataSource(client).getHome())
            } finally {
                client.close()
            }
        }

    @Test
    fun `malformed or incomplete home does not become an empty success`() =
        runTest {
            for (body in listOf("invalid", "{}", homeResponseFixture.replace("1200.0", "-10.0"))) {
                val client = createHomeHttpClient(MockEngine { respond(body) })
                try {
                    assertEquals(Result.Error(DataError.Network.SERIALIZATION), KtorHomeDataSource(client).getHome())
                } finally {
                    client.close()
                }
            }
        }

    @Test
    fun `timeout is typed and cancellation propagates`() =
        runTest {
            val timedOut = createHomeHttpClient(MockEngine { request -> throw HttpRequestTimeoutException(request) })
            try {
                assertEquals(Result.Error(DataError.Network.REQUEST_TIMEOUT), KtorHomeDataSource(timedOut).getHome())
            } finally {
                timedOut.close()
            }
            val cancelled = createHomeHttpClient(MockEngine { throw CancellationException("cancel") })
            try {
                assertFailsWith<CancellationException> { KtorHomeDataSource(cancelled).getHome() }
            } finally {
                cancelled.close()
            }
        }
}
