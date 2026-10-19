package com.nps.booknest.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

enum class AppDestination {
    HOME,
    EXPLORE,
    LIBRARY,
    PROFILE,
}

@Serializable
sealed interface AppNavKey : NavKey

@Serializable
data object ExploreKey : AppNavKey

@Serializable
data object ProfileKey : AppNavKey

@Serializable
data object EditProfileKey : AppNavKey

@Serializable
data object ProfileSettingsKey : AppNavKey

@Serializable
data object SettingsKey : AppNavKey

val appNavigationSerializers =
    SerializersModule {
        polymorphic(NavKey::class) {
            subclass(ExploreKey::class, ExploreKey.serializer())
            subclass(ProfileKey::class, ProfileKey.serializer())
            subclass(EditProfileKey::class, EditProfileKey.serializer())
            subclass(ProfileSettingsKey::class, ProfileSettingsKey.serializer())
            subclass(SettingsKey::class, SettingsKey.serializer())
        }
    }
