package com.nps.booknest.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestCompactButton
import com.nps.booknest.feature.profile.presentation.generated.resources.Res
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_avatar
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_camera
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_edit
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProfileAvatar(onCameraClick: () -> Unit) {
    val description = stringResource(Res.string.profile_avatar)
    Box(Modifier.size(104.dp)) {
        Surface(
            modifier = Modifier.size(88.dp).align(Alignment.Center).semantics { contentDescription = description },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "ML",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        IconButton(
            onClick = onCameraClick,
            modifier = Modifier.align(Alignment.BottomEnd).testTag("profile_camera"),
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                Icon(
                    Icons.Outlined.CameraAlt,
                    stringResource(Res.string.profile_camera),
                    Modifier.padding(6.dp).size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Composable
internal fun ProfileIdentity(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileAvatar { onAction(ProfileAction.OnComingSoonClick) }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(state.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(
                state.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text("“${state.bio}”", style = MaterialTheme.typography.bodySmall)
            BookNestCompactButton(
                stringResource(Res.string.profile_edit),
                { onAction(ProfileAction.OnEditClick) },
                Modifier.testTag("profile_edit"),
                textStyle = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
