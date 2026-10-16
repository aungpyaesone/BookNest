package com.nps.booknest.core.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class DefaultReaderPreferencesLocalDataSourceTest {
    @Test
    fun `missing preferences default to light and medium`() {
        val dataSource = DefaultReaderPreferencesLocalDataSource(FakeReaderSettingsStorage())

        assertThat(dataSource.preferences.value).isEqualTo(ReaderPreferences())
    }

    @Test
    fun `reader selections are persisted and emitted`() {
        val storage = FakeReaderSettingsStorage()
        val dataSource = DefaultReaderPreferencesLocalDataSource(storage)

        dataSource.setThemeMode(ReaderThemeMode.SEPIA)
        dataSource.setFontSize(ReaderFontSize.LARGE)

        assertThat(storage.values[READER_THEME_MODE_KEY]).isEqualTo("sepia")
        assertThat(storage.values[READER_FONT_SIZE_KEY]).isEqualTo("large")
        assertThat(dataSource.preferences.value).isEqualTo(
            ReaderPreferences(themeMode = ReaderThemeMode.SEPIA, fontSize = ReaderFontSize.LARGE),
        )
    }
}

private class FakeReaderSettingsStorage : SettingsStorage {
    val values = mutableMapOf<String, String>()

    override fun readString(key: String): String? = values[key]

    override fun writeString(
        key: String,
        value: String,
    ) {
        values[key] = value
    }
}
