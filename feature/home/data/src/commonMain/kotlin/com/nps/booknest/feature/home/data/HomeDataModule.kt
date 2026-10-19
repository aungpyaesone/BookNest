package com.nps.booknest.feature.home.data

import com.nps.booknest.feature.home.domain.HomeLocalDataSource
import com.nps.booknest.feature.home.domain.HomeRemoteDataSource
import com.nps.booknest.feature.home.domain.HomeRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val homeDataModule =
    module {
        singleOf(::KtorHomeDataSource) bind HomeRemoteDataSource::class
        singleOf(::RoomHomeDataSource) bind HomeLocalDataSource::class
        singleOf(::OfflineFirstHomeRepository) bind HomeRepository::class
    }
