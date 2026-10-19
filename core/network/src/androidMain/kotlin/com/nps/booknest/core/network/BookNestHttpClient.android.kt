package com.nps.booknest.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun createPlatformBookNestHttpClient(): HttpClient = createBookNestHttpClient(OkHttp.create())
