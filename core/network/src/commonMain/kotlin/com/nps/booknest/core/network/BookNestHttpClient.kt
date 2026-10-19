package com.nps.booknest.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import kotlinx.serialization.json.Json
import org.koin.dsl.module

const val BOOK_NEST_API_URL = "https://booknest-api-3jl3.onrender.com"
val apiJson = Json { ignoreUnknownKeys = true }

fun createBookNestHttpClient(engine: HttpClientEngine): HttpClient =
    HttpClient(engine) {
        expectSuccess = false
        install(HttpTimeout) {
            requestTimeoutMillis = 90_000
            socketTimeoutMillis = 90_000
        }
    }

internal expect fun createPlatformBookNestHttpClient(): HttpClient

val coreNetworkModule =
    module {
        single { createPlatformBookNestHttpClient() }
    }
