package com.nps.booknest.di

import com.nps.booknest.core.database.coreDatabaseModule
import com.nps.booknest.feature.library.data.libraryDataModule
import com.nps.booknest.feature.library.presentation.libraryPresentationModule

val appModules =
    listOf(
        coreDatabaseModule,
        libraryDataModule,
        libraryPresentationModule,
    )
