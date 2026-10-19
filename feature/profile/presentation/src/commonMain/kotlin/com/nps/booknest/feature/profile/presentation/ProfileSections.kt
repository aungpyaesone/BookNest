package com.nps.booknest.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.bookNestBookmarkStarIcon
import com.nps.booknest.core.designsystem.bookNestCircledCheckIcon
import com.nps.booknest.core.designsystem.bookNestDownloadTrayIcon
import com.nps.booknest.core.designsystem.bookNestOpenBookIcon
import com.nps.booknest.feature.profile.presentation.generated.resources.Res
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_about
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_account
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_backup
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_contact
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_download_settings
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_downloads
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_finished
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_go_premium
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_help
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_payment
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_personal
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_preferences
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_premium
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_premium_description
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_reading
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_saved
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_support
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_total
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProfileSummary(onAction: (ProfileAction) -> Unit) {
    val entries =
        listOf(
            Triple(32, Res.string.profile_total, bookNestOpenBookIcon),
            Triple(6, Res.string.profile_reading, bookNestOpenBookIcon),
            Triple(8, Res.string.profile_saved, bookNestBookmarkStarIcon),
            Triple(12, Res.string.profile_finished, bookNestCircledCheckIcon),
            Triple(4, Res.string.profile_downloads, bookNestDownloadTrayIcon),
        )
    ProfilePanel {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val cellWidth = (maxWidth / 5).coerceAtLeast(64.dp * LocalDensity.current.fontScale.coerceAtLeast(1f))
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).height(IntrinsicSize.Min)) {
                entries.forEach { (count, label, icon) ->
                    Surface(
                        onClick = { onAction(ProfileAction.OnComingSoonClick) },
                        modifier = Modifier.width(cellWidth).fillMaxHeight(),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Column(
                            Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(icon, null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                            Text(count.toString(), style = MaterialTheme.typography.labelMedium)
                            Text(
                                stringResource(label),
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfilePremium(onAction: (ProfileAction) -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.WorkspacePremium, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.tertiary)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(stringResource(Res.string.profile_premium), style = MaterialTheme.typography.labelMedium)
                Text(
                    stringResource(Res.string.profile_premium_description),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            BookNestPrimaryButton(
                stringResource(Res.string.profile_go_premium),
                { onAction(ProfileAction.OnComingSoonClick) },
                Modifier.widthIn(max = 112.dp),
            )
        }
    }
}

@Composable
internal fun ProfileAccount(onAction: (ProfileAction) -> Unit) {
    ProfileSection(Res.string.profile_account) {
        ProfileMenuRow(
            Res.string.profile_personal,
            Icons.Outlined.PersonOutline,
            onClick = { onAction(ProfileAction.OnEditClick) },
            hasDivider = true,
        )
        ProfileMenuRow(
            Res.string.profile_preferences,
            Icons.Outlined.Tune,
            onClick = { onAction(ProfileAction.OnComingSoonClick) },
            hasDivider = true,
        )
        ProfileMenuRow(
            Res.string.profile_payment,
            Icons.Outlined.CreditCard,
            onClick = { onAction(ProfileAction.OnComingSoonClick) },
            hasDivider = true,
        )
        ProfileMenuRow(
            Res.string.profile_download_settings,
            Icons.Outlined.CloudDownload,
            onClick = { onAction(ProfileAction.OnComingSoonClick) },
            hasDivider = true,
        )
        ProfileMenuRow(
            Res.string.profile_backup,
            Icons.Outlined.CloudUpload,
            onClick = { onAction(ProfileAction.OnComingSoonClick) },
            hasDivider = false,
        )
    }
}

@Composable
internal fun ProfileSupport(onAction: (ProfileAction) -> Unit) {
    ProfileSection(Res.string.profile_support) {
        ProfileMenuRow(
            Res.string.profile_help,
            Icons.Outlined.HelpOutline,
            onClick = { onAction(ProfileAction.OnComingSoonClick) },
            hasDivider = true,
        )
        ProfileMenuRow(
            Res.string.profile_contact,
            Icons.Outlined.Email,
            onClick = { onAction(ProfileAction.OnComingSoonClick) },
            hasDivider = true,
        )
        ProfileMenuRow(
            Res.string.profile_about,
            Icons.Outlined.Info,
            onClick = { onAction(ProfileAction.OnComingSoonClick) },
            hasDivider = false,
        )
    }
}

@Composable
internal fun ProfileSection(
    title: StringResource,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(title), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        ProfilePanel(content)
    }
}

@Composable
internal fun ProfilePanel(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column { content() }
    }
}

@Composable
private fun ProfileMenuRow(
    label: StringResource,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasDivider: Boolean = true,
) {
    Column(modifier) {
        Surface(onClick = onClick) {
            Row(
                Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Text(stringResource(label), Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                Icon(Icons.Outlined.ChevronRight, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.outline)
            }
        }
        if (hasDivider) {
            HorizontalDivider(
                Modifier.padding(start = 42.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}
