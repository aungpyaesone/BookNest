package com.nps.booknest

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
class AppViewModelTest {
    @Test
    fun missingPreferenceStateStartsInLightTheme() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val viewModel = AppViewModel(FakeThemeLocalDataSource())

                assertThat(viewModel.state.value.themeMode).isEqualTo(ThemeMode.LIGHT)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun preferenceChangesUpdateAppThemeState() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val dataSource = FakeThemeLocalDataSource()
                val viewModel = AppViewModel(dataSource)

                dataSource.setThemeMode(ThemeMode.SYSTEM)
                runCurrent()

                assertThat(viewModel.state.value.themeMode).isEqualTo(ThemeMode.SYSTEM)
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
