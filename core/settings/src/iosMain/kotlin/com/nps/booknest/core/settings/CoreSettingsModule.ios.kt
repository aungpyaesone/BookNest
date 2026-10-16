package com.nps.booknest.core.settings

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

private class IosSettingsStorage : SettingsStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun readString(key: String): String? = userDefaults.stringForKey(key)

    override fun writeString(
        key: String,
        value: String,
    ) {
        userDefaults.setObject(value, forKey = key)
    }
}

actual val coreSettingsModule =
    module {
        singleOf(::IosSettingsStorage) bind SettingsStorage::class
        singleOf(::DefaultThemeLocalDataSource) bind ThemeLocalDataSource::class
        singleOf(::DefaultReaderPreferencesLocalDataSource) bind ReaderPreferencesLocalDataSource::class
    }
