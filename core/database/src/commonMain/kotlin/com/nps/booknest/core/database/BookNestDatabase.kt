package com.nps.booknest.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.nps.booknest.core.database.book.BookDao
import com.nps.booknest.core.database.book.BookEntity
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module

internal const val BOOKNEST_DATABASE_NAME = "booknest.db"

@Database(
    entities = [BookEntity::class],
    version = 1,
)
@ConstructedBy(BookNestDatabaseConstructor::class)
abstract class BookNestDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}

@Suppress("KotlinNoActualForExpect")
expect object BookNestDatabaseConstructor : RoomDatabaseConstructor<BookNestDatabase> {
    override fun initialize(): BookNestDatabase
}

fun createBookNestDatabase(builder: RoomDatabase.Builder<BookNestDatabase>): BookNestDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()

expect val coreDatabaseModule: Module
