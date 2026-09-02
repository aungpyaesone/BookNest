package com.nps.booknest.core.settings

enum class ThemeMode(
    internal val storageValue: String,
) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system"),
    ;

    internal companion object {
        fun fromStorageValue(value: String?): ThemeMode =
            entries.firstOrNull { themeMode -> themeMode.storageValue == value } ?: LIGHT
    }
}
