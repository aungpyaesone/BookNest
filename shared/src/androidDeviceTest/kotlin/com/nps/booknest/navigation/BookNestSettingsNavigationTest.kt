@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.nps.booknest.core.designsystem.BookNestTheme
import org.junit.Assert.assertEquals
import org.junit.Test

class BookNestSettingsNavigationTest {
    @Test
    fun selectedHomeDispatchesWhenAChildDestinationIsVisible() =
        runComposeUiTest {
            var selectionCount = 0
            setContent {
                BookNestTheme {
                    BookNestAppScaffold(
                        selectedDestination = AppDestination.HOME,
                        onDestinationSelect = { selectionCount += 1 },
                        forceExpanded = false,
                        allowSelectedDestinationClick = true,
                    ) {
                        Box(modifier = Modifier)
                    }
                }
            }

            onNodeWithText("ပင်မ").performClick()
            runOnIdle { assertEquals(1, selectionCount) }
        }
}
