package com.nps.booknest.core.settings

enum class ReaderFontSize(
    val scale: Float,
    internal val storageValue: String,
) {
    SMALL(scale = 0.9f, storageValue = "small"),
    MEDIUM(scale = 1f, storageValue = "medium"),
    LARGE(scale = 1.15f, storageValue = "large"),
    EXTRA_LARGE(scale = 1.3f, storageValue = "extra_large"),
    ;

    companion object {
        internal fun fromStorageValue(value: String?): ReaderFontSize =
            entries.firstOrNull { size -> size.storageValue == value } ?: MEDIUM
    }
}
