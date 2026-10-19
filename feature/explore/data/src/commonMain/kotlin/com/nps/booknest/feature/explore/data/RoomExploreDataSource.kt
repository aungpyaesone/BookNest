package com.nps.booknest.feature.explore.data

import com.nps.booknest.core.database.explore.CatalogSnapshotEntity
import com.nps.booknest.core.database.explore.ExploreCacheDao
import com.nps.booknest.core.database.explore.ExploreTaxonomyEntity
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.network.apiJson
import com.nps.booknest.feature.explore.domain.CatalogQuery
import com.nps.booknest.feature.explore.domain.CatalogSnapshot
import com.nps.booknest.feature.explore.domain.ExploreCategory
import com.nps.booknest.feature.explore.domain.ExploreLocalDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString

class RoomExploreDataSource(
    private val dao: ExploreCacheDao,
) : ExploreLocalDataSource {
    override fun observeCategories(): Flow<Result<List<ExploreCategory>?, DataError>> =
        dao
            .observeCategories()
            .map { entity ->
                decodeSnapshot {
                    entity?.let {
                        apiJson
                            .decodeFromString<ExploreResponseDto>(
                                it.payload,
                            ).toCategories()
                    }
                }
            }.catch { exception ->
                if (exception is CancellationException) throw exception
                emit(Result.Error(DataError.Local.UNKNOWN))
            }

    override fun observeCatalog(query: CatalogQuery): Flow<Result<CatalogSnapshot?, DataError>> =
        dao
            .observeCatalog(query.cacheKey())
            .map { entity ->
                decodeSnapshot {
                    entity?.let {
                        apiJson
                            .decodeFromString<CatalogCacheDto>(
                                it.payload,
                            ).toSnapshot(query)
                    }
                }
            }.catch { exception ->
                if (exception is CancellationException) throw exception
                emit(Result.Error(DataError.Local.UNKNOWN))
            }

    override suspend fun getCatalog(query: CatalogQuery): Result<CatalogSnapshot?, DataError> =
        try {
            val entity = dao.getCatalog(query.cacheKey())
            decodeSnapshot { entity?.let { apiJson.decodeFromString<CatalogCacheDto>(it.payload).toSnapshot(query) } }
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun saveCategories(categories: List<ExploreCategory>): EmptyResult<DataError.Local> =
        saveSnapshot {
            dao.upsertCategories(ExploreTaxonomyEntity(payload = apiJson.encodeToString(categories.toExploreDto())))
        }

    override suspend fun saveCatalog(snapshot: CatalogSnapshot): EmptyResult<DataError.Local> =
        saveSnapshot {
            dao.upsertCatalog(
                CatalogSnapshotEntity(snapshot.query.cacheKey(), apiJson.encodeToString(snapshot.toCacheDto())),
            )
        }
}

private inline fun <T> decodeSnapshot(decode: () -> T): Result<T, DataError> =
    try {
        Result.Success(decode())
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: Exception) {
        Result.Error(DataError.Network.SERIALIZATION)
    }

private suspend fun saveSnapshot(save: suspend () -> Unit): EmptyResult<DataError.Local> =
    try {
        save()
        Result.Success(Unit)
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: Exception) {
        Result.Error(DataError.Local.UNKNOWN)
    }
