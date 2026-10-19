package com.nps.booknest.core.network

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

suspend inline fun <reified T, R> HttpClient.getApi(
    path: String,
    parameters: Map<String, String> = emptyMap(),
    transform: (T) -> R,
): Result<R, DataError.Network> =
    try {
        val response =
            get("$BOOK_NEST_API_URL$path") {
                parameters.forEach { (key, value) -> parameter(key, value) }
            }
        // Problem Details and empty/non-JSON failures use HTTP status, never success DTO decoding.
        if (response.status.value in 200..299) {
            Result.Success(transform(apiJson.decodeFromString<T>(response.bodyAsText())))
        } else {
            Result.Error(response.status.value.toApiNetworkError())
        }
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: HttpRequestTimeoutException) {
        Result.Error(DataError.Network.REQUEST_TIMEOUT)
    } catch (_: ConnectTimeoutException) {
        Result.Error(DataError.Network.REQUEST_TIMEOUT)
    } catch (_: SocketTimeoutException) {
        Result.Error(DataError.Network.REQUEST_TIMEOUT)
    } catch (_: SerializationException) {
        Result.Error(DataError.Network.SERIALIZATION)
    } catch (_: IllegalArgumentException) {
        Result.Error(DataError.Network.SERIALIZATION)
    } catch (_: IOException) {
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (_: Exception) {
        Result.Error(DataError.Network.UNKNOWN)
    }

fun Int.toApiNetworkError(): DataError.Network =
    when (this) {
        400 -> DataError.Network.BAD_REQUEST
        401 -> DataError.Network.UNAUTHORIZED
        403 -> DataError.Network.FORBIDDEN
        404 -> DataError.Network.NOT_FOUND
        408 -> DataError.Network.REQUEST_TIMEOUT
        409 -> DataError.Network.CONFLICT
        413 -> DataError.Network.PAYLOAD_TOO_LARGE
        429 -> DataError.Network.TOO_MANY_REQUESTS
        503 -> DataError.Network.SERVICE_UNAVAILABLE
        in 500..599 -> DataError.Network.SERVER_ERROR
        else -> DataError.Network.UNKNOWN
    }

fun resolveApiImageUrl(
    imageUrl: String?,
    baseUrl: String = BOOK_NEST_API_URL,
): String? {
    if (imageUrl.isNullOrBlank()) return null
    val url = URLBuilder(baseUrl).apply { takeFrom(imageUrl) }.build()
    require(url.protocol.name == "https")
    return url.toString()
}
