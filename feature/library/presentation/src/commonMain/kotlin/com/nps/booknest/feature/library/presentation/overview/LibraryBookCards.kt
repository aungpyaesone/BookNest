package com.nps.booknest.feature.library.presentation.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestBookCover
import com.nps.booknest.core.designsystem.BookNestCompactButton
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_added
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_chapter
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_continue
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_cover
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_more
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_notes_count
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_page
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_progress
import com.nps.booknest.feature.library.presentation.libraryIsTablet
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LibraryReadingRow(
    books: List<LibraryBookUi>,
    onAction: (LibraryOverviewAction) -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxWidth().testTag("library_reading_section")) {
        val isTablet = libraryIsTablet()
        val coverWidth = if (isTablet) 104.dp else 64.dp
        val featuredWidth =
            if (!isTablet) {
                (maxWidth - (coverWidth + BookNestSpacing.small) * 2).coerceAtLeast(200.dp)
            } else {
                336.dp
            }
        if (books.isEmpty()) {
            LibraryOverviewEmpty()
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
                verticalAlignment = Alignment.Top,
            ) {
                item(key = books.first().id) {
                    FeaturedReadingBook(books.first(), onAction, Modifier.width(featuredWidth))
                }
                items(books.drop(1), key = LibraryBookUi::id) { book ->
                    Column(
                        Modifier.width(coverWidth).clickable { onAction(LibraryOverviewAction.OnComingSoonClick) },
                        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall),
                    ) {
                        LibraryOverviewCover(book, Modifier.fillMaxWidth())
                        LibraryReadingProgress(book)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedReadingBook(
    book: LibraryBookUi,
    onAction: (LibraryOverviewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.testTag("library_featured_reading"),
        horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
    ) {
        LibraryOverviewCover(book, Modifier.width(if (libraryIsTablet()) 88.dp else 72.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall)) {
            LibraryBookTitle(book)
            Text(
                stringResource(Res.string.library_overview_chapter, book.chapter),
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                stringResource(Res.string.library_overview_page, book.currentPage, book.pageCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LibraryReadingProgress(book)
            BookNestCompactButton(
                stringResource(Res.string.library_overview_continue),
                { onAction(LibraryOverviewAction.OnComingSoonClick) },
                Modifier.testTag("library_continue_reading"),
                textStyle = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
internal fun LibraryReadingProgress(book: LibraryBookUi) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall),
    ) {
        LinearProgressIndicator(
            progress = { book.progress / 100f },
            modifier = Modifier.weight(1f).height(3.dp),
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Text(
            stringResource(Res.string.library_overview_progress, book.progress),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
internal fun LibraryRecentRow(
    books: List<LibraryBookUi>,
    onAction: (LibraryOverviewAction) -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxWidth().testTag("library_recent_section")) {
        val cardWidth = ((maxWidth - BookNestSpacing.medium) / 2).coerceAtLeast(156.dp)
        if (books.isEmpty()) {
            LibraryOverviewEmpty()
        } else {
            Row(
                Modifier.horizontalScroll(rememberScrollState()).height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
            ) {
                books.forEach { book -> LibraryRecentBookCard(book, onAction, Modifier.width(cardWidth)) }
            }
        }
    }
}

@Composable
internal fun LibraryBookRow(
    book: LibraryBookUi,
    onAction: (LibraryOverviewAction) -> Unit,
    modifier: Modifier = Modifier,
    showNotes: Boolean = false,
) {
    Box(
        modifier
            .fillMaxWidth()
            .testTag("library_book_${book.id}")
            .clickable { onAction(LibraryOverviewAction.OnComingSoonClick) },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
            verticalAlignment = Alignment.Top,
        ) {
            LibraryOverviewCover(book, Modifier.width(if (libraryIsTablet()) 64.dp else 48.dp))
            Column(
                Modifier.weight(1f).padding(end = 32.dp),
                verticalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
            ) {
                LibraryBookTitle(book)
                Text(
                    stringResource(Res.string.library_overview_added, book.addedDaysAgo),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (book.isReading) LibraryReadingProgress(book)
                if (showNotes) Text(stringResource(Res.string.library_overview_notes_count, book.noteCount))
            }
        }
        IconButton(
            onClick = { onAction(LibraryOverviewAction.OnComingSoonClick) },
            modifier = Modifier.align(Alignment.TopEnd).size(48.dp).testTag("library_book_more_${book.id}"),
        ) {
            Icon(
                Icons.Outlined.MoreVert,
                stringResource(Res.string.library_overview_more, book.title),
                Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun LibraryBookTitle(book: LibraryBookUi) {
    Column {
        Text(
            book.title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            book.author,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LibraryOverviewCover(
    book: LibraryBookUi,
    modifier: Modifier = Modifier,
) {
    BookNestBookCover(
        book.coverUrl,
        book.title,
        stringResource(Res.string.library_overview_cover, book.title),
        modifier.aspectRatio(0.68f),
    )
}
