@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.reader.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.feature.reader.domain.ReadableBook
import com.nps.booknest.feature.reader.domain.ReadableChapter
import com.nps.booknest.feature.reader.domain.ReadableParagraph
import org.junit.Assert.assertTrue
import org.junit.Test

class ReaderScreenTest {
    @Test
    fun readerShowsBurmeseContentAndEssentialControls() =
        runComposeUiTest {
            val actions = mutableListOf<ReaderAction>()
            setContent {
                BookNestTheme {
                    ReaderScreen(
                        state = readerStateFixture(),
                        onAction = actions::add,
                    )
                }
            }

            onNodeWithTag(ReaderTestTags.READER_CONTENT).assertIsDisplayed()
            onNodeWithText("အခန်း (၁) — မြူကြားမှ လမ်းကလေး").assertIsDisplayed()
            onNodeWithTag(ReaderTestTags.NEXT_CHAPTER).performClick()
            runOnIdle { assertTrue(actions.contains(ReaderAction.OnNextChapterClick)) }
        }

    @Test
    fun chapterAndAppearanceSheetsCanBeOpened() =
        runComposeUiTest {
            var state by mutableStateOf(readerStateFixture())
            setContent {
                BookNestTheme {
                    ReaderScreen(
                        state = state,
                        onAction = { action ->
                            state =
                                when (action) {
                                    ReaderAction.OnChapterListClick -> state.copy(isChapterSheetVisible = true)
                                    ReaderAction.OnSettingsClick -> state.copy(isSettingsSheetVisible = true)
                                    else -> state
                                }
                        },
                    )
                }
            }

            onNodeWithContentDescription("အခန်းစာရင်း ဖွင့်မည်").performClick()
            onNodeWithTag(ReaderTestTags.CHAPTER_LIST).assertIsDisplayed()
        }
}

private fun readerStateFixture() =
    ReaderState(
        isLoading = false,
        book =
            ReadableBook(
                id = "mountain-morning",
                title = "တောင်တန်းနောက်က နံနက်ခင်း",
                author = "သီဟနိုင်",
                chapters =
                    listOf(
                        ReadableChapter(
                            id = "chapter-1",
                            title = "အခန်း (၁) — မြူကြားမှ လမ်းကလေး",
                            paragraphs =
                                listOf(
                                    ReadableParagraph(
                                        id = "chapter-1-paragraph-1",
                                        text = "မနက်ခင်းအလင်းရောင်က တောင်တန်းပေါ်သို့ ဖြည်းဖြည်းရောက်လာသည်။",
                                    ),
                                ),
                        ),
                        ReadableChapter(
                            id = "chapter-2",
                            title = "အခန်း (၂) — ရေစီးသံနောက်ကွယ်",
                            paragraphs =
                                listOf(
                                    ReadableParagraph("chapter-2-paragraph-1", "စမ်းချောင်းသံကို ကြားရသည်။"),
                                ),
                        ),
                    ),
            ),
    )
