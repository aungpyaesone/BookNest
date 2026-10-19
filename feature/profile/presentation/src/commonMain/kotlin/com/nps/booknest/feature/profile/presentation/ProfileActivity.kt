package com.nps.booknest.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.nps.booknest.feature.profile.presentation.generated.resources.Res
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_activity
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_goal_percent
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_reading_goal
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_reading_goal_value
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_reading_time
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_reading_time_value
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_week_chart
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProfileActivity(onAction: (ProfileAction) -> Unit) {
    ProfileSection(Res.string.profile_activity) {
        Surface(onClick = { onAction(ProfileAction.OnComingSoonClick) }) {
            Row(
                Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Outlined.Schedule, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(Res.string.profile_reading_time), style = MaterialTheme.typography.bodySmall)
                    Text(
                        stringResource(Res.string.profile_reading_time_value),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                WeeklyReadingChart(Modifier.weight(0.85f))
                Icon(Icons.Outlined.ChevronRight, null, Modifier.size(16.dp))
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Surface(onClick = { onAction(ProfileAction.OnComingSoonClick) }) {
            Row(
                Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Outlined.TrackChanges, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(Res.string.profile_reading_goal), style = MaterialTheme.typography.bodySmall)
                    Text(
                        stringResource(Res.string.profile_reading_goal_value),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(Modifier.weight(0.85f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(progress = { 0.5f }, modifier = Modifier.fillMaxWidth().height(3.dp))
                    Text(stringResource(Res.string.profile_goal_percent), style = MaterialTheme.typography.labelSmall)
                }
                Icon(Icons.Outlined.ChevronRight, null, Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun WeeklyReadingChart(modifier: Modifier = Modifier) {
    val description = stringResource(Res.string.profile_week_chart)
    val bars = listOf(8, 13, 10, 18, 6, 9, 8)
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    Row(
        modifier.semantics(mergeDescendants = true) { contentDescription = description },
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        bars.forEachIndexed { index, value ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(3.dp, value.dp).background(MaterialTheme.colorScheme.outlineVariant))
                Text(days[index], style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
