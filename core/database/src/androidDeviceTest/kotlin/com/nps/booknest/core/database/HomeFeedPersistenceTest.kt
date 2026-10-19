package com.nps.booknest.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.platform.app.InstrumentationRegistry
import com.nps.booknest.core.database.home.HomeFeedEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeFeedPersistenceTest {
    @Test
    fun versionTwoDatabaseMigratesAndSnapshotSurvivesReopen() =
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val name = "home-migration-test.db"
            context.deleteDatabase(name)
            val file = context.getDatabasePath(name)
            file.parentFile?.mkdirs()
            val connection = BundledSQLiteDriver().open(file.absolutePath)
            try {
                connection.execSQL(
                    "CREATE TABLE books (book_id TEXT NOT NULL, title TEXT NOT NULL, author TEXT NOT NULL, PRIMARY KEY(book_id))",
                )
                connection.execSQL("INSERT INTO books VALUES ('existing', 'Existing', 'Author')")
                Migration1To2.migrate(connection)
                connection.execSQL("INSERT INTO reading_progress VALUES ('existing', 'chapter', 'paragraph', 0.5)")
                connection.execSQL("PRAGMA user_version = 2")
            } finally {
                connection.close()
            }
            var database = createBookNestDatabase(Room.databaseBuilder<BookNestDatabase>(context, name))
            try {
                val snapshot = HomeFeedEntity(payload = "{\"banners\":[],\"categories\":[],\"popularBooks\":[]}")
                database.homeFeedDao().upsertHome(snapshot)
                database.close()
                database = createBookNestDatabase(Room.databaseBuilder<BookNestDatabase>(context, name))
                assertEquals(snapshot, database.homeFeedDao().observeHome().first())
                assertEquals(
                    "Existing",
                    database
                        .bookDao()
                        .observeBook("existing")
                        .first()
                        ?.title,
                )
                assertEquals(
                    0.5f,
                    database
                        .readingProgressDao()
                        .observeProgress("existing")
                        .first()
                        ?.overallProgress,
                )
            } finally {
                database.close()
                context.deleteDatabase(name)
            }
        }
}
