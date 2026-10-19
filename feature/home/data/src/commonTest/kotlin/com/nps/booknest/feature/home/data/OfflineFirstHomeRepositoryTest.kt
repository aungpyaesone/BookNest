package com.nps.booknest.feature.home.data

import app.cash.turbine.test
import com.nps.booknest.core.database.home.HomeFeedDao
import com.nps.booknest.core.database.home.HomeFeedEntity
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.home.domain.HomeFeed
import com.nps.booknest.feature.home.domain.HomeRemoteDataSource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstHomeRepositoryTest {
    @Test
    fun `new repository reads last snapshot after restart and keeps it when offline`() =
        runTest {
            val dao = FakeHomeFeedDao()
            val remote = FakeHomeRemoteDataSource()
            val repository = OfflineFirstHomeRepository(remote, RoomHomeDataSource(dao))
            assertEquals(Result.Success(null), repository.observeHome().first())
            assertEquals(Result.Success(Unit), repository.refreshHome())
            val saved = repository.observeHome().first()
            remote.result = Result.Error(DataError.Network.NO_INTERNET)
            val restarted = OfflineFirstHomeRepository(remote, RoomHomeDataSource(dao))
            assertEquals(saved, restarted.observeHome().first())
            assertEquals(Result.Error(DataError.Network.NO_INTERNET), restarted.refreshHome())
            assertEquals(saved, restarted.observeHome().first())
        }

    @Test
    fun `successful empty feed replaces old snapshot`() =
        runTest {
            val dao = FakeHomeFeedDao()
            val remote = FakeHomeRemoteDataSource()
            val repository = OfflineFirstHomeRepository(remote, RoomHomeDataSource(dao))
            repository.refreshHome()
            remote.result = Result.Success(HomeFeed(emptyList(), emptyList(), emptyList()))
            repository.refreshHome()
            assertEquals(remote.result, repository.observeHome().first())
        }

    @Test
    fun `corrupt cache observation recovers after successful refresh`() =
        runTest {
            val dao = FakeHomeFeedDao().apply { snapshot.value = HomeFeedEntity(payload = "broken") }
            val repository = OfflineFirstHomeRepository(FakeHomeRemoteDataSource(), RoomHomeDataSource(dao))
            repository.observeHome().test {
                assertIs<Result.Error<*>>(awaitItem())
                repository.refreshHome()
                assertIs<Result.Success<*>>(awaitItem())
            }
        }

    @Test
    fun `cache write failure preserves old snapshot`() =
        runTest {
            val dao = FakeHomeFeedDao()
            val remote = FakeHomeRemoteDataSource()
            val repository = OfflineFirstHomeRepository(remote, RoomHomeDataSource(dao))
            repository.refreshHome()
            val old = dao.snapshot.value
            dao.shouldFailWrite = true
            remote.result = Result.Success(HomeFeed(emptyList(), emptyList(), emptyList()))
            assertEquals(Result.Error(DataError.Local.UNKNOWN), repository.refreshHome())
            assertEquals(old, dao.snapshot.value)
        }

    @Test
    fun `simultaneous refreshes make one request`() =
        runTest {
            val remote = FakeHomeRemoteDataSource().apply { gate = CompletableDeferred() }
            val repository = OfflineFirstHomeRepository(remote, RoomHomeDataSource(FakeHomeFeedDao()))
            val first = async { repository.refreshHome() }
            runCurrent()
            val second = async { repository.refreshHome() }
            runCurrent()
            remote.gate?.complete(Unit)
            first.await()
            second.await()
            assertEquals(1, remote.calls)
        }
}

private class FakeHomeFeedDao : HomeFeedDao {
    val snapshot = MutableStateFlow<HomeFeedEntity?>(null)
    var shouldFailWrite = false

    override fun observeHome() = snapshot

    override suspend fun upsertHome(feed: HomeFeedEntity) {
        check(!shouldFailWrite) { "Disk write failed" }
        snapshot.value = feed
    }
}

private class FakeHomeRemoteDataSource : HomeRemoteDataSource {
    var result: Result<HomeFeed, DataError.Network> =
        Result.Success(
            homeJson.decodeFromString<HomeFeedDto>(homeResponseFixture).toHomeFeed(),
        )
    var gate: CompletableDeferred<Unit>? = null
    var calls = 0

    override suspend fun getHome(): Result<HomeFeed, DataError.Network> {
        calls++
        gate?.await()
        return result
    }
}
