package com.nps.booknest.feature.settings.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nps.booknest.core.settings.ThemeLocalDataSource
import com.nps.booknest.core.settings.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @Test
    fun `theme selection is persisted and reflected in state`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val dataSource = FakeThemeLocalDataSource()
                val viewModel = SettingsViewModel(dataSource)

                viewModel.onAction(SettingsAction.OnThemeModeSelect(ThemeMode.DARK))
                runCurrent()

                assertThat(dataSource.themeMode.value).isEqualTo(ThemeMode.DARK)
                assertThat(viewModel.state.value.themeMode).isEqualTo(ThemeMode.DARK)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `back click emits navigation event`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val viewModel = SettingsViewModel(FakeThemeLocalDataSource())
                viewModel.events.test {
                    viewModel.onAction(SettingsAction.OnBackClick)
                    runCurrent()

                    assertThat(awaitItem()).isEqualTo(SettingsEvent.NavigateBack)
                }
            } finally {
                Dispatchers.resetMain()
            }
        }
}

private class FakeThemeLocalDataSource : ThemeLocalDataSource {
    override val themeMode = MutableStateFlow(ThemeMode.LIGHT)

    override fun setThemeMode(themeMode: ThemeMode) {
        this.themeMode.value = themeMode
    }
}
