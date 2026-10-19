@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.explore.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.presentation.UiText
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogAppendStatusTest {
    @Test
    fun appendSpinnerIsCenteredAcrossFooter() =
        runComposeUiTest {
            setContent {
                BookNestTheme {
                    Box(Modifier.fillMaxSize().testTag("append_host")) {
                        CatalogAppendStatus(categoryBooksPreviewState().copy(isAppending = true), {})
                    }
                }
            }
            val host = onNodeWithTag("append_host").getUnclippedBoundsInRoot()
            val spinner = onNodeWithTag("catalog_append_loading").getUnclippedBoundsInRoot()
            assertEquals((host.left.value + host.right.value) / 2, (spinner.left.value + spinner.right.value) / 2, 1f)
        }

    @Test
    fun appendRetryDispatchesOnceAndIsDisabledDuringRequest() =
        runComposeUiTest {
            var state by mutableStateOf(categoryBooksPreviewState().copy(appendError = UiText.DynamicString("Offline")))
            var retries = 0
            setContent {
                BookNestTheme {
                    CatalogAppendStatus(state) {
                        if (it == CategoryBooksAction.OnRetryAppend) {
                            retries++
                            state = state.copy(isAppending = true)
                        }
                    }
                }
            }
            onNodeWithTag("catalog_append_retry").performClick().assertIsNotEnabled()
            runOnIdle { assertEquals(1, retries) }
        }
}
