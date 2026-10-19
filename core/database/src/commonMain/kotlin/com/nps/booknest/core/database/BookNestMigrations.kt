package com.nps.booknest.core.database

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

const val DEMO_BOOK_ID = "mountain-morning"
const val DEMO_BOOK_TITLE = "တောင်တန်းနောက်က နံနက်ခင်း"
const val DEMO_BOOK_AUTHOR = "သီဟနိုင်"

object Migration1To2 : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS reading_progress (
                book_id TEXT NOT NULL,
                chapter_id TEXT NOT NULL,
                paragraph_id TEXT NOT NULL,
                overall_progress REAL NOT NULL,
                PRIMARY KEY(book_id)
            )
            """.trimIndent(),
        )
        insertDemoBook(connection)
    }
}

object DemoBookDatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        insertDemoBook(connection)
    }
}

private fun insertDemoBook(connection: SQLiteConnection) {
    connection.execSQL(
        "INSERT OR IGNORE INTO books (book_id, title, author) " +
            "VALUES ('$DEMO_BOOK_ID', '$DEMO_BOOK_TITLE', '$DEMO_BOOK_AUTHOR')",
    )
}

object Migration2To3 : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS home_feeds (feed_id INTEGER NOT NULL, payload TEXT NOT NULL, PRIMARY KEY(feed_id))",
        )
    }
}

object Migration3To4 : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS explore_taxonomy (feed_id INTEGER NOT NULL, payload TEXT NOT NULL, PRIMARY KEY(feed_id))",
        )
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS catalog_snapshots (query_key TEXT NOT NULL, payload TEXT NOT NULL, PRIMARY KEY(query_key))",
        )
    }
}
