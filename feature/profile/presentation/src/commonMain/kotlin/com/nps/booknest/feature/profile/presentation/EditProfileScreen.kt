package com.nps.booknest.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.designsystem.bookNestAdaptiveCompactTypography
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.feature.profile.presentation.generated.resources.Res
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_back
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_bio
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_bio_count
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_change_picture
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_coming_soon
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_edit
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_email
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_email_readonly
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_name
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_save
import com.nps.booknest.feature.profile.presentation.generated.resources.profile_tagline
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditProfileRoot(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val message = stringResource(Res.string.profile_coming_soon)
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EditProfileEvent.NavigateBack -> onNavigateBack()
            EditProfileEvent.ShowComingSoon -> snackbar.showSnackbar(message)
        }
    }
    EditProfileScreen(state, viewModel::onAction, modifier, snackbar)
}

@Composable
fun EditProfileScreen(
    state: EditProfileState,
    onAction: (EditProfileAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    MaterialTheme(typography = bookNestAdaptiveCompactTypography()) {
        Scaffold(
            modifier = modifier.testTag("edit_profile_screen").imePadding(),
            snackbarHost = { SnackbarHost(snackbar) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { padding ->
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                val sidePadding = if (maxWidth < 600.dp) 16.dp else 32.dp
                Column(
                    Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = sidePadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    EditProfileHeader(onAction)
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        ProfileAvatar { onAction(EditProfileAction.OnCameraClick) }
                        Text(
                            stringResource(Res.string.profile_change_picture),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(Modifier.size(4.dp))
                    EditProfileFields(state, onAction)
                    Spacer(Modifier.size(16.dp))
                    BookNestPrimaryButton(
                        stringResource(Res.string.profile_save),
                        { onAction(EditProfileAction.OnSaveClick) },
                        Modifier.fillMaxWidth().testTag("edit_profile_save"),
                    )
                    Spacer(Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun EditProfileHeader(onAction: (EditProfileAction) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { onAction(EditProfileAction.OnBackClick) },
            modifier = Modifier.testTag("edit_profile_back"),
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(Res.string.profile_back), Modifier.size(20.dp))
        }
        Text(stringResource(Res.string.profile_edit), Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
            BookNestLogo("BookNest", iconSize = 20.dp, textStyle = MaterialTheme.typography.labelSmall)
            Text(stringResource(Res.string.profile_tagline), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun EditProfileFields(
    state: EditProfileState,
    onAction: (EditProfileAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = state.name,
            onValueChange = { onAction(EditProfileAction.OnNameChange(it)) },
            modifier = Modifier.fillMaxWidth().testTag("edit_profile_name"),
            label = { Text(stringResource(Res.string.profile_name)) },
            leadingIcon = { Icon(Icons.Outlined.PersonOutline, null, Modifier.size(20.dp)) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().testTag("edit_profile_email"),
            label = { Text(stringResource(Res.string.profile_email)) },
            leadingIcon = { Icon(Icons.Outlined.Email, null, Modifier.size(20.dp)) },
            supportingText = { Text(stringResource(Res.string.profile_email_readonly)) },
            readOnly = true,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall,
        )
        OutlinedTextField(
            value = state.bio,
            onValueChange = { onAction(EditProfileAction.OnBioChange(it)) },
            modifier = Modifier.fillMaxWidth().testTag("edit_profile_bio"),
            label = { Text(stringResource(Res.string.profile_bio)) },
            leadingIcon = { Icon(Icons.Outlined.Edit, null, Modifier.size(20.dp)) },
            supportingText = {
                Text(
                    stringResource(Res.string.profile_bio_count, state.bio.length),
                    Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                )
            },
            minLines = 3,
            textStyle = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview
@Composable
private fun EditProfileScreenPreview() {
    BookNestTheme { EditProfileScreen(EditProfileState(), {}) }
}
