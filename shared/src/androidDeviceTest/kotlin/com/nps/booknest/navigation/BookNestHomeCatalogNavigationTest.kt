@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.domain.Result
import com.nps.booknest.feature.explore.presentation.CategoryBooksRoot
import com.nps.booknest.feature.explore.presentation.CategoryBooksViewModel
import com.nps.booknest.feature.explore.presentation.navigation.CategoryBooksKey
import com.nps.booknest.feature.home.domain.HomeCategory
import com.nps.booknest.feature.home.domain.HomeFeed
import com.nps.booknest.feature.home.domain.HomeRepository
import com.nps.booknest.feature.home.presentation.HomeTestTags
import com.nps.booknest.feature.home.presentation.HomeViewModel
import com.nps.booknest.feature.home.presentation.navigation.HomeKey
import com.nps.booknest.feature.library.presentation.libraryPresentationModule
import com.nps.booknest.feature.library.presentation.navigation.AllBooksKey
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.compose.KoinIsolatedContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class BookNestHomeCatalogNavigationTest {
    @Test
    fun allBooksHidesChromeAndBackRestoresHomeEntry() {
        listOf(360, 1024).forEach { width ->
            runComposeUiTest {
                val robot = HomeCatalogRobot(this).setContent(width)
                onNodeWithTag(HomeTestTags.SEARCH).performTextInput("James")
                robot.openCategory("အားလုံး")
                assertEquals(AllBooksKey, robot.backStack.last())
                onNodeWithTag("library_books_ALL_BOOKS").assertIsDisplayed()
                onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
                onNodeWithTag(AppNavigationTestTags.NAVIGATION_RAIL).assertDoesNotExist()
                onNodeWithTag("library_browse_back").performClick()
                assertEquals(HomeKey, robot.backStack.last())
                onNodeWithTag(HomeTestTags.SEARCH).assertTextContains("James")
                val chrome =
                    if (width >=
                        840
                    ) {
                        AppNavigationTestTags.NAVIGATION_RAIL
                    } else {
                        AppNavigationTestTags.BOTTOM_BAR
                    }
                onNodeWithTag(chrome).assertIsDisplayed()
            }
        }
    }

    @Test
    fun homeCategoryUsesBackendIdAndReturnsDirectlyHome() =
        runComposeUiTest {
            val robot = HomeCatalogRobot(this).setContent(412)
            robot.openCategory("Fiction")
            assertEquals(CategoryBooksKey("1"), robot.backStack.last())
            onNodeWithTag("category_books_screen").assertIsDisplayed()
            onNodeWithTag(AppNavigationTestTags.BOTTOM_BAR).assertDoesNotExist()
            onNodeWithTag("explore_back").performClick()
            assertEquals(HomeKey, robot.backStack.last())
            onNodeWithTag(HomeTestTags.SEARCH).assertIsDisplayed()
        }
}

private class HomeCatalogRobot(
    private val test: ComposeUiTest,
) {
    val backStack = NavBackStack<NavKey>(HomeKey)
    private var feedTag = HomeTestTags.COMPACT_FEED

    fun openCategory(label: String) {
        test.onNodeWithTag(feedTag).performScrollToNode(hasText(label))
        test.onNodeWithText(label).performClick()
    }

    fun setContent(width: Int) =
        apply {
            feedTag = if (width < 600) HomeTestTags.COMPACT_FEED else HomeTestTags.ADAPTIVE_FEED
            val repository =
                object : HomeRepository {
                    override fun observeHome() =
                        flowOf(
                            Result.Success(
                                HomeFeed(
                                    emptyList(),
                                    listOf(HomeCategory(1, "Fiction", "fiction", "book")),
                                    emptyList(),
                                ),
                            ),
                        )

                    override suspend fun refreshHome() = Result.Success(Unit)
                }
            val application =
                koinApplication {
                    modules(libraryPresentationModule, module { viewModel { HomeViewModel(repository) } })
                }
            val explore = NavigationExploreRepository()
            test.setContent {
                KoinIsolatedContext(application) {
                    val density =
                        LocalWindowInfo.current.containerSize.width
                            .toFloat() / width
                    CompositionLocalProvider(LocalDensity provides Density(density)) {
                        BookNestTheme {
                            val chrome = remember { BookNestChromeSceneStrategy({ it.toAppDestination() }, {}) }
                            NavDisplay(
                                backStack = backStack,
                                onBack = { backStack.removeLastOrNull() },
                                entryDecorators =
                                    listOf(
                                        rememberSaveableStateHolderNavEntryDecorator(),
                                        rememberViewModelStoreNavEntryDecorator(),
                                    ),
                                sceneDecoratorStrategies = listOf(chrome),
                                entryProvider =
                                    entryProvider {
                                        entry<HomeKey>(
                                            metadata = mapOf(BOOK_NEST_DESTINATION_KEY to HomeKey),
                                        ) { HomeDestination(backStack) }
                                        libraryBrowseEntries(backStack)
                                        entry<CategoryBooksKey>(metadata = categoryBooksSceneMetadata) { key ->
                                            val viewModel =
                                                remember(key) { CategoryBooksViewModel(key.categoryId, explore) }
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
}
