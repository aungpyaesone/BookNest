package com.nps.booknest.feature.reader.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.EmptyResult
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.settings.ReaderFontSize
import com.nps.booknest.core.settings.ReaderPreferences
import com.nps.booknest.core.settings.ReaderPreferencesLocalDataSource
import com.nps.booknest.core.settings.ReaderThemeMode
import com.nps.booknest.feature.reader.domain.BookContentDataSource
import com.nps.booknest.feature.reader.domain.ReadableBook
import com.nps.booknest.feature.reader.domain.ReadableChapter
import com.nps.booknest.feature.reader.domain.ReadableParagraph
import com.nps.booknest.feature.reader.domain.ReadingProgress
import com.nps.booknest.feature.reader.domain.ReadingProgressLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReaderViewModelTest {
    @Test
    fun `saved paragraph restores stable reading location`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val progress = ReadingProgress("demo", "chapter-2", "chapter-2-paragraph-2", 0.75f)
                val viewModel = readerViewModel(savedProgress = progress)
                runCurrent()

                assertThat(viewModel.state.value.chapterIndex).isEqualTo(1)
                assertThat(viewModel.state.value.paragraphIndex).isEqualTo(1)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `visible paragraph is persisted after debounce`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val progressDataSource = FakeReadingProgressLocalDataSource()
                val viewModel = readerViewModel(progressDataSource = progressDataSource)
                runCurrent()

                viewModel.onAction(ReaderAction.OnVisibleParagraphChange(1))
                advanceTimeBy(401)
                runCurrent()

                assertThat(progressDataSource.savedProgress?.paragraphId).isEqualTo("chapter-1-paragraph-2")
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `reader appearance actions update persistent preferences`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val preferences = FakeReaderPreferencesLocalDataSource()
                val viewModel = readerViewModel(preferences = preferences)
                runCurrent()

                viewModel.onAction(ReaderAction.OnThemeModeSelect(ReaderThemeMode.DARK))
                viewModel.onAction(ReaderAction.OnIncreaseFontSizeClick)
                runCurrent()

                assertThat(viewModel.state.value.themeMode).isEqualTo(ReaderThemeMode.DARK)
                assertThat(viewModel.state.value.fontSize).isEqualTo(ReaderFontSize.LARGE)
            } finally {
                Dispatchers.resetMain()
            }
        }
}

private fun readerViewModel(
    savedProgress: ReadingProgress? = null,
    progressDataSource: FakeReadingProgressLocalDataSource = FakeReadingProgressLocalDataSource(savedProgress),
    preferences: FakeReaderPreferencesLocalDataSource = FakeReaderPreferencesLocalDataSource(),
): ReaderViewModel =
    ReaderViewModel(
        bookId = "demo",
        bookContentDataSource = FakeBookContentDataSource(),
        readingProgressLocalDataSource = progressDataSource,
        readerPreferencesLocalDataSource = preferences,
    )

private class FakeBookContentDataSource : BookContentDataSource {
    override suspend fun getBook(bookId: String): Result<ReadableBook, DataError.Local> =
        Result.Success(
            ReadableBook(
                id = bookId,
                title = "Demo",
                author = "Author",
                chapters =
                    (1..2).map { chapterNumber ->
                        ReadableChapter(
                            id = "chapter-$chapterNumber",
                            title = "Chapter $chapterNumber",
                            paragraphs =
                                (1..2).map { paragraphNumber ->
                                    ReadableParagraph(
                                        id = "chapter-$chapterNumber-paragraph-$paragraphNumber",
                                        text = "Paragraph $paragraphNumber",
                                    )
                                },
                        )
                    },
            ),
        )
}

private class FakeReadingProgressLocalDataSource(
    private val initialProgress: ReadingProgress? = null,
) : ReadingProgressLocalDataSource {
    var savedProgress: ReadingProgress? = null

    override fun observeProgress(bookId: String): Flow<Result<ReadingProgress?, DataError.Local>> =
        flowOf(Result.Success(initialProgress))

    override suspend fun saveProgress(progress: ReadingProgress): EmptyResult<DataError.Local> {
        savedProgress = progress
        return Result.Success(Unit)
    }
}

private class FakeReaderPreferencesLocalDataSource : ReaderPreferencesLocalDataSource {
    private val mutablePreferences = MutableStateFlow(ReaderPreferences())
    override val preferences = mutablePreferences

    override fun setThemeMode(themeMode: ReaderThemeMode) {
        mutablePreferences.value = mutablePreferences.value.copy(themeMode = themeMode)
    }

    override fun setFontSize(fontSize: ReaderFontSize) {
        mutablePreferences.value = mutablePreferences.value.copy(fontSize = fontSize)
    }
}
