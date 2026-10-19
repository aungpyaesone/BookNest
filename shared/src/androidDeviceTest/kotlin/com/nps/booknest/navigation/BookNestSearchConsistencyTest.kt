@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.feature.explore.presentation.ExploreAction
import com.nps.booknest.feature.explore.presentation.ExploreScreen
import com.nps.booknest.feature.explore.presentation.ExploreState
import com.nps.booknest.feature.home.presentation.HomeAction
import com.nps.booknest.feature.home.presentation.HomeScreen
import com.nps.booknest.feature.home.presentation.HomeState
import com.nps.booknest.feature.home.presentation.HomeTestTags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BookNestSearchConsistencyTest {
    @Test
    fun compactFieldsMatchAtDefaultScale() = verifySearch(width = 384, fontScale = 1f)

    @Test
    fun compactFieldsGrowTogetherAtLargeScale() = verifySearch(width = 320, fontScale = 1.5f)

    @Test
    fun tabletFieldsMatchAtLargeScale() = verifySearch(width = 900, fontScale = 1.5f)

    @Test
    fun wideFieldsStayWithinTheContentLimit() = verifySearch(width = 1600, fontScale = 1f)

    private fun verifySearch(
        width: Int,
        fontScale: Float,
    ) = runComposeUiTest {
        val isHome = mutableStateOf(true)
        val home = mutableStateOf(HomeState(isLoading = false))
        val explore = mutableStateOf(ExploreState())
        setContent {
            val density =
                LocalWindowInfo.current.containerSize.width
                    .toFloat() / width
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) {
                BookNestTheme {
                    if (isHome.value) {
                        HomeScreen(home.value, {
                            if (it is HomeAction.OnSearchQueryChange) {
                                home.value =
                                    home.value.copy(searchQuery = it.query)
                            }
                        })
                    } else {
                        ExploreScreen(explore.value, {
                            if (it is ExploreAction.OnSearchChange) {
                                explore.value =
                                    explore.value.copy(query = it.query)
                            }
                        })
                    }
                }
            }
        }
        onAllNodesWithContentDescription("အသိပေးချက်များ").assertCountEquals(1)
        val homeBounds = onNodeWithTag(HomeTestTags.SEARCH).getUnclippedBoundsInRoot()
        onNodeWithTag(HomeTestTags.SEARCH).performTextInput("Home")
        runOnIdle {
            assertEquals("Home", home.value.searchQuery)
            isHome.value = false
        }
        onNodeWithTag("explore_back").assertDoesNotExist()
        onNodeWithContentDescription("Notifications").assertDoesNotExist()
        val exploreBounds = onNodeWithTag("explore_category_search").getUnclippedBoundsInRoot()
        assertEquals(homeBounds.height.value, exploreBounds.height.value, 1f)
        assertTrue(exploreBounds.height.value >= 48f)
        if (fontScale == 1f) assertEquals(48f, exploreBounds.height.value, 1f)
        assertEquals(homeBounds.width.value, exploreBounds.width.value, 1f)
        val expectedWidth = minOf(width, 1200) - if (width < 600) 32 else 64
        assertEquals(expectedWidth.toFloat(), exploreBounds.width.value, 1f)
        onNodeWithTag("explore_category_search").performTextInput("Fiction")
        runOnIdle { assertEquals("Fiction", explore.value.query) }
        onNodeWithTag("explore_category_search").assertIsDisplayed()
    }
}
