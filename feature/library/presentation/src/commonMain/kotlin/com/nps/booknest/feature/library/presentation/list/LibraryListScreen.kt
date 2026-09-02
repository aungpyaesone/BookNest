package com.nps.booknest.feature.library.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.core.presentation.asString
import com.nps.booknest.feature.library.domain.Book
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_empty_message
import com.nps.booknest.feature.library.presentation.generated.resources.library_empty_title
import com.nps.booknest.feature.library.presentation.generated.resources.library_list_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryListRoot(
    onNavigateToBookDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingSnackbar by remember { mutableStateOf<com.nps.booknest.core.presentation.UiText?>(null) }
    val snackbarMessage = pendingSnackbar?.asString()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is LibraryListEvent.NavigateToBookDetail -> onNavigateToBookDetail(event.bookId)
            is LibraryListEvent.ShowSnackbar -> pendingSnackbar = event.message
        }
    }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            pendingSnackbar = null
        }
    }

    LibraryListScreen(
        state = state,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@Composable
fun LibraryListScreen(
    state: LibraryListState,
    onAction: (LibraryListAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
        ) {
            Text(
                text = stringResource(Res.string.library_list_title),
                modifier = Modifier.padding(horizontal = BookNestSpacing.large, vertical = BookNestSpacing.large),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            when {
                state.isLoading -> {
                    LoadingLibraryContent()
                }

                state.books.isEmpty() -> {
                    EmptyLibraryContent()
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = BookNestSpacing.extraLarge),
                    ) {
                        items(
                            items = state.books,
                            key = Book::id,
                        ) { book ->
                            BookListItem(
                                book = book,
                                onClick = { onAction(LibraryListAction.OnBookClick(book.id)) },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingLibraryContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyLibraryContent(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(BookNestSpacing.section),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.library_empty_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(Res.string.library_empty_message),
            modifier = Modifier.padding(top = BookNestSpacing.small),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun BookListItem(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = BookNestSpacing.large, vertical = BookNestSpacing.medium),
    ) {
        Text(
            text = book.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = book.author,
            modifier = Modifier.padding(top = BookNestSpacing.extraSmall),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview
@Composable
private fun LibraryListPreview() {
    BookNestTheme {
        LibraryListScreen(
            state =
                LibraryListState(
                    isLoading = false,
                    books =
                        listOf(
                            Book(id = "sample", title = "The Left Hand of Darkness", author = "Ursula K. Le Guin"),
                        ),
                ),
            onAction = {},
        )
    }
}
