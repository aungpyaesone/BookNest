@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.explore.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.feature.explore.domain.CatalogSort
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class CategoryQueryLoadingTest(
    private val widthDp: Int,
) {
    @Test
    fun filterAndSortKeepHeadersAndCenterLoadingOnlyBelowControls() =
        runComposeUiTest {
            val robot = CategoryQueryLoadingRobot(this, widthDp).setContent()
            val hero = onNodeWithTag("category_hero").getUnclippedBoundsInRoot()
            val sort = onNodeWithTag("category_sort_action").getUnclippedBoundsInRoot()
            onNodeWithTag("category_chip_2").performClick().assertIsSelected()
            onNodeWithTag("category_hero").assertIsDisplayed()
            onNodeWithTag("category_subcategories").assertIsDisplayed()
            onNodeWithTag("category_books_count").assertDoesNotExist()
            onNodeWithTag("explore_book_10").assertDoesNotExist()
            val status = onNodeWithTag("category_results_status").getUnclippedBoundsInRoot()
            val spinner = onNodeWithTag("explore_loading").getUnclippedBoundsInRoot()
            assertTrue(status.top.value >= sort.bottom.value)
            assertEquals(
                (status.left.value + status.right.value) / 2,
                (spinner.left.value + spinner.right.value) / 2,
                1f,
            )
            assertEquals(
                (status.top.value + status.bottom.value) / 2,
                (spinner.top.value + spinner.bottom.value) / 2,
                1f,
            )
            assertEquals(hero, onNodeWithTag("category_hero").getUnclippedBoundsInRoot())
            assertEquals(sort, onNodeWithTag("category_sort_action").getUnclippedBoundsInRoot())
            onNodeWithTag("category_sort_action").performClick()
            onNodeWithText("Price: Low to High").performClick()
            onNodeWithTag("category_hero").assertIsDisplayed()
            onNodeWithTag("category_results_status").assertIsDisplayed()
            runOnIdle { assertEquals(CatalogSort.PRICE, robot.state.sort) }
            runOnIdle { robot.showEmptyResult() }
            onNodeWithTag("category_results_status").assertDoesNotExist()
            onNodeWithTag("explore_loading").assertDoesNotExist()
            onNodeWithTag("explore_empty").assertIsDisplayed()
            onNodeWithText("0 Books").assertIsDisplayed()
            assertEquals(
                sort.right.value,
                onNodeWithTag("category_sort_action").getUnclippedBoundsInRoot().right.value,
                1f,
            )
        }

    @Test
    fun failedNewQueryOffersLocalRetryAndControlsRemainAvailable() =
        runComposeUiTest {
            val robot = CategoryQueryLoadingRobot(this, widthDp).setContent()
            onNodeWithTag("category_chip_2").performClick()
            runOnIdle { robot.showError() }
            onNodeWithTag("category_hero").assertIsDisplayed()
            onNodeWithTag("category_books_count").assertDoesNotExist()
            val retry = onNodeWithTag("explore_refresh").getUnclippedBoundsInRoot()
            val sort = onNodeWithTag("category_sort_action").getUnclippedBoundsInRoot()
            assertTrue(retry.top.value > sort.bottom.value)
            onNodeWithTag("explore_refresh").performClick().assertIsNotEnabled()
            onNodeWithTag("category_sort_action").performClick()
            onNodeWithText("Recently Added").performClick()
            onNodeWithTag("explore_error").assertDoesNotExist()
            onNodeWithTag("category_results_status").assertIsDisplayed()
            runOnIdle {
                assertEquals(1, robot.retries)
                assertEquals(CatalogSort.RECENT, robot.state.sort)
            }
        }

    @Test
    fun cachedQueryShowsItsBooksWithoutLoadingOrOldResults() =
        runComposeUiTest {
            val robot = CategoryQueryLoadingRobot(this, widthDp).setContent()
            onNodeWithTag("category_chip_3").performClick()
            onNodeWithTag("explore_book_20").assertIsDisplayed()
            onNodeWithTag("explore_book_10").assertDoesNotExist()
            onNodeWithTag("category_results_status").assertDoesNotExist()
            onNodeWithTag("explore_loading").assertDoesNotExist()
            onNodeWithTag("explore_refresh").assertDoesNotExist()
            onNodeWithText("12 Books").assertIsDisplayed()
            runOnIdle { robot.showError() }
            onNodeWithTag("explore_book_20").assertIsDisplayed()
            onNodeWithTag("category_hero").assertIsDisplayed()
            onNodeWithTag("explore_refresh").assertIsDisplayed()
        }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}")
        fun widths(): List<Array<Any>> = listOf(arrayOf(384), arrayOf(900))
    }
}

private class CategoryQueryLoadingRobot(
    private val test: ComposeUiTest,
    private val widthDp: Int,
) {
    var state by mutableStateOf(
        categoryBooksPreviewState().copy(
            subcategories =
                listOf(
                    ExploreCategoryUi(2, "Fantasy", "book-open"),
                    ExploreCategoryUi(3, "Romance", "book-open"),
                ),
        ),
    )
        private set
    var retries = 0
        private set

    fun setContent() =
        apply {
            test.setContent {
                val density =
                    LocalWindowInfo.current.containerSize.width
                        .toFloat() / widthDp
                CompositionLocalProvider(LocalDensity provides Density(density, if (widthDp >= 600) 1.5f else 1f)) {
                    BookNestTheme { CategoryBooksScreen(state, ::onAction) }
                }
            }
        }

    fun showEmptyResult() {
        state = state.copy(hasLoaded = true, isLoading = false, isRefreshing = false, books = emptyList(), total = 0)
    }

    fun showError() {
        state = state.copy(isLoading = false, isRefreshing = false, error = UiText.DynamicString("Offline"))
    }

    private fun startQuery() {
        state =
            state.copy(
                hasLoaded = false,
                books = emptyList(),
                total = 0,
                isLoading = true,
                isRefreshing = true,
                error = null,
                queryVersion = state.queryVersion + 1,
            )
    }

    private fun onAction(action: CategoryBooksAction) {
        when (action) {
            is CategoryBooksAction.OnSubcategoryClick -> {
                state = state.copy(selectedSubcategoryId = action.id)
                startQuery()
                if (action.id == 3L) {
                    state =
                        state.copy(
                            hasLoaded = true,
                            isLoading = false,
                            total = 12,
                            books = listOf(ExploreBookUi(20, "Cached Romance", "Author", "¥1,200", null)),
                        )
                }
            }

            CategoryBooksAction.OnSortClick -> {
                state = state.copy(isSortVisible = true)
            }

            is CategoryBooksAction.OnSortSelect -> {
                state = state.copy(sort = action.sort, isSortVisible = false)
                startQuery()
            }

            CategoryBooksAction.OnRefreshClick -> {
                retries++
                state = state.copy(isLoading = !state.hasLoaded, isRefreshing = true)
            }

            else -> {
                Unit
            }
        }
    }
}
