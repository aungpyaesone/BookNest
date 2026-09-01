package com.nps.booknest.feature.library.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed interface LibraryNavKey : NavKey

@Serializable
data object LibraryListKey : LibraryNavKey

@Serializable
data class BookDetailKey(
    val bookId: String,
) : LibraryNavKey

val libraryNavigationSerializers =
    SerializersModule {
        polymorphic(NavKey::class) {
            subclass(LibraryListKey::class, LibraryListKey.serializer())
            subclass(BookDetailKey::class, BookDetailKey.serializer())
        }
    }
