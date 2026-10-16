package com.nps.booknest.feature.reader.data

import com.nps.booknest.core.database.reading.ReadingProgressDao
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.reader.domain.ReadingProgress
import com.nps.booknest.feature.reader.domain.ReadingProgressLocalDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class RoomReadingProgressDataSource(
    private val readingProgressDao: ReadingProgressDao,
) : ReadingProgressLocalDataSource {
    override fun observeProgress(bookId: String): Flow<Result<ReadingProgress?, DataError.Local>> =
        readingProgressDao
            .observeProgress(bookId)
            .map { entity ->
                val result: Result<ReadingProgress?, DataError.Local> =
                    Result.Success(entity?.toReadingProgress())
                result
            }.catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(Result.Error(DataError.Local.UNKNOWN))
            }

    // Room exposes different database exception types per platform, so the common boundary maps them to one typed local error.
    @Suppress("TooGenericExceptionCaught")
    override suspend fun saveProgress(progress: ReadingProgress): EmptyResult<DataError.Local> =
        try {
            readingProgressDao.upsertProgress(progress.toEntity())
            Result.Success(Unit)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
