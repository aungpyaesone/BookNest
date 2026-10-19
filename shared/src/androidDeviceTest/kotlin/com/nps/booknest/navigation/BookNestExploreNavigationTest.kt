@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.feature.explore.presentation.CategoryBooksRoot
import com.nps.booknest.feature.explore.presentation.CategoryBooksViewModel
import com.nps.booknest.feature.explore.presentation.ExploreRoot
import com.nps.booknest.feature.explore.presentation.ExploreViewModel
import com.nps.booknest.feature.explore.presentation.navigation.CategoryBooksKey
import org.junit.Assert.assertEquals
import org.junit.Test

class BookNestExploreNavigationTest {
    @Test
    fun detailHidesBottomBarAndRestoresCategorySearch() =
        runComposeUiTest {
            ExploreNavigationRobot(this).setContent(width = 384)
            onNodeWithTag("explore_category_search").performTextInput("Fiction")
            onNodeWithTag("explore_category_1").performClick()
            onNodeWithTag("category_books_screen").assertIsDisplayed()
            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertDoesNotExist()
            onNodeWithTag("explore_back").performClick()
            onNodeWithTag("explore_screen").assertIsDisplayed()
            onNodeWithTag("explore_category_2").assertDoesNotExist()
            onNodeWithTag(AppNavigationTestTags.destinationItem(AppDestination.EXPLORE)).assertIsSelected()
        }

    @Test
    fun detailHidesRailAndRestoresMainScrollPosition() =
        runComposeUiTest {
            ExploreNavigationRobot(this).setContent(width = 900)
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertIsDisplayed()
            onNodeWithTag("explore_category_6").performClick()
            onNodeWithTag("category_books_screen").assertIsDisplayed()
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertDoesNotExist()
            onNodeWithTag("explore_back").performClick()
            onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertIsDisplayed()
        }

    @Test
    fun mainScrollSurvivesDetailPushAndPop() =
        runComposeUiTest {
            ExploreNavigationRobot(this).setContent(width = 384)
            onNodeWithTag("explore_category_grid").performScrollToNode(hasTestTag("explore_category_14"))
            val before = onNodeWithTag("explore_category_14").getUnclippedBoundsInRoot()
            onNodeWithTag("explore_category_14").performClick()
            onNodeWithTag("explore_back").performClick()
            val after = onNodeWithTag("explore_category_14").getUnclippedBoundsInRoot()
            assertEquals(before.top.value, after.top.value, 1f)
        }
}

private class ExploreNavigationRobot(
    private val test: ComposeUiTest,
) {
    fun setContent(width: Int) =
        apply {
            val backStack = mutableStateListOf<NavKey>(ExploreKey)
            val repository = NavigationExploreRepository()
            val exploreViewModel = ExploreViewModel(repository)
            test.setContent {
                val density =
                    LocalWindowInfo.current.containerSize.width
                        .toFloat() / width
                CompositionLocalProvider(LocalDensity provides Density(density)) {
                    BookNestTheme {
                        val chrome = remember { BookNestChromeSceneStrategy({ AppDestination.EXPLORE }, {}) }
                        NavDisplay(
                            backStack = backStack,
                            onBack = { backStack.removeLastOrNull() },
                            sceneDecoratorStrategies = listOf(chrome),
                            entryProvider =
                                entryProvider {
                                    entry<ExploreKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to ExploreKey)) {
                                        ExploreRoot(
                                            { backStack.add(CategoryBooksKey(it)) },
                                            viewModel = exploreViewModel,
                                        )
                                    }
                                    entry<CategoryBooksKey>(metadata = categoryBooksSceneMetadata) { key ->
                                        val viewModel =
                                            remember(
                                                key.categoryId,
                                            ) { CategoryBooksViewModel(key.categoryId, repository) }
                                        CategoryBooksRoot(
                                            key.categoryId,
                                            { backStack.removeLastOrNull() },
                                            viewModel = viewModel,
                                        )
                                    }
                                },
                        )
                    }
                }
            }
        }
}
