package com.nps.booknest.di

import com.nps.booknest.appPresentationModule
import com.nps.booknest.core.database.coreDatabaseModule
import com.nps.booknest.core.settings.coreSettingsModule
import com.nps.booknest.feature.home.presentation.homePresentationModule
import com.nps.booknest.feature.library.data.libraryDataModule
import com.nps.booknest.feature.library.presentation.libraryPresentationModule
import com.nps.booknest.feature.reader.data.readerDataModule
import com.nps.booknest.feature.reader.presentation.readerPresentationModule
import com.nps.booknest.feature.settings.presentation.settingsPresentationModule

val appModules =
    listOf(
        coreDatabaseModule,
        coreSettingsModule,
        appPresentationModule,
        homePresentationModule,
        libraryDataModule,
        libraryPresentationModule,
        readerDataModule,
        readerPresentationModule,
        settingsPresentationModule,
    )
