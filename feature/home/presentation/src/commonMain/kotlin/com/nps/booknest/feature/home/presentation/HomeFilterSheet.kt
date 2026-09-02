package com.nps.booknest.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_filter_apply
import com.nps.booknest.feature.home.presentation.generated.resources.home_filter_close_description
import com.nps.booknest.feature.home.presentation.generated.resources.home_filter_rental_only
import com.nps.booknest.feature.home.presentation.generated.resources.home_filter_sort_title
import com.nps.booknest.feature.home.presentation.generated.resources.home_filter_title
import com.nps.booknest.feature.home.presentation.generated.resources.home_sort_price_high
import com.nps.booknest.feature.home.presentation.generated.resources.home_sort_price_low
import com.nps.booknest.feature.home.presentation.generated.resources.home_sort_recommended
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeFilterSheet(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onAction(HomeAction.OnFilterDismiss) },
        modifier = Modifier.testTag(HomeTestTags.FILTER_SHEET),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BookNestSpacing.extraLarge)
                    .padding(bottom = BookNestSpacing.section),
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.large),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(Res.string.home_filter_title), style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = { onAction(HomeAction.OnFilterDismiss) }) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = stringResource(Res.string.home_filter_close_description),
                    )
                }
            }
            HorizontalDivider()
            Text(text = stringResource(Res.string.home_filter_sort_title), style = MaterialTheme.typography.titleMedium)
            SortOrderOption(HomeSortOrder.RECOMMENDED, Res.string.home_sort_recommended, state, onAction)
            SortOrderOption(HomeSortOrder.PRICE_LOW_TO_HIGH, Res.string.home_sort_price_low, state, onAction)
            SortOrderOption(HomeSortOrder.PRICE_HIGH_TO_LOW, Res.string.home_sort_price_high, state, onAction)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.home_filter_rental_only),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Switch(
                    checked = state.isRentalOnly,
                    onCheckedChange = { onAction(HomeAction.OnRentalOnlyChange(it)) },
                )
            }
            BookNestPrimaryButton(
                text = stringResource(Res.string.home_filter_apply),
                onClick = { onAction(HomeAction.OnApplyFilterClick) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SortOrderOption(
    sortOrder: HomeSortOrder,
    label: StringResource,
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = state.selectedSortOrder == sortOrder,
                    role = Role.RadioButton,
                    onClick = { onAction(HomeAction.OnSortOrderSelect(sortOrder)) },
                ).padding(vertical = BookNestSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = state.selectedSortOrder == sortOrder, onClick = null)
        Text(
            text = stringResource(label),
            modifier = Modifier.padding(start = BookNestSpacing.medium),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
