package com.nps.booknest.feature.library.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.nps.booknest.core.designsystem.BookNestAdaptiveText
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.core.presentation.asString
import com.nps.booknest.feature.library.domain.Book
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.book_detail_author
import com.nps.booknest.feature.library.presentation.generated.resources.book_detail_missing
import com.nps.booknest.feature.library.presentation.generated.resources.book_detail_placeholder
import com.nps.booknest.feature.library.presentation.generated.resources.book_detail_read
import com.nps.booknest.feature.library.presentation.generated.resources.book_detail_title
import com.nps.booknest.feature.library.presentation.generated.resources.library_retry
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BookDetailRoot(
    bookId: String,
    onNavigateToReader: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookDetailViewModel = koinViewModel(parameters = { parametersOf(bookId) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingSnackbar by remember { mutableStateOf<com.nps.booknest.core.presentation.UiText?>(null) }
    val snackbarMessage = pendingSnackbar?.asString()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BookDetailEvent.NavigateToReader -> onNavigateToReader(event.bookId)
            is BookDetailEvent.ShowSnackbar -> pendingSnackbar = event.message
        }
    }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            pendingSnackbar = null
        }
    }

    BookDetailScreen(
        state = state,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@Composable
fun BookDetailScreen(
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    BookNestAdaptiveText {
        Scaffold(
            modifier = modifier,
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { contentPadding ->
            when {
                state.isLoading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(contentPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.book == null -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                                .padding(BookNestSpacing.section),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.book_detail_missing),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        TextButton(
                            onClick = { onAction(BookDetailAction.OnRetryClick) },
                            modifier = Modifier.padding(top = BookNestSpacing.small),
                        ) {
                            Text(text = stringResource(Res.string.library_retry))
                        }
                    }
                }

                else -> {
                    BookDetailContent(
                        book = state.book,
                        onAction = onAction,
                        modifier = Modifier.padding(contentPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun BookDetailContent(
    book: Book,
    onAction: (BookDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(BookNestSpacing.section),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.book_detail_title),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = book.title,
            modifier = Modifier.padding(top = BookNestSpacing.small),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = stringResource(Res.string.book_detail_author, book.author),
            modifier = Modifier.padding(top = BookNestSpacing.small),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        BookNestPrimaryButton(
            text = stringResource(Res.string.book_detail_read),
            onClick = { onAction(BookDetailAction.OnReadClick) },
            modifier = Modifier.fillMaxWidth().padding(top = BookNestSpacing.large),
        )
    }
}

@Composable
fun BookDetailPane(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(BookNestSpacing.section),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.book_detail_placeholder),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
private fun BookDetailPreview() {
    BookNestTheme {
        BookDetailScreen(
            state =
                BookDetailState(
                    isLoading = false,
                    book = Book(id = "sample", title = "The Dispossessed", author = "Ursula K. Le Guin"),
                ),
            onAction = {},
        )
    }
}
