package com.nps.booknest.feature.reader.presentation

import androidx.compose.runtime.Stable
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.core.settings.ReaderFontSize
import com.nps.booknest.core.settings.ReaderThemeMode
import com.nps.booknest.feature.reader.domain.ReadableBook

@Stable
data class ReaderState(
    val isLoading: Boolean = true,
    val book: ReadableBook? = null,
    val chapterIndex: Int = 0,
    val paragraphIndex: Int = 0,
    val overallProgress: Float = 0f,
    val fontSize: ReaderFontSize = ReaderFontSize.MEDIUM,
    val themeMode: ReaderThemeMode = ReaderThemeMode.LIGHT,
    val isChapterSheetVisible: Boolean = false,
    val isSettingsSheetVisible: Boolean = false,
    val scrollRequestId: Int = 0,
)

sealed interface ReaderAction {
    data object OnBackClick : ReaderAction

    data object OnChapterListClick : ReaderAction

    data object OnChapterSheetDismiss : ReaderAction

    data object OnSettingsClick : ReaderAction

    data object OnSettingsSheetDismiss : ReaderAction

    data object OnPreviousChapterClick : ReaderAction

    data object OnNextChapterClick : ReaderAction

    data class OnChapterClick(
        val chapterIndex: Int,
    ) : ReaderAction

    data class OnVisibleParagraphChange(
        val paragraphIndex: Int,
    ) : ReaderAction

    data class OnThemeModeSelect(
        val themeMode: ReaderThemeMode,
    ) : ReaderAction

    data object OnDecreaseFontSizeClick : ReaderAction

    data object OnIncreaseFontSizeClick : ReaderAction
}

sealed interface ReaderEvent {
    data object NavigateBack : ReaderEvent

    data class ShowSnackbar(
        val message: UiText,
    ) : ReaderEvent
}
