package com.nps.booknest.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.presentation.asString
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_cached_data
import com.nps.booknest.feature.home.presentation.generated.resources.home_loading
import com.nps.booknest.feature.home.presentation.generated.resources.home_refresh
import com.nps.booknest.feature.home.presentation.generated.resources.home_retry
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeFeedStatus(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(BookNestSpacing.small)) {
        if (state.isLoading || state.isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().testTag(HomeTestTags.LOADING))
            if (state.isLoading) Text(stringResource(Res.string.home_loading))
        }
        if (state.isShowingCachedData) {
            Text(
                text = stringResource(Res.string.home_cached_data),
                modifier = Modifier.testTag(HomeTestTags.CACHED),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            state.error?.let { error ->
                Text(
                    text = error.asString(),
                    modifier = Modifier.weight(1f).testTag(HomeTestTags.FEED_ERROR),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            TextButton(
                onClick = { onAction(HomeAction.OnRefreshClick) },
                enabled = !state.isRefreshing,
                modifier = Modifier.testTag(HomeTestTags.REFRESH),
            ) {
                Text(stringResource(if (state.error == null) Res.string.home_refresh else Res.string.home_retry))
            }
        }
    }
}
