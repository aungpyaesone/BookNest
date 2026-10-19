@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.home.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.presentation.UiText
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeApiUiTest {
    @Test
    fun firstLoadShowsProgressAndKeepsExistingReadingSection() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(HomeLayoutMode.COMPACT, HomeState(), 360.dp)
            onNodeWithTag(HomeTestTags.LOADING).assertIsDisplayed()
            onNodeWithTag(HomeTestTags.HERO_CARD).assertDoesNotExist()
            onNodeWithTag(HomeTestTags.COMPACT_FEED).performScrollToNode(hasTestTag(HomeTestTags.CONTINUE_READING_CARD))
            onNodeWithTag(HomeTestTags.CONTINUE_READING_CARD).assertIsDisplayed()
        }

    @Test
    fun initialErrorExposesRetryAction() =
        runComposeUiTest {
            var clicks = 0
            HomeScreenRobot(this).setContent(
                HomeLayoutMode.COMPACT,
                HomeState(isLoading = false, error = UiText.DynamicString("Offline")),
                360.dp,
                onAction = { if (it == HomeAction.OnRefreshClick) clicks++ },
            )
            onNodeWithTag(HomeTestTags.FEED_ERROR).assertIsDisplayed()
            onNodeWithTag(HomeTestTags.REFRESH).performClick()
            runOnIdle { assertEquals(1, clicks) }
        }

    @Test
    fun cachedFeedAndRetryRemainVisibleInExpandedLayout() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(
                HomeLayoutMode.EXPANDED,
                homePreviewState().copy(isShowingCachedData = true, error = UiText.DynamicString("Offline")),
                900.dp,
            )
            onNodeWithTag(HomeTestTags.CACHED).assertIsDisplayed()
            onNodeWithTag(HomeTestTags.FEED_ERROR).assertIsDisplayed()
            onNodeWithTag(HomeTestTags.ADAPTIVE_FEED).performScrollToNode(hasTestTag(HomeTestTags.HERO_CARD))
            onNodeWithTag(HomeTestTags.HERO_CARD).assertIsDisplayed()
        }

    @Test
    fun unknownCategoryIconRendersWithReturnedIdentifier() =
        runComposeUiTest {
            var clickedId: Long? = null
            val category = HomeCategoryUi(99L, UiText.DynamicString("New category"), "unknown-icon")
            HomeScreenRobot(this).setContent(
                HomeLayoutMode.EXPANDED,
                homePreviewState().copy(categories = listOf(homeAllCategory, category, homeMoreCategory)),
                900.dp,
                onAction = { if (it is HomeAction.OnCategoryClick) clickedId = it.category.id },
            )
            onNodeWithTag(HomeTestTags.ADAPTIVE_FEED).performScrollToNode(hasText("New category"))
            onNodeWithText("New category").performClick()
            runOnIdle { assertEquals(99L, clickedId) }
        }

    @Test
    fun multipleBannersCreateMatchingIndicators() =
        runComposeUiTest {
            val preview = homePreviewState()
            HomeScreenRobot(this).setContent(
                HomeLayoutMode.EXPANDED,
                preview.copy(banners = List(3) { preview.banners.single().copy(id = "banner-$it") }),
                900.dp,
            )
            onNodeWithTag("home_hero_indicator_2").assertIsDisplayed()
            onNodeWithTag("home_hero_indicator_3").assertDoesNotExist()
        }

    @Test
    fun emptyBannerArrayHidesCarouselInCompactLayout() =
        runComposeUiTest {
            HomeScreenRobot(
                this,
            ).setContent(HomeLayoutMode.COMPACT, homePreviewState().copy(banners = emptyList()), 360.dp)
            onNodeWithTag(HomeTestTags.HERO_CARD).assertDoesNotExist()
        }

    @Test
    fun oneBannerCreatesOneIndicatorAndDispatchesItsIdentifier() =
        runComposeUiTest {
            var clickedId: String? = null
            HomeScreenRobot(this).setContent(
                HomeLayoutMode.COMPACT,
                homePreviewState(),
                360.dp,
                onAction = { if (it is HomeAction.OnBannerClick) clickedId = it.bannerId },
            )
            onNodeWithTag("home_hero_indicator_0").assertIsDisplayed()
            onNodeWithTag("home_hero_indicator_1").assertDoesNotExist()
            onNodeWithTag(HomeTestTags.HERO_ACTION).performClick()
            runOnIdle { assertEquals("preview", clickedId) }
        }
}
