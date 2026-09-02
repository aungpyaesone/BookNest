package com.nps.booknest.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.core.settings.ThemeMode
import com.nps.booknest.feature.settings.presentation.generated.resources.Res
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_appearance_title
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_back_description
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_theme_dark
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_theme_dark_description
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_theme_description
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_theme_light
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_theme_light_description
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_theme_system
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_theme_system_description
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_theme_title
import com.nps.booknest.feature.settings.presentation.generated.resources.settings_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoot(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.NavigateBack -> onNavigateBack()
        }
    }

    SettingsScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.testTag(SettingsTestTags.SCREEN),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag(SettingsTestTags.APP_BAR),
                title = {
                    Text(
                        text = stringResource(Res.string.settings_title),
                        modifier = Modifier.testTag(SettingsTestTags.TITLE),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(SettingsAction.OnBackClick) },
                        modifier =
                            Modifier
                                .size(BookNestSize.minimumTouchTarget)
                                .testTag(SettingsTestTags.BACK_ACTION),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(Res.string.settings_back_description),
                            modifier = Modifier.size(BookNestSize.iconSmall),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
    ) { contentPadding ->
        SettingsContent(
            state = state,
            onAction = onAction,
            modifier = Modifier.padding(contentPadding),
        )
    }
}

@Composable
private fun SettingsContent(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .padding(horizontal = BookNestSpacing.screenCompact)
                    .padding(top = BookNestSpacing.large),
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
        ) {
            Text(
                text = stringResource(Res.string.settings_appearance_title),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = stringResource(Res.string.settings_theme_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(Res.string.settings_theme_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            ThemeOptions(
                selectedThemeMode = state.themeMode,
                onThemeModeSelect = { onAction(SettingsAction.OnThemeModeSelect(it)) },
            )
        }
    }
}

@Composable
private fun ThemeOptions(
    selectedThemeMode: ThemeMode,
    onThemeModeSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column {
            ThemeMode.entries.forEachIndexed { index, themeMode ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ThemeOption(
                    themeMode = themeMode,
                    isSelected = selectedThemeMode == themeMode,
                    onClick = { onThemeModeSelect(themeMode) },
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    themeMode: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(themeMode.labelResource())
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .testTag(SettingsTestTags.themeOption(themeMode))
                .padding(horizontal = BookNestSpacing.medium, vertical = BookNestSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
        )
        Spacer(modifier = Modifier.size(BookNestSpacing.small))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = stringResource(themeMode.descriptionResource()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private fun ThemeMode.labelResource(): StringResource =
    when (this) {
        ThemeMode.LIGHT -> Res.string.settings_theme_light
        ThemeMode.DARK -> Res.string.settings_theme_dark
        ThemeMode.SYSTEM -> Res.string.settings_theme_system
    }

private fun ThemeMode.descriptionResource(): StringResource =
    when (this) {
        ThemeMode.LIGHT -> Res.string.settings_theme_light_description
        ThemeMode.DARK -> Res.string.settings_theme_dark_description
        ThemeMode.SYSTEM -> Res.string.settings_theme_system_description
    }

@Preview
@Composable
private fun SettingsScreenPreview() {
    BookNestTheme {
        SettingsScreen(
            state = SettingsState(),
            onAction = {},
        )
    }
}
