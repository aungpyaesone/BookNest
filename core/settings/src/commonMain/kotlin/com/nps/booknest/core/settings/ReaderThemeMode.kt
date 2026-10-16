package com.nps.booknest.core.settings

enum class ReaderThemeMode(
    internal val storageValue: String,
) {
    LIGHT("light"),
    SEPIA("sepia"),
    DARK("dark"),
    ;

    companion object {
        internal fun fromStorageValue(value: String?): ReaderThemeMode =
            entries.firstOrNull { mode -> mode.storageValue == value } ?: LIGHT
    }
}
