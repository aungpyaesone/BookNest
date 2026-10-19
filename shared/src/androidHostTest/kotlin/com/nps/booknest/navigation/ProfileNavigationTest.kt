package com.nps.booknest.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class ProfileNavigationTest {
    @Test
    fun `profile edit and settings retain profile selection and settings retain home`() {
        assertEquals(ProfileKey, AppDestination.PROFILE.rootKey())
        listOf(ProfileKey, EditProfileKey, ProfileSettingsKey).forEach {
            assertEquals(AppDestination.PROFILE, it.toAppDestination())
        }
        assertEquals(AppDestination.HOME, SettingsKey.toAppDestination())
    }
}
