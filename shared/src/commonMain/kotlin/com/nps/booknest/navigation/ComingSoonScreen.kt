package com.nps.booknest.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.shared.generated.resources.Res
import com.nps.booknest.shared.generated.resources.app_coming_soon_body
import com.nps.booknest.shared.generated.resources.app_explore_title
import com.nps.booknest.shared.generated.resources.app_profile_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComingSoonScreen(
    destination: AppDestination,
    modifier: Modifier = Modifier,
) {
    val title =
        stringResource(
            when (destination) {
                AppDestination.PROFILE -> Res.string.app_profile_title
                else -> Res.string.app_explore_title
            },
        )
    val icon = if (destination == AppDestination.PROFILE) Icons.Outlined.PersonOutline else Icons.Outlined.Explore
    Column(
        modifier = modifier.fillMaxSize().padding(BookNestSpacing.section),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(BookNestSize.iconLarge),
        )
        Text(
            text = title,
            modifier = Modifier.padding(top = BookNestSpacing.medium),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(Res.string.app_coming_soon_body),
            modifier = Modifier.padding(top = BookNestSpacing.small),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
