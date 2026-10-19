package com.nps.booknest.feature.home.domain

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeHome(): Flow<Result<HomeFeed?, DataError>>

    suspend fun refreshHome(): EmptyResult<DataError>
}

interface HomeRemoteDataSource {
    suspend fun getHome(): Result<HomeFeed, DataError.Network>
}

interface HomeLocalDataSource {
    fun observeHome(): Flow<Result<HomeFeed?, DataError>>

    suspend fun saveHome(feed: HomeFeed): EmptyResult<DataError.Local>
}
