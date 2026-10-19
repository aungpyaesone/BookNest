package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.bookNestBookmarkStarIcon
import com.nps.booknest.core.designsystem.bookNestOpenBookIcon
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_book_more
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_collection_books
import com.nps.booknest.feature.library.presentation.generated.resources.library_collection_business
import com.nps.booknest.feature.library.presentation.generated.resources.library_collection_personal
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LibraryCollectionCard(
    collection: LibraryCollectionUi,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().testTag("collection_${collection.id}"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        when (collection.id) {
            "personal", "business" -> {
                Image(
                    painterResource(
                        if (collection.id ==
                            "personal"
                        ) {
                            Res.drawable.library_collection_personal
                        } else {
                            Res.drawable.library_collection_business
                        },
                    ),
                    null,
                    Modifier.fillMaxWidth().height(72.dp),
                    contentScale = ContentScale.Crop,
                )
            }

            else -> {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
                    Box(Modifier.fillMaxWidth().height(72.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            if (collection.isFavorite) bookNestBookmarkStarIcon else bookNestOpenBookIcon,
                            null,
                            Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        collection.name,
                        style = MaterialTheme.typography.labelMedium,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        stringResource(Res.string.library_browse_collection_books, collection.bookCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick, Modifier.size(48.dp).testTag("collection_more_${collection.id}")) {
                    Icon(
                        Icons.Outlined.MoreVert,
                        stringResource(Res.string.library_browse_book_more, collection.name),
                        Modifier.size(18.dp),
                    )
                }
            }
            Text(
                collection.description,
                style = MaterialTheme.typography.labelSmall,
                minLines = 3,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
