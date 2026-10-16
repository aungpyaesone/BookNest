package com.nps.booknest.feature.reader.data

import com.nps.booknest.feature.reader.domain.BookContentDataSource
import com.nps.booknest.feature.reader.domain.ReadingProgressLocalDataSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val readerDataModule =
    module {
        singleOf(::BundledBookContentDataSource) { bind<BookContentDataSource>() }
        singleOf(::RoomReadingProgressDataSource) { bind<ReadingProgressLocalDataSource>() }
    }
