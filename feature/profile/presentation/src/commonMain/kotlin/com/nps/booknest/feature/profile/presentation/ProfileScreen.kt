package com.nps.booknest.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nps.booknest.core.designsystem.BookNestLogo
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.designsystem.bookNestAdaptiveCompactTypography
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.feature.profile.presentation.generated.resources.Res
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_coming_soon
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_settings
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoot(
    onNavigateToEdit: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val message = stringResource(Res.string.profile_coming_soon)
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ProfileEvent.NavigateToEdit -> onNavigateToEdit()
            ProfileEvent.NavigateToSettings -> onNavigateToSettings()
            ProfileEvent.ShowComingSoon -> snackbar.showSnackbar(message)
        }
    }
    ProfileScreen(state, viewModel::onAction, modifier, snackbar)
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    MaterialTheme(typography = bookNestAdaptiveCompactTypography()) {
        Scaffold(
            modifier = modifier.testTag("profile_screen"),
            snackbarHost = { SnackbarHost(snackbar) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { padding ->
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                val isWide = maxWidth >= 840.dp
                val sidePadding = if (maxWidth < 600.dp) 16.dp else 32.dp
                LazyColumn(
                    Modifier
                        .widthIn(max = 1200.dp)
                        .fillMaxSize()
                        .padding(horizontal = sidePadding)
                        .testTag("profile_feed"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(key = "header") {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            BookNestLogo(
                                "BookNest",
                                Modifier.weight(1f),
                                iconSize = 24.dp,
                                textStyle = MaterialTheme.typography.titleMedium,
                            )
                            IconButton(
                                onClick = { onAction(ProfileAction.OnSettingsClick) },
                                modifier = Modifier.testTag("profile_settings"),
                            ) {
                                Icon(
                                    Icons.Outlined.Settings,
                                    stringResource(Res.string.profile_settings),
                                    Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                    item(key = "identity") { ProfileIdentity(state, onAction) }
                    item(key = "summary") { ProfileSummary(onAction) }
                    item(key = "premium") { ProfilePremium(onAction) }
                    if (isWide) {
                        item(key = "panels") {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                Column(Modifier.weight(1f)) { ProfileActivity(onAction) }
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ProfileAccount(onAction)
                                    ProfileSupport(onAction)
                                }
                            }
                        }
                    } else {
                        item(key = "activity") { ProfileActivity(onAction) }
                        item(key = "account") { ProfileAccount(onAction) }
                        item(key = "support") { ProfileSupport(onAction) }
                    }
                    item(key = "bottom_space") {
                        androidx.compose.foundation.layout
                            .Spacer(Modifier.size(8.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    BookNestTheme { ProfileScreen(ProfileState(), {}) }
}
