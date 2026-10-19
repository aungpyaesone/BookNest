@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.home.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeSettingsUiTest {
    @Test
    fun compactScrollKeepsHomeAppBarVisible() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(
                layoutMode = HomeLayoutMode.COMPACT,
                width = 360.dp,
            )

            onNodeWithTag(HomeTestTags.COMPACT_FEED)
                .performScrollToNode(hasTestTag(HomeTestTags.PREMIUM_BANNER))

            onNodeWithTag(HomeTestTags.APP_BAR).assertIsDisplayed()
            onNodeWithTag(HomeTestTags.SETTINGS_ACTION).assertIsDisplayed()
        }

    @Test
    fun popularBookUsesSingleLineTitleAndPriceFreeRentLabel() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(
                layoutMode = HomeLayoutMode.COMPACT,
                width = 360.dp,
            )

            onNodeWithTag(HomeTestTags.COMPACT_FEED)
                .performScrollToNode(hasTestTag(HomeTestTags.rentAction("quiet-pages")))
            onNodeWithTag(HomeTestTags.rentAction("quiet-pages"))
                .assertTextContains("ငှားမည်")
            val titleBounds =
                onNodeWithTag(
                    HomeTestTags.popularBookTitle("quiet-pages"),
                    useUnmergedTree = true,
                ).getUnclippedBoundsInRoot()
            assertTrue(titleBounds.height.value <= 22.dp.value * density.density)
        }

    @Test
    fun popularBookBuyAndRentActionsUseCompactGap() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(
                layoutMode = HomeLayoutMode.COMPACT,
                width = 360.dp,
            )

            onNodeWithTag(HomeTestTags.COMPACT_FEED)
                .performScrollToNode(hasTestTag(HomeTestTags.rentAction("quiet-pages")))
            val buyBounds =
                onNodeWithTag(HomeTestTags.buyAction("quiet-pages"))
                    .getUnclippedBoundsInRoot()
            val rentBounds =
                onNodeWithTag(HomeTestTags.rentAction("quiet-pages"))
                    .getUnclippedBoundsInRoot()

            assertEquals(
                4.dp.value * density.density,
                (rentBounds.top - buyBounds.bottom).value,
                0.5f,
            )
            onNodeWithTag(HomeTestTags.buyAction("quiet-pages")).performClick()
            onNodeWithTag(HomeTestTags.rentAction("quiet-pages")).performClick()
        }

    @Test
    fun settingsIconDispatchesSettingsAction() =
        runComposeUiTest {
            var settingsClickCount = 0
            HomeScreenRobot(this).setContent(
                layoutMode = HomeLayoutMode.COMPACT,
                width = 360.dp,
                onAction = { action ->
                    if (action == HomeAction.OnSettingsClick) settingsClickCount += 1
                },
            )

            onNodeWithTag(HomeTestTags.SETTINGS_ACTION).performClick()
            runOnIdle { assertTrue(settingsClickCount == 1) }
        }
}
