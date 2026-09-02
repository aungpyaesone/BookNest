package com.nps.booknest.feature.home.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.nps.booknest.core.presentation.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Test
    fun `search query filters books by title`() {
        val viewModel = HomeViewModel()

        viewModel.onAction(HomeAction.OnSearchQueryChange("ငွေကြေး"))

        assertThat(
            viewModel.state.value.visibleBooks
                .map(HomeBookUi::id),
        ).containsExactly("mindful-money")
    }

    @Test
    fun `category and rental filters keep matching rentable books`() {
        val viewModel = HomeViewModel()

        viewModel.onAction(HomeAction.OnCategoryClick(HomeCategory.SELF_HELP))
        viewModel.onAction(HomeAction.OnRentalOnlyChange(true))

        assertThat(
            viewModel.state.value.visibleBooks
                .map(HomeBookUi::id),
        ).containsExactly("brave-thoughts")
    }

    @Test
    fun `price sorting orders books from low to high`() {
        val viewModel = HomeViewModel()

        viewModel.onAction(HomeAction.OnSortOrderSelect(HomeSortOrder.PRICE_LOW_TO_HIGH))

        assertThat(
            viewModel.state.value.visibleBooks
                .map(HomeBookUi::priceAmount),
        ).containsExactly(8_000, 9_000, 10_000, 12_000)
    }

    @Test
    fun `explore click emits navigation event`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val viewModel = HomeViewModel()
                viewModel.events.test {
                    viewModel.onAction(HomeAction.OnExploreClick)
                    runCurrent()

                    assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToExplore)
                }
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `settings click emits navigation event`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val viewModel = HomeViewModel()
                viewModel.events.test {
                    viewModel.onAction(HomeAction.OnSettingsClick)
                    runCurrent()

                    assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToSettings)
                }
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `buy click emits localized coming soon message`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val viewModel = HomeViewModel()
                viewModel.events.test {
                    viewModel.onAction(HomeAction.OnBuyClick("quiet-pages"))
                    runCurrent()

                    val event = awaitItem()
                    assertThat(event).isInstanceOf(HomeEvent.ShowSnackbar::class)
                    assertThat((event as HomeEvent.ShowSnackbar).message).isInstanceOf(UiText.Resource::class)
                }
            } finally {
                Dispatchers.resetMain()
            }
        }
}
