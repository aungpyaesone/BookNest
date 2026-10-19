package com.nps.booknest.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nps.booknest.feature.profile.presentation.EditProfileRoot
import com.nps.booknest.feature.profile.presentation.ProfileRoot
import com.nps.booknest.feature.settings.presentation.SettingsRoot

internal fun EntryProviderScope<NavKey>.profileEntries(backStack: NavBackStack<NavKey>) {
    entry<ProfileKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to ProfileKey)) {
        ProfileRoot(
            onNavigateToEdit = { backStack.add(EditProfileKey) },
            onNavigateToSettings = { backStack.add(ProfileSettingsKey) },
        )
    }
    entry<EditProfileKey>(
        metadata = mapOf(BOOK_NEST_DESTINATION_KEY to ProfileKey, BOOK_NEST_HIDE_NAVIGATION to true),
    ) {
        EditProfileRoot(onNavigateBack = { backStack.removeLastOrNull() })
    }
    entry<ProfileSettingsKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to ProfileSettingsKey)) {
        SettingsRoot(onNavigateBack = { backStack.removeLastOrNull() })
    }
}
