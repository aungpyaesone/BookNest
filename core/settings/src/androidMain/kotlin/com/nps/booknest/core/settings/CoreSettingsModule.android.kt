package com.nps.booknest.core.settings

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val PREFERENCES_FILE_NAME = "booknest_preferences"

private class AndroidSettingsStorage(
    context: Context,
) : SettingsStorage {
    private val preferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    override fun readString(key: String): String? = preferences.getString(key, null)

    override fun writeString(
        key: String,
        value: String,
    ) {
        preferences.edit().putString(key, value).apply()
    }
}

actual val coreSettingsModule =
    module {
        single<SettingsStorage> { AndroidSettingsStorage(androidContext()) }
        singleOf(::DefaultThemeLocalDataSource) bind ThemeLocalDataSource::class
        singleOf(::DefaultReaderPreferencesLocalDataSource) bind ReaderPreferencesLocalDataSource::class
    }
