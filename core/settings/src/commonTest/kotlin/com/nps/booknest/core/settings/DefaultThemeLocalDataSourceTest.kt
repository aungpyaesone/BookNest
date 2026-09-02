package com.nps.booknest.core.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class DefaultThemeLocalDataSourceTest {
    @Test
    fun `missing preference defaults to light theme`() {
        val dataSource = DefaultThemeLocalDataSource(FakeThemeSettingsStorage())

        assertThat(dataSource.themeMode.value).isEqualTo(ThemeMode.LIGHT)
    }

    @Test
    fun `invalid preference defaults to light theme`() {
        val storage = FakeThemeSettingsStorage(initialValue = "unknown")
        val dataSource = DefaultThemeLocalDataSource(storage)

        assertThat(dataSource.themeMode.value).isEqualTo(ThemeMode.LIGHT)
    }

    @Test
    fun `theme selection is persisted and emitted`() {
        val storage = FakeThemeSettingsStorage()
        val dataSource = DefaultThemeLocalDataSource(storage)

        dataSource.setThemeMode(ThemeMode.DARK)

        assertThat(storage.value).isEqualTo("dark")
        assertThat(dataSource.themeMode.value).isEqualTo(ThemeMode.DARK)
    }
}

private class FakeThemeSettingsStorage(
    initialValue: String? = null,
) : ThemeSettingsStorage {
    var value: String? = initialValue
        private set

    override fun readString(key: String): String? = value

    override fun writeString(
        key: String,
        value: String,
    ) {
        this.value = value
    }
}
