package com.nps.booknest.feature.library.presentation.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestBookCover
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_added
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_cover
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_more
import com.nps.booknest.feature.library.presentation.libraryIsTablet
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LibraryRecentBookCard(
    book: LibraryBookUi,
    onAction: (LibraryOverviewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxHeight()
            .heightIn(min = if (libraryIsTablet()) 112.dp else 96.dp)
            .testTag("library_book_${book.id}")
            .clickable { onAction(LibraryOverviewAction.OnComingSoonClick) },
    ) {
        Row(Modifier.fillMaxHeight(), horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small)) {
            BookNestBookCover(
                book.coverUrl,
                book.title,
                stringResource(Res.string.library_overview_cover, book.title),
                Modifier.width(if (libraryIsTablet()) 64.dp else 48.dp).aspectRatio(0.68f),
            )
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Text(
                    book.title,
                    modifier = Modifier.padding(end = 24.dp),
                    style = MaterialTheme.typography.labelMedium,
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
                Spacer(Modifier.weight(1f).heightIn(min = BookNestSpacing.extraSmall))
                Text(
                    stringResource(Res.string.library_overview_added, book.addedDaysAgo),
                    Modifier.testTag("library_added_${book.id}"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
