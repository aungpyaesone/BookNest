package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestBookCover
import com.nps.booknest.core.designsystem.bookNestBookmarkStarIcon
import com.nps.booknest.core.designsystem.bookNestCircledCheckIcon
import com.nps.booknest.core.designsystem.bookNestDownloadTrayIcon
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_cover
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_downloaded
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_finished
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_more
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_progress
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_saved
import com.nps.booknest.feature.library.presentation.overview.LibraryBookUi
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AllBooksGridCard(
    book: LibraryBookUi,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().testTag("all_books_card_${book.id}"),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.fillMaxWidth().padding(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val coverWidth = minOf(maxWidth * 0.78f, (maxWidth - 24.dp).coerceAtLeast(0.dp))
                BookNestBookCover(
                    book.coverUrl,
                    book.title,
                    stringResource(Res.string.library_browse_cover, book.title),
                    Modifier.width(coverWidth).aspectRatio(0.68f).testTag("all_books_cover_${book.id}"),
                )
                IconButton(
                    onClick,
                    Modifier.align(Alignment.TopEnd).size(48.dp).testTag("all_books_more_${book.id}"),
                ) {
                    Box(Modifier.fillMaxSize().padding(end = 2.dp), contentAlignment = Alignment.CenterEnd) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            stringResource(Res.string.library_browse_more),
                            Modifier.size(18.dp).testTag("all_books_more_icon_${book.id}"),
                        )
                    }
                }
            }
            Text(
                book.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                minLines = 2,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                book.author,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shadowElevation = 1.dp,
            ) {
                Text(
                    stringResource(book.category.label()),
                    Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    minLines = 1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            AllBooksGridStatus(book)
        }
    }
}

@Composable
private fun AllBooksGridStatus(book: LibraryBookUi) {
    val status = book.browseStatus()
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (status == LibraryBookStatus.READING) {
            LinearProgressIndicator(
                progress = { book.progress / 100f },
                modifier = Modifier.weight(1f).height(3.dp),
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
            Text(
                stringResource(Res.string.library_browse_progress, book.progress),
                style = MaterialTheme.typography.labelSmall,
            )
        } else if (status != LibraryBookStatus.AVAILABLE) {
            val label =
                when (status) {
                    LibraryBookStatus.SAVED -> Res.string.library_browse_saved
                    LibraryBookStatus.FINISHED -> Res.string.library_browse_finished
                    else -> Res.string.library_browse_downloaded
                }
            Icon(
                when (status) {
                    LibraryBookStatus.SAVED -> bookNestBookmarkStarIcon
                    LibraryBookStatus.FINISHED -> bookNestCircledCheckIcon
                    else -> bookNestDownloadTrayIcon
                },
                null,
                Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(stringResource(label), style = MaterialTheme.typography.labelSmall)
        }
    }
}
