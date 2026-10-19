package com.nps.booknest.feature.home.presentation

import app.cash.turbine.test
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.home.domain.HomeAuthor
import com.nps.booknest.feature.home.domain.HomeBanner
import com.nps.booknest.feature.home.domain.HomeBook
import com.nps.booknest.feature.home.domain.HomeBookMetadata
import com.nps.booknest.feature.home.domain.HomeCategory
import com.nps.booknest.feature.home.domain.HomeFeed
import com.nps.booknest.feature.home.domain.HomeRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Test
    fun `more category and category see all open explore`() =
        runHomeTest {
            val viewModel = HomeViewModel(FakeHomeRepository())
            runCurrent()
            viewModel.events.test {
                viewModel.onAction(HomeAction.OnCategoryClick(homeMoreCategory))
                viewModel.onAction(HomeAction.OnCategorySeeAllClick)
                runCurrent()
                repeat(2) { assertEquals(HomeEvent.NavigateToExplore, awaitItem()) }
            }
        }

    @Test
    fun `first load shows loading then mapped API feed`() =
        runHomeTest {
            val repository = FakeHomeRepository()
            val viewModel = HomeViewModel(repository)
            assertTrue(viewModel.state.value.isLoading)
            runCurrent()
            assertEquals(1, repository.refreshCalls)
            assertEquals(
                listOf("Atomic Habits", "Money"),
                viewModel.state.value.books
                    .map { it.title },
            )
            assertEquals(
                "James Clear, Another Author",
                viewModel.state.value.books
                    .first()
                    .author,
            )
            assertEquals(3, viewModel.state.value.categories.size)
            assertTrue(viewModel.state.value.hasLoadedFeed)
            assertFalse(viewModel.state.value.isLoading)
            assertFalse(viewModel.state.value.isShowingCachedData)
        }

    @Test
    fun `search filters loaded titles and all authors without another request`() =
        runHomeTest {
            val repository = FakeHomeRepository()
            val viewModel = HomeViewModel(repository)
            runCurrent()
            viewModel.onAction(HomeAction.OnSearchQueryChange("another"))
            assertEquals(
                listOf("1"),
                viewModel.state.value.visibleBooks
                    .map { it.id },
            )
            viewModel.onAction(HomeAction.OnSearchQueryChange("money"))
            assertEquals(
                listOf("2"),
                viewModel.state.value.visibleBooks
                    .map { it.id },
            )
            assertEquals(1, repository.refreshCalls)
        }

    @Test
    fun `price sort and rental filter apply to loaded popular books`() =
        runHomeTest {
            val viewModel = HomeViewModel(FakeHomeRepository())
            runCurrent()
            viewModel.onAction(HomeAction.OnSortOrderSelect(HomeSortOrder.PRICE_LOW_TO_HIGH))
            assertEquals(
                listOf(800.0, 1200.0),
                viewModel.state.value.visibleBooks
                    .map { it.priceAmount },
            )
            viewModel.onAction(HomeAction.OnRentalOnlyChange(true))
            assertEquals(
                listOf("1"),
                viewModel.state.value.visibleBooks
                    .map { it.id },
            )
        }

    @Test
    fun `category click reports coming soon without filtering books`() =
        runHomeTest {
            val viewModel = HomeViewModel(FakeHomeRepository())
            runCurrent()
            viewModel.events.test {
                viewModel.onAction(HomeAction.OnCategoryClick(viewModel.state.value.categories[1]))
                runCurrent()
                assertIs<HomeEvent.ShowSnackbar>(awaitItem())
                assertEquals(2, viewModel.state.value.visibleBooks.size)
                viewModel.onAction(HomeAction.OnCategoryClick(homeAllCategory))
                expectNoEvents()
            }
        }

    @Test
    fun `offline launch retains persisted feed and exposes retry error`() =
        runHomeTest {
            val repository =
                FakeHomeRepository(homeFeedFixture()).apply {
                    refreshResult =
                        Result.Error(DataError.Network.NO_INTERNET)
                }
            val viewModel = HomeViewModel(repository)
            runCurrent()
            assertTrue(viewModel.state.value.hasLoadedFeed)
            assertTrue(viewModel.state.value.isShowingCachedData)
            assertIs<UiText.Resource>(viewModel.state.value.error)
            assertEquals(2, viewModel.state.value.books.size)
            repository.refreshResult = Result.Success(Unit)
            viewModel.onAction(HomeAction.OnRefreshClick)
            runCurrent()
            assertFalse(viewModel.state.value.isShowingCachedData)
            assertEquals(null, viewModel.state.value.error)
        }

    @Test
    fun `failed first load offers error and retry can load empty feed`() =
        runHomeTest {
            val repository =
                FakeHomeRepository().apply {
                    refreshResult =
                        Result.Error(DataError.Network.REQUEST_TIMEOUT)
                }
            val viewModel = HomeViewModel(repository)
            runCurrent()
            assertFalse(viewModel.state.value.hasLoadedFeed)
            assertFalse(viewModel.state.value.isLoading)
            assertIs<UiText.Resource>(viewModel.state.value.error)
            repository.feed = HomeFeed(emptyList(), emptyList(), emptyList())
            repository.refreshResult = Result.Success(Unit)
            viewModel.onAction(HomeAction.OnRefreshClick)
            runCurrent()
            assertTrue(viewModel.state.value.hasLoadedFeed)
            assertTrue(
                viewModel.state.value.books
                    .isEmpty(),
            )
            assertEquals(0, viewModel.state.value.currentHeroPage)
        }

    @Test
    fun `refresh preserves filters clamps hero page and prevents duplicate requests`() =
        runHomeTest {
            val repository = FakeHomeRepository()
            val viewModel = HomeViewModel(repository)
            runCurrent()
            viewModel.onAction(HomeAction.OnHeroPageChange(2))
            viewModel.onAction(HomeAction.OnSearchQueryChange("money"))
            repository.feed = homeFeedFixture().copy(banners = homeFeedFixture().banners.take(1))
            repository.gate = CompletableDeferred()
            viewModel.onAction(HomeAction.OnRefreshClick)
            viewModel.onAction(HomeAction.OnRefreshClick)
            runCurrent()
            assertTrue(viewModel.state.value.isRefreshing)
            assertEquals(2, repository.refreshCalls)
            repository.gate?.complete(Unit)
            runCurrent()
            assertEquals(0, viewModel.state.value.currentHeroPage)
            assertEquals(
                listOf("2"),
                viewModel.state.value.visibleBooks
                    .map { it.id },
            )
        }

    @Test
    fun `banner actions and existing navigation emit expected events`() =
        runHomeTest {
            val viewModel = HomeViewModel(FakeHomeRepository())
            runCurrent()
            viewModel.events.test {
                viewModel.onAction(HomeAction.OnBannerClick("banner-0"))
                viewModel.onAction(HomeAction.OnSettingsClick)
                viewModel.onAction(HomeAction.OnReadNowClick("mountain-morning"))
                viewModel.onAction(HomeAction.OnExploreClick)
                runCurrent()
                assertEquals(HomeEvent.NavigateToExplore, awaitItem())
                assertEquals(HomeEvent.NavigateToSettings, awaitItem())
                assertEquals(HomeEvent.NavigateToReader("mountain-morning"), awaitItem())
                assertEquals(HomeEvent.NavigateToExplore, awaitItem())
                viewModel.onAction(HomeAction.OnBannerClick("missing"))
                viewModel.onAction(HomeAction.OnBuyClick("1"))
                viewModel.onAction(HomeAction.OnRentClick("1"))
                runCurrent()
                repeat(3) { assertIs<HomeEvent.ShowSnackbar>(awaitItem()) }
            }
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun runHomeTest(block: suspend TestScope.() -> Unit) =
    runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }

