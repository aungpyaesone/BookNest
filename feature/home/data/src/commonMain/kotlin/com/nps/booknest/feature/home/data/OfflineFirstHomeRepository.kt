package com.nps.booknest.feature.home.data

import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.home.domain.HomeFeed
import com.nps.booknest.feature.home.domain.HomeLocalDataSource
import com.nps.booknest.feature.home.domain.HomeRemoteDataSource
import com.nps.booknest.feature.home.domain.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OfflineFirstHomeRepository(
    private val remote: HomeRemoteDataSource,
    private val local: HomeLocalDataSource,
) : HomeRepository {
    private val refreshMutex = Mutex()
    private var lastRefreshResult: EmptyResult<DataError> = Result.Success(Unit)

    override fun observeHome(): Flow<Result<HomeFeed?, DataError>> = local.observeHome()

    override suspend fun refreshHome(): EmptyResult<DataError> {
        if (!refreshMutex.tryLock()) return refreshMutex.withLock { lastRefreshResult }
        return try {
            when (val result = remote.getHome()) {
                is Result.Error -> result
                is Result.Success -> local.saveHome(result.data)
            }.also { lastRefreshResult = it }
        } finally {
            refreshMutex.unlock()
        }
    }
}
