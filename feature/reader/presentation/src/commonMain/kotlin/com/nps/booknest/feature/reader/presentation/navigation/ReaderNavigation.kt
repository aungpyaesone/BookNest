package com.nps.booknest.feature.reader.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed interface ReaderNavKey : NavKey

@Serializable
data class ReaderKey(
    val bookId: String,
) : ReaderNavKey

val readerNavigationSerializers =
    SerializersModule {
        polymorphic(NavKey::class) {
            subclass(ReaderKey::class, ReaderKey.serializer())
        }
    }
