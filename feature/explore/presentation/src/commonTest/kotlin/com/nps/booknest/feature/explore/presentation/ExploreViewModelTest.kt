package com.nps.booknest.feature.explore.presentation

import app.cash.turbine.test
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {
    @Test
    fun `root search excludes children and refreshes once`() =
        exploreTest {
            val repository = FakeExploreRepository()
            val viewModel = ExploreViewModel(repository)
            runCurrent()
            assertEquals(
                listOf(1L, 3L),
                viewModel.state.value.categories
                    .map { it.id },
            )
            assertEquals(1, repository.taxonomyRefreshes)
            viewModel.onAction(ExploreAction.OnSearchChange(" FICTION "))
            assertEquals(
                listOf(1L),
                viewModel.state.value.categories
                    .map { it.id },
            )
            viewModel.onAction(ExploreAction.OnSearchChange("Fantasy"))
            assertTrue(
                viewModel.state.value.categories
                    .isEmpty(),
            )
        }

    @Test
    fun `only valid roots navigate using serialized numeric IDs`() =
        exploreTest {
            val viewModel = ExploreViewModel(FakeExploreRepository())
            runCurrent()
            viewModel.events.test {
                viewModel.onAction(ExploreAction.OnCategoryClick(1))
                runCurrent()
                assertEquals(ExploreEvent.NavigateToCategory("1"), awaitItem())
                viewModel.onAction(ExploreAction.OnCategoryClick(2))
                viewModel.onAction(ExploreAction.OnCategoryClick(99))
                runCurrent()
                expectNoEvents()
            }
        }

    @Test
    fun `offline refresh retains cached roots and retry clears error`() =
        exploreTest {
            val repository =
                FakeExploreRepository().apply {
                    refreshResult = Result.Error(DataError.Network.NO_INTERNET)
                }
            val viewModel = ExploreViewModel(repository)
            runCurrent()
            assertTrue(viewModel.state.value.isShowingCachedData)
            assertEquals(2, viewModel.state.value.categories.size)
            assertNotNull(viewModel.state.value.error)
            repository.refreshResult = Result.Success(Unit)
            viewModel.onAction(ExploreAction.OnRefreshClick)
            runCurrent()
            assertFalse(viewModel.state.value.isShowingCachedData)
            assertEquals(null, viewModel.state.value.error)
        }

    @Test
    fun `valid empty taxonomy is loaded and corrupt cache can recover`() =
        exploreTest {
            val repository = FakeExploreRepository()
            repository.categories.value = Result.Error(DataError.Network.SERIALIZATION)
            val viewModel = ExploreViewModel(repository)
            runCurrent()
            repository.categories.value = Result.Success(emptyList())
            runCurrent()
            assertTrue(viewModel.state.value.hasLoaded)
            assertTrue(
                viewModel.state.value.categories
                    .isEmpty(),
            )
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal fun exploreTest(block: suspend TestScope.() -> Unit) =
    runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }
