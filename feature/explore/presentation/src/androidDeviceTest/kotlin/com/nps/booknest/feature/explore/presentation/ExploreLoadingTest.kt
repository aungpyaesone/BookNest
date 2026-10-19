@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.explore.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.presentation.UiText
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ExploreLoadingTest(
    private val isDetail: Boolean,
    private val widthDp: Int,
) {
    @Test
    fun initialSpinnerIsCenteredAndDetailHasNoPrematureContent() =
        runComposeUiTest {
            ExploreLoadingRobot(this, isDetail, widthDp).setContent()
            val content = onNodeWithTag("explore_initial_status").getUnclippedBoundsInRoot()
            val spinner = onNodeWithTag("explore_loading").getUnclippedBoundsInRoot()
            assertEquals(
                (content.left.value + content.right.value) / 2,
                (spinner.left.value + spinner.right.value) / 2,
                1f,
            )
            assertEquals(
                (content.top.value + content.bottom.value) / 2,
                (spinner.top.value + spinner.bottom.value) / 2,
                1f,
            )
            onNodeWithTag("explore_refresh").assertDoesNotExist()
            onNodeWithTag("category_sort_action").assertDoesNotExist()
            onNodeWithText("Discover books in this category.").assertDoesNotExist()
        }

    @Test
    fun loadedEmptyAndCachedRefreshingContentHaveNoSpinnerOrRefresh() =
        runComposeUiTest {
            val robot = ExploreLoadingRobot(this, isDetail, widthDp).setContent()
            for (isEmpty in listOf(false, true)) {
                for (isRefreshing in listOf(false, true)) {
                    runOnIdle { robot.showLoaded(isEmpty, isRefreshing) }
                    onNodeWithTag("explore_loading").assertDoesNotExist()
                    onNodeWithTag("explore_refresh").assertDoesNotExist()
                    onNodeWithTag("explore_initial_status").assertDoesNotExist()
                    if (isEmpty) {
                        onNodeWithTag("explore_empty").assertIsDisplayed()
                    } else {
                        robot.assertLoadedContent()
                    }
                }
            }
        }

    @Test
    fun initialFailureIsCenteredAndRetryCannotBeRepeatedWhileInFlight() =
        runComposeUiTest {
            val robot = ExploreLoadingRobot(this, isDetail, widthDp).setContent()
            runOnIdle { robot.showError(hasCache = false) }
            val content = onNodeWithTag("explore_initial_status").getUnclippedBoundsInRoot()
            val error = onNodeWithTag("explore_error").getUnclippedBoundsInRoot()
            val retry = onNodeWithTag("explore_refresh").getUnclippedBoundsInRoot()
            assertEquals((content.top.value + content.bottom.value) / 2, (error.top.value + retry.bottom.value) / 2, 1f)
            onNodeWithTag("explore_refresh").performClick().assertIsNotEnabled()
            runOnIdle { assertEquals(1, robot.retries) }
            runOnIdle { robot.showLoaded(isEmpty = false, isRefreshing = false) }
            onNodeWithTag("explore_error").assertDoesNotExist()
            onNodeWithTag("explore_refresh").assertDoesNotExist()
        }

    @Test
    fun cachedNetworkAndSaveFailuresKeepContentAndOfferRetry() =
        runComposeUiTest {
            val robot = ExploreLoadingRobot(this, isDetail, widthDp).setContent()
            for (error in listOf(DataError.Network.NO_INTERNET, DataError.Local.UNKNOWN)) {
                runOnIdle { robot.showError(hasCache = true, error = error.toExploreUiText()) }
                robot.assertLoadedContent()
                onNodeWithTag("explore_initial_status").assertDoesNotExist()
                onNodeWithTag("explore_loading").assertDoesNotExist()
                onNodeWithTag("explore_error").assertIsDisplayed()
                onNodeWithTag("explore_refresh").performClick().assertIsNotEnabled()
            }
            runOnIdle { assertEquals(2, robot.retries) }
        }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "detail={0}, width={1}")
        fun configurations(): List<Array<Any>> =
            listOf(
                arrayOf(false, 384),
                arrayOf(true, 384),
                arrayOf(false, 900),
                arrayOf(true, 900),
            )
    }
}

private class ExploreLoadingRobot(
    private val test: ComposeUiTest,
    private val isDetail: Boolean,
    private val widthDp: Int,
) {
    private var categories by mutableStateOf(ExploreState())
    private var books by mutableStateOf(CategoryBooksState(category = categoryBooksPreviewState().category))
    var retries = 0
        private set

    fun setContent() =
        apply {
            test.setContent {
                val density =
                    LocalWindowInfo.current.containerSize.width
                        .toFloat() / widthDp
                val fontScale = if (widthDp >= 600) 1.5f else LocalDensity.current.fontScale
                CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) {
                    BookNestTheme {
                        Box(Modifier.fillMaxSize().testTag("loading_test_host")) {
                            if (isDetail) {
                                CategoryBooksScreen(books, {
                                    if (it == CategoryBooksAction.OnRefreshClick) retry()
                                })
                            } else {
                                ExploreScreen(categories, {
                                    if (it == ExploreAction.OnRefreshClick) retry()
                                })
                            }
                        }
                    }
                }
            }
        }

    fun showLoaded(
        isEmpty: Boolean,
        isRefreshing: Boolean,
    ) = apply {
        categories =
            explorePreviewState().copy(
                categories = if (isEmpty) emptyList() else explorePreviewState().categories,
                isRefreshing = isRefreshing,
                isShowingCachedData = isRefreshing,
            )
        books =
            categoryBooksPreviewState().copy(
                books = if (isEmpty) emptyList() else categoryBooksPreviewState().books,
                total = if (isEmpty) 0 else 1,
                isRefreshing = isRefreshing,
                isShowingCachedData = isRefreshing,
            )
    }

    fun showError(
        hasCache: Boolean,
        error: UiText = UiText.DynamicString("Unable to load"),
    ) = apply {
        showLoaded(isEmpty = false, isRefreshing = false)
        categories = categories.copy(hasLoaded = hasCache, isShowingCachedData = hasCache, error = error)
        books =
            books.copy(
                hasLoaded = hasCache,
                hasShownCatalog = hasCache,
                isShowingCachedData = hasCache,
                error = error,
            )
    }

    fun assertLoadedContent() =
        apply {
            test.onNodeWithTag(if (isDetail) "explore_book_10" else "explore_category_1").assertIsDisplayed()
        }

    private fun retry() {
        retries++
        categories = categories.copy(isRefreshing = true, isLoading = !categories.hasLoaded)
        books = books.copy(isRefreshing = true, isLoading = !books.hasLoaded)
    }
}
