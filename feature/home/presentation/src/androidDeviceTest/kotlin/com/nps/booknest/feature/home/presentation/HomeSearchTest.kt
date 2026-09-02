@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.home.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Test

class HomeSearchTest {
    @Test
    fun compactSearchFillsAvailableWidth() =
        verifySearchWidth(
            layoutMode = HomeLayoutMode.COMPACT,
            width = 360.dp,
            expectedSearchWidth = 328.dp,
        )

    @Test
    fun mediumSearchFillsAvailableWidth() =
        verifySearchWidth(
            layoutMode = HomeLayoutMode.MEDIUM,
            width = 600.dp,
            expectedSearchWidth = 536.dp,
        )

    @Test
    fun expandedSearchFillsAvailableWidth() =
        verifySearchWidth(
            layoutMode = HomeLayoutMode.EXPANDED,
            width = 900.dp,
            expectedSearchWidth = 836.dp,
        )

    @Test
    fun logoIsVisibleInHomeAppBar() =
        runComposeUiTest {
            HomeScreenRobot(this).setContent(
                layoutMode = HomeLayoutMode.COMPACT,
                width = 360.dp,
            )

            onNodeWithTag(HomeTestTags.LOGO).assertIsDisplayed()
        }

    private fun verifySearchWidth(
        layoutMode: HomeLayoutMode,
        width: Dp,
        expectedSearchWidth: Dp,
    ) = runComposeUiTest {
        HomeScreenRobot(this).setContent(layoutMode = layoutMode, width = width)

        onNodeWithTag(HomeTestTags.SEARCH).assertWidthIsEqualTo(expectedSearchWidth)
    }
}
