package com.nps.booknest.feature.reader.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nps.booknest.core.domain.Result
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.core.presentation.toUiText
import com.nps.booknest.core.settings.ReaderFontSize
import com.nps.booknest.core.settings.ReaderPreferencesLocalDataSource
import com.nps.booknest.feature.reader.domain.BookContentDataSource
import com.nps.booknest.feature.reader.domain.ReadableBook
import com.nps.booknest.feature.reader.domain.ReadingProgress
import com.nps.booknest.feature.reader.domain.ReadingProgressLocalDataSource
import com.nps.booknest.feature.reader.presentation.generated.resources.Res
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_progress_save_error
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PROGRESS_SAVE_DEBOUNCE_MILLIS = 400L

class ReaderViewModel(
    private val bookId: String,
    private val bookContentDataSource: BookContentDataSource,
    private val readingProgressLocalDataSource: ReadingProgressLocalDataSource,
    private val readerPreferencesLocalDataSource: ReaderPreferencesLocalDataSource,
) : ViewModel() {
    private val mutableState = MutableStateFlow(ReaderState())
    val state = mutableState.asStateFlow()

    private val eventChannel = Channel<ReaderEvent>(capacity = Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var progressSaveJob: Job? = null

    init {
        observePreferences()
        loadBook()
    }

    fun onAction(action: ReaderAction) {
        when (action) {
            ReaderAction.OnBackClick -> sendEvent(ReaderEvent.NavigateBack)
            ReaderAction.OnChapterListClick -> mutableState.update { it.copy(isChapterSheetVisible = true) }
            ReaderAction.OnChapterSheetDismiss -> mutableState.update { it.copy(isChapterSheetVisible = false) }
            ReaderAction.OnSettingsClick -> mutableState.update { it.copy(isSettingsSheetVisible = true) }
            ReaderAction.OnSettingsSheetDismiss -> mutableState.update { it.copy(isSettingsSheetVisible = false) }
            ReaderAction.OnPreviousChapterClick -> selectChapter(mutableState.value.chapterIndex - 1)
            ReaderAction.OnNextChapterClick -> selectChapter(mutableState.value.chapterIndex + 1)
            ReaderAction.OnDecreaseFontSizeClick -> changeFontSize(-1)
            ReaderAction.OnIncreaseFontSizeClick -> changeFontSize(1)
            is ReaderAction.OnChapterClick -> selectChapter(action.chapterIndex)
            is ReaderAction.OnVisibleParagraphChange -> updateVisibleParagraph(action.paragraphIndex)
            is ReaderAction.OnThemeModeSelect -> readerPreferencesLocalDataSource.setThemeMode(action.themeMode)
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            readerPreferencesLocalDataSource.preferences.collect { preferences ->
                mutableState.update {
                    it.copy(
                        fontSize = preferences.fontSize,
                        themeMode = preferences.themeMode,
                    )
                }
            }
        }
    }

    private fun loadBook() {
        viewModelScope.launch {
            when (val result = bookContentDataSource.getBook(bookId)) {
                is Result.Error -> {
                    mutableState.update { it.copy(isLoading = false) }
                    sendEvent(ReaderEvent.ShowSnackbar(result.error.toUiText()))
                }

                is Result.Success -> {
                    restoreBook(result.data)
                }
            }
        }
    }

    private suspend fun restoreBook(book: ReadableBook) {
        val savedProgress =
            when (val result = readingProgressLocalDataSource.observeProgress(book.id).first()) {
                is Result.Error -> {
                    sendEvent(ReaderEvent.ShowSnackbar(result.error.toUiText()))
                    null
                }

                is Result.Success -> {
                    result.data
                }
            }
        val chapterIndex =
            book.chapters
                .indexOfFirst { chapter ->
                    chapter.id == savedProgress?.chapterId
                }.coerceAtLeast(0)
        val chapter = book.chapters.getOrNull(chapterIndex)
        val paragraphIndex =
            chapter
                ?.paragraphs
                ?.indexOfFirst { paragraph -> paragraph.id == savedProgress?.paragraphId }
                ?.coerceAtLeast(0)
                ?: 0
        val progress = calculateProgress(book, chapterIndex, paragraphIndex)

        mutableState.update {
            it.copy(
                isLoading = false,
                book = book,
                chapterIndex = chapterIndex,
                paragraphIndex = paragraphIndex,
                overallProgress = progress,
                scrollRequestId = it.scrollRequestId + 1,
            )
        }
        if (savedProgress != null &&
            (
                savedProgress.chapterId != chapter?.id ||
                    savedProgress.paragraphId != chapter.paragraphs.getOrNull(paragraphIndex)?.id
            )
        ) {
            scheduleProgressSave()
        }
    }

    private fun selectChapter(chapterIndex: Int) {
        val book = mutableState.value.book ?: return
        if (chapterIndex !in book.chapters.indices || chapterIndex == mutableState.value.chapterIndex) return
        mutableState.update {
            it.copy(
                chapterIndex = chapterIndex,
                paragraphIndex = 0,
                overallProgress = calculateProgress(book, chapterIndex, 0),
                isChapterSheetVisible = false,
                scrollRequestId = it.scrollRequestId + 1,
            )
        }
        scheduleProgressSave()
    }

    private fun updateVisibleParagraph(paragraphIndex: Int) {
        val state = mutableState.value
        val book = state.book ?: return
        val chapter = book.chapters.getOrNull(state.chapterIndex) ?: return
        if (paragraphIndex !in chapter.paragraphs.indices || paragraphIndex == state.paragraphIndex) return
        mutableState.update {
            it.copy(
                paragraphIndex = paragraphIndex,
                overallProgress = calculateProgress(book, state.chapterIndex, paragraphIndex),
            )
        }
        scheduleProgressSave()
    }

    private fun changeFontSize(direction: Int) {
        val sizes = ReaderFontSize.entries
        val currentIndex = sizes.indexOf(mutableState.value.fontSize)
        val nextSize = sizes[(currentIndex + direction).coerceIn(sizes.indices)]
        if (nextSize != mutableState.value.fontSize) {
            readerPreferencesLocalDataSource.setFontSize(nextSize)
        }
    }

    private fun scheduleProgressSave() {
        progressSaveJob?.cancel()
        progressSaveJob =
            viewModelScope.launch {
                delay(PROGRESS_SAVE_DEBOUNCE_MILLIS)
                val state = mutableState.value
                val book = state.book ?: return@launch
                val chapter = book.chapters.getOrNull(state.chapterIndex) ?: return@launch
                val paragraph = chapter.paragraphs.getOrNull(state.paragraphIndex) ?: return@launch
                val progress =
                    ReadingProgress(
                        bookId = book.id,
                        chapterId = chapter.id,
                        paragraphId = paragraph.id,
                        overallProgress = state.overallProgress,
                    )
                if (readingProgressLocalDataSource.saveProgress(progress) is Result.Error) {
                    sendEvent(
                        ReaderEvent.ShowSnackbar(
                            UiText.Resource(Res.string.reader_progress_save_error),
                        ),
                    )
                }
            }
    }

    private fun calculateProgress(
        book: ReadableBook,
        chapterIndex: Int,
        paragraphIndex: Int,
    ): Float {
        val totalParagraphs = book.chapters.sumOf { chapter -> chapter.paragraphs.size }
        if (totalParagraphs == 0) return 0f
        val precedingParagraphs = book.chapters.take(chapterIndex).sumOf { chapter -> chapter.paragraphs.size }
        return ((precedingParagraphs + paragraphIndex).toFloat() / totalParagraphs).coerceIn(0f, 1f)
    }

    private fun sendEvent(event: ReaderEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}
