package com.nps.booknest.feature.home.data

import com.nps.booknest.core.network.apiJson
import com.nps.booknest.core.network.createBookNestHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine

internal val homeJson = apiJson

internal fun createHomeHttpClient(engine: HttpClientEngine): HttpClient = createBookNestHttpClient(engine)
