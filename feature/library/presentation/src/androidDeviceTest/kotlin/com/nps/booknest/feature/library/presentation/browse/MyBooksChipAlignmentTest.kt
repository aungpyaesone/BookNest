@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.feature.library.presentation.overview.LibraryTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MyBooksChipAlignmentTest {
    @Test
    fun labelsAndCountsStayCenteredAcrossWidthsAndTextScales() {
        listOf(320, 360, 412, 840, 1024).forEach { width ->
            listOf(1f, 1.3f, 1.5f).forEach { scale ->
                runComposeUiTest {
                    var action: LibraryBooksAction? = null
                    setContent {
                        val density =
                            LocalWindowInfo.current.containerSize.width
                                .toFloat() / width
                        CompositionLocalProvider(LocalDensity provides Density(density, scale)) {
                            BookNestTheme(isDarkTheme = scale == 1.5f) {
                                LibraryBooksChips(
                                    LibraryBooksState(
                                        mode = LibraryBooksMode.MY_BOOKS,
                                        statusCounts = mapOf(LibraryTab.ALL to 6, LibraryTab.READING to 3),
                                    ),
                                    { action = it },
                                )
                            }
                        }
                    }
                    listOf("All\n6", "Reading Now\n3").forEach { label ->
                        val layouts = mutableListOf<TextLayoutResult>()
                        onNodeWithText(label, useUnmergedTree = true)
                            .performScrollTo()
                            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
                        val layout = layouts.single()
                        assertEquals(2, layout.lineCount)
                        assertFalse(
                            "$width/$scale $label size=${layout.size} paragraph=${layout.multiParagraph.width}x${layout.multiParagraph.height}",
                            layout.hasVisualOverflow,
                        )
                        repeat(2) { line ->
                            assertEquals(
                                layout.size.width / 2f,
                                (layout.getLineLeft(line) + layout.getLineRight(line)) / 2f,
                                1f,
                            )
                        }
                    }
                    onNodeWithTag("library_browse_status_READING").performClick()
                    assertEquals(LibraryBooksAction.OnStatusSelect(LibraryTab.READING), action)
                }
            }
        }
    }
}
