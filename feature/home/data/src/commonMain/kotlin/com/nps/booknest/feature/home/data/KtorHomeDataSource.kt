package com.nps.booknest.feature.home.data

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.network.getApi
import com.nps.booknest.feature.home.domain.HomeFeed
import com.nps.booknest.feature.home.domain.HomeRemoteDataSource
import io.ktor.client.HttpClient

class KtorHomeDataSource(
    private val client: HttpClient,
) : HomeRemoteDataSource {
    override suspend fun getHome(): Result<HomeFeed, DataError.Network> =
        client.getApi<HomeFeedDto, HomeFeed>("/api/v1/home") { it.toHomeFeed() }
}
