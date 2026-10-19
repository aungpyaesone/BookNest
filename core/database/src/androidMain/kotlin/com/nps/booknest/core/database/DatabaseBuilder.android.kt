package com.nps.booknest.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<BookNestDatabase> {
    val applicationContext = context.applicationContext
    val databaseFile = applicationContext.getDatabasePath(BOOKNEST_DATABASE_NAME)
    return Room.databaseBuilder<BookNestDatabase>(
        context = applicationContext,
        name = databaseFile.absolutePath,
    )
}

actual val coreDatabaseModule =
    module {
        single { createBookNestDatabase(getDatabaseBuilder(androidContext())) }
        single { get<BookNestDatabase>().bookDao() }
        single { get<BookNestDatabase>().homeFeedDao() }
        single { get<BookNestDatabase>().exploreCacheDao() }
        single { get<BookNestDatabase>().readingProgressDao() }
    }
