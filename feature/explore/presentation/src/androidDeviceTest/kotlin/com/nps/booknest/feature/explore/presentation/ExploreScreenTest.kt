@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.explore.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.feature.explore.domain.CatalogSort
import org.junit.Assert.assertEquals
import org.junit.Test

class ExploreScreenTest {
    @Test
    fun mainHasNoBackBellOrRecentlyViewedAndSearchCanShowEmpty() =
        runComposeUiTest {
            setContent {
                var query by remember { mutableStateOf("") }
                BookNestTheme {
                    val fixture = explorePreviewState()
                    ExploreScreen(
                        fixture.copy(
                            query = query,
                            categories = fixture.categories.filter { it.name.contains(query, true) },
                        ),
                        { if (it is ExploreAction.OnSearchChange) query = it.query },
                    )
                }
            }
            onNodeWithTag("explore_back").assertDoesNotExist()
            onNodeWithContentDescription("Notifications").assertDoesNotExist()
            onNodeWithTag("explore_recently_viewed").assertDoesNotExist()
            onNodeWithTag("explore_category_search").performTextInput("not-found")
            onNodeWithTag("explore_empty").assertIsDisplayed()
        }

    @Test
    fun categoryClickDispatchesBackendId() =
        runComposeUiTest {
            var clicked: Long? = null
            setContent {
                BookNestTheme {
                    ExploreScreen(
                        explorePreviewState(),
                        { if (it is ExploreAction.OnCategoryClick) clicked = it.categoryId },
                    )
                }
            }
            onNodeWithTag("explore_category_1").performClick()
            runOnIdle { assertEquals(1L, clicked) }
        }

    @Test
    fun detailShowsApiTotalDemoAndSupportedSortActions() =
        runComposeUiTest {
            var selected: CatalogSort? = null
            setContent {
                var isSortVisible by remember { mutableStateOf(false) }
                BookNestTheme {
                    CategoryBooksScreen(categoryBooksPreviewState().copy(total = 124, isSortVisible = isSortVisible), {
                        when (it) {
                            CategoryBooksAction.OnSortClick -> isSortVisible = true
                            is CategoryBooksAction.OnSortSelect -> selected = it.sort
                            else -> Unit
                        }
                    })
                }
            }
            onNodeWithText("124 Books").assertIsDisplayed()
            onNodeWithText("Demo").assertIsDisplayed()
            onNodeWithTag("category_sort_action").performClick()
            onNodeWithText("Recently Added").performClick()
            runOnIdle { assertEquals(CatalogSort.RECENT, selected) }
            onNodeWithTag("category_search_action").assertDoesNotExist()
            onNodeWithTag("category_filter_action").assertDoesNotExist()
            onNodeWithTag("category_book_search").assertDoesNotExist()
        }

    @Test
    fun tabletDarkThemeAndLargeTextKeepBookAccessible() =
        runComposeUiTest {
            setContent {
                val density = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                    BookNestTheme(isDarkTheme = true) {
                        Box(Modifier.width(900.dp).fillMaxHeight()) {
                            CategoryBooksScreen(categoryBooksPreviewState(), {})
                        }
                    }
                }
            }
            onNodeWithTag("explore_book_10").assertIsDisplayed()
            onNodeWithTag("explore_back").assertIsDisplayed()
        }
}
