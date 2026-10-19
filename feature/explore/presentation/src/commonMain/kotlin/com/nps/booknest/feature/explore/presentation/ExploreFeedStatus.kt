package com.nps.booknest.feature.explore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.core.presentation.asString
import com.nps.booknest.feature.explore.presentation.generated.resources.Res
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_cached
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_retry
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ExploreInitialStatus(
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: UiText?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier.fillMaxSize().testTag("explore_initial_status"),
        contentAlignment = Alignment.Center,
    ) {
        val horizontalPadding =
            if (maxWidth < 600.dp) BookNestSpacing.screenCompact else BookNestSpacing.screenExpanded
        if (error != null) {
            Column(Modifier.widthIn(max = BookNestSize.contentMaxWidth).padding(horizontal = horizontalPadding)) {
                ExploreFeedStatus(isLoading || isRefreshing, false, error, onRetry)
            }
        } else if (isLoading || isRefreshing) {
            CircularProgressIndicator(Modifier.testTag("explore_loading"))
        }
    }
}

@Composable
internal fun ExploreFeedStatus(
    isRefreshing: Boolean,
    isCached: Boolean,
    error: UiText,
    onRefresh: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = BookNestSpacing.small),
        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isCached) {
            Text(
                stringResource(Res.string.explore_cached),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            error.asString(),
            modifier = Modifier.testTag("explore_error"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        TextButton(
            onClick = onRefresh,
            enabled = !isRefreshing,
            modifier = Modifier.testTag("explore_refresh"),
        ) {
            Text(stringResource(Res.string.explore_retry))
        }
    }
}

@Composable
internal fun CatalogAppendStatus(
    state: CategoryBooksState,
    onAction: (CategoryBooksAction) -> Unit,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (state.isAppending) CircularProgressIndicator(Modifier.testTag("catalog_append_loading"))
        state.appendError?.let { error ->
            Row(Modifier.fillMaxWidth()) {
                Text(error.asString(), Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                TextButton(
                    onClick = { onAction(CategoryBooksAction.OnRetryAppend) },
                    enabled = !state.isAppending,
                    modifier = Modifier.testTag("catalog_append_retry"),
                ) {
                    Text(stringResource(Res.string.explore_retry))
                }
            }
        }
    }
}
