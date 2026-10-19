package com.nps.booknest.feature.explore.data

import com.nps.booknest.feature.explore.domain.ExploreLocalDataSource
import com.nps.booknest.feature.explore.domain.ExploreRemoteDataSource
import com.nps.booknest.feature.explore.domain.ExploreRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val exploreDataModule =
    module {
        singleOf(::KtorExploreDataSource) bind ExploreRemoteDataSource::class
        singleOf(::RoomExploreDataSource) bind ExploreLocalDataSource::class
        singleOf(::OfflineFirstExploreRepository) bind ExploreRepository::class
    }
