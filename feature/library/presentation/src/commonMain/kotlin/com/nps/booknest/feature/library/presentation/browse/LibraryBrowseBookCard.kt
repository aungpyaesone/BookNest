package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestBookCover
import com.nps.booknest.core.designsystem.BookNestCompactButton
import com.nps.booknest.core.designsystem.bookNestBookmarkStarIcon
import com.nps.booknest.core.designsystem.bookNestCircledCheckIcon
import com.nps.booknest.core.designsystem.bookNestDownloadTrayIcon
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_book_more
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_chapter
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_continue
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_cover
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_downloaded
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_finished
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_page
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_progress
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_read
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_saved
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_view
import com.nps.booknest.feature.library.presentation.libraryIsTablet
import com.nps.booknest.feature.library.presentation.overview.LibraryBookUi
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LibraryBrowseBookCard(
    book: LibraryBookUi,
    mode: LibraryBooksMode,
    onClick: () -> Unit,
) {
    if (mode == LibraryBooksMode.MY_BOOKS) {
        MyBooksBookCard(book, onClick)
    } else {
        ElevatedCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().testTag("library_browse_book_${book.id}"),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            BoxWithConstraints(Modifier.fillMaxWidth().padding(8.dp)) {
                val hasSideActions = maxWidth >= 344.dp && LocalDensity.current.fontScale < 1.3f
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                    BookNestBookCover(
                        book.coverUrl,
                        book.title,
                        stringResource(Res.string.library_browse_cover, book.title),
                        Modifier.width(if (libraryIsTablet()) 72.dp else 56.dp).aspectRatio(0.68f),
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            book.title,
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
                        if (mode ==
                            LibraryBooksMode.MY_BOOKS
                        ) {
                            Text(
                                stringResource(book.category.label()),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        LibraryBrowseBookProgress(book, mode)
                        if (!hasSideActions) LibraryBookActions(book, onClick)
                    }
                    if (hasSideActions) LibraryBookActions(book, onClick)
                }
            }
        }
    }
}

@Composable
private fun LibraryBookActions(
    book: LibraryBookUi,
    onClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BookNestCompactButton(
            stringResource(
                when (book.browseStatus()) {
                    LibraryBookStatus.READING -> Res.string.library_browse_continue
                    LibraryBookStatus.SAVED, LibraryBookStatus.AVAILABLE -> Res.string.library_browse_read
                    else -> Res.string.library_browse_view
                },
            ),
            onClick,
            Modifier.testTag("library_browse_book_action_${book.id}"),
            textStyle = MaterialTheme.typography.labelSmall,
        )
        IconButton(onClick, Modifier.size(48.dp).testTag("library_browse_book_more_${book.id}")) {
            Icon(
                Icons.Outlined.MoreVert,
                stringResource(Res.string.library_browse_book_more, book.title),
                Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun LibraryBrowseBookProgress(
    book: LibraryBookUi,
    mode: LibraryBooksMode,
) {
    if (book.browseStatus() == LibraryBookStatus.READING) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(progress = {
                    book.progress / 100f
                }, modifier = Modifier.weight(1f).height(4.dp), gapSize = 0.dp, drawStopIndicator = {})
                Text(
                    stringResource(Res.string.library_browse_progress, book.progress),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Text(
                if (mode ==
                    LibraryBooksMode.READING
                ) {
                    stringResource(Res.string.library_browse_page, book.currentPage, book.pageCount)
                } else {
                    stringResource(Res.string.library_browse_chapter, book.chapter, book.chapterCount)
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        val status = book.browseStatus()
        if (status != LibraryBookStatus.AVAILABLE) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    when (status) {
                        LibraryBookStatus.FINISHED -> bookNestCircledCheckIcon
                        LibraryBookStatus.SAVED -> bookNestBookmarkStarIcon
                        else -> bookNestDownloadTrayIcon
                    },
                    null,
                    Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    stringResource(
                        when (status) {
                            LibraryBookStatus.FINISHED -> Res.string.library_browse_finished
                            LibraryBookStatus.SAVED -> Res.string.library_browse_saved
                            else -> Res.string.library_browse_downloaded
                        },
                    ),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