private class FakeHomeRepository(
    cachedFeed: HomeFeed? = null,
) : HomeRepository {
    val cached = MutableStateFlow<Result<HomeFeed?, DataError>>(Result.Success(cachedFeed))
    var feed = homeFeedFixture()
    var refreshResult: EmptyResult<DataError> = Result.Success(Unit)
    var gate: CompletableDeferred<Unit>? = null
    var refreshCalls = 0

    override fun observeHome() = cached

    override suspend fun refreshHome(): EmptyResult<DataError> {
        refreshCalls++
        gate?.await()
        if (refreshResult is Result.Success) cached.value = Result.Success(feed)
        return refreshResult
    }
}

private fun homeFeedFixture() =
    HomeFeed(
        banners = List(3) { HomeBanner("banner-$it", "Title", "Body", "", "Explore", "OPEN_EXPLORE") },
        categories = listOf(HomeCategory(4, "Self-help", "self-help", "sprout")),
        popularBooks =
            listOf(
                HomeBook(
                    1,
                    "Atomic Habits",
                    "",
                    listOf(HomeAuthor(1, "James Clear"), HomeAuthor(2, "Another Author")),
                    1200.0,
                    300.0,
                    "JPY",
                    30,
                    true,
                    HomeBookMetadata(100, "en", "PDF", false),
                ),
                HomeBook(
                    2,
                    "Money",
                    "",
                    emptyList(),
                    800.0,
                    null,
                    "JPY",
                    null,
                    true,
                    HomeBookMetadata(100, "en", "PDF", false),
                ),
            ),
    )
