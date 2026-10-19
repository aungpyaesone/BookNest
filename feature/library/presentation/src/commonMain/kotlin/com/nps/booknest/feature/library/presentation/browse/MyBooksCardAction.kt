package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_book_more
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_continue
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_read
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_view
import com.nps.booknest.feature.library.presentation.overview.LibraryBookUi
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MyBooksCardAction(
    book: LibraryBookUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val status = book.browseStatus()
    val isReading = status == LibraryBookStatus.READING
    val label =
        when (status) {
            LibraryBookStatus.READING -> Res.string.library_browse_continue
            LibraryBookStatus.SAVED, LibraryBookStatus.AVAILABLE -> Res.string.library_browse_read
            else -> Res.string.library_browse_view
        }
    Box(
        modifier
            .widthIn(min = 72.dp)
            .heightIn(min = 48.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .testTag("library_browse_book_action_${book.id}"),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.widthIn(min = 72.dp).heightIn(min = 32.dp).testTag("my_books_action_visual_${book.id}"),
            shape = RoundedCornerShape(8.dp),
            color = if (isReading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
            contentColor =
                if (isReading) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
        ) {
            Text(
                stringResource(label),
                Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
internal fun MyBooksCardOverflow(
    book: LibraryBookUi,
    onClick: () -> Unit,
) {
    IconButton(onClick, Modifier.size(48.dp).testTag("library_browse_book_more_${book.id}")) {
        Icon(
            Icons.Outlined.MoreVert,
            stringResource(Res.string.library_browse_book_more, book.title),
            Modifier.size(18.dp),
        )
    }
}
