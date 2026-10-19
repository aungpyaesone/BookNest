package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.bookNestBookmarkStarIcon
import com.nps.booknest.core.designsystem.bookNestCircledCheckIcon
import com.nps.booknest.core.designsystem.bookNestDownloadTrayIcon
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_chapter
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_downloaded
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_finished
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_progress
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_saved
import com.nps.booknest.feature.library.presentation.overview.LibraryBookUi
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MyBooksCardProgress(book: LibraryBookUi) {
    val status = book.browseStatus()
    if (status == LibraryBookStatus.READING) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                Modifier.fillMaxWidth().testTag("my_books_progress_row_${book.id}"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LinearProgressIndicator(
                    progress = { book.progress / 100f },
                    modifier = Modifier.weight(1f).height(4.dp).testTag("my_books_progress_bar_${book.id}"),
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )
                Text(
                    stringResource(Res.string.library_browse_progress, book.progress),
                    Modifier.testTag("my_books_percentage_${book.id}"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                stringResource(Res.string.library_browse_chapter, book.chapter, book.chapterCount),
                Modifier.testTag("my_books_chapter_${book.id}"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else if (status != LibraryBookStatus.AVAILABLE) {
        val label =
            when (status) {
                LibraryBookStatus.FINISHED -> Res.string.library_browse_finished
                LibraryBookStatus.SAVED -> Res.string.library_browse_saved
                else -> Res.string.library_browse_downloaded
            }
        Row(
            Modifier.fillMaxWidth().testTag("my_books_status_${book.id}"),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                when (status) {
                    LibraryBookStatus.FINISHED -> bookNestCircledCheckIcon
                    LibraryBookStatus.SAVED -> bookNestBookmarkStarIcon
                    else -> bookNestDownloadTrayIcon
                },
                null,
                Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(stringResource(label), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
