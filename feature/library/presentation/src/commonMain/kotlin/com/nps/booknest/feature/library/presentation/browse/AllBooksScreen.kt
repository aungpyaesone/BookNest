package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.feature.library.presentation.overview.libraryOverviewBooks
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.floor

@Composable
fun AllBooksRoot(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AllBooksViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LibraryBooksRoot(state, viewModel::onAction, viewModel.events, onNavigateBack, modifier)
}

@Composable
fun AllBooksScreen(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LibraryBooksScreen(state, onAction, modifier)
}

internal fun allBooksGridColumns(
    contentWidth: Float,
    isTablet: Boolean,
    fontScale: Float,
): Int =
    if (isTablet) {
        floor((contentWidth + 8f) / (168f * fontScale.coerceAtLeast(1f) + 8f)).toInt().coerceAtLeast(1)
    } else if (fontScale >= 1.3f) {
        2
    } else {
        3
    }

@Preview
@Composable
private fun AllBooksPreview() {
    BookNestTheme {
        AllBooksScreen(
            LibraryBooksState(LibraryBooksMode.ALL_BOOKS, books = libraryOverviewBooks(), totalBooks = 6),
            {},
        )
    }
}
