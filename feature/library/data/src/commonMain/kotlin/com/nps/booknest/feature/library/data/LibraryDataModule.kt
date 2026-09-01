package com.nps.booknest.feature.library.data

import com.nps.booknest.feature.library.domain.BookLocalDataSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val libraryDataModule =
    module {
        singleOf(::RoomBookDataSource) { bind<BookLocalDataSource>() }
    }
