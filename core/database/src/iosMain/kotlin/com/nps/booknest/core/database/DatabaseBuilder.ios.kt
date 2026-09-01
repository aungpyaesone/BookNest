package com.nps.booknest.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

private fun getDatabaseBuilder(): RoomDatabase.Builder<BookNestDatabase> =
    Room.databaseBuilder<BookNestDatabase>(
        name = "${documentDirectory()}/$BOOKNEST_DATABASE_NAME",
    )

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
    return requireNotNull(documentDirectory?.path)
}

actual val coreDatabaseModule =
    module {
        single { createBookNestDatabase(getDatabaseBuilder()) }
        single { get<BookNestDatabase>().bookDao() }
    }
