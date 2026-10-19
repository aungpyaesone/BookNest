package com.nps.booknest.feature.home.data

import com.nps.booknest.core.database.home.HomeFeedDao
import com.nps.booknest.core.database.home.HomeFeedEntity
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.home.domain.HomeFeed
import com.nps.booknest.feature.home.domain.HomeLocalDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString

class RoomHomeDataSource(
    private val dao: HomeFeedDao,
) : HomeLocalDataSource {
    override fun observeHome(): Flow<Result<HomeFeed?, DataError>> =
        dao
            .observeHome()
            .map<HomeFeedEntity?, Result<HomeFeed?, DataError>> { entity ->
                // Decode inside map so one corrupt snapshot does not stop observation of later repairs.
                try {
                    val feed = entity?.let { homeJson.decodeFromString<HomeFeedDto>(it.payload).toHomeFeed() }
                    Result.Success(feed)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    Result.Error(DataError.Network.SERIALIZATION)
                }
            }.catch { exception ->
                if (exception is CancellationException) throw exception
                emit(Result.Error(DataError.Local.UNKNOWN))
            }

    override suspend fun saveHome(feed: HomeFeed): EmptyResult<DataError.Local> =
        try {
            dao.upsertHome(HomeFeedEntity(payload = homeJson.encodeToString(feed.toHomeFeedDto())))
            Result.Success(Unit)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
