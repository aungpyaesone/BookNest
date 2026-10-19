package com.nps.booknest.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

internal actual fun createPlatformBookNestHttpClient(): HttpClient = createBookNestHttpClient(Darwin.create())
