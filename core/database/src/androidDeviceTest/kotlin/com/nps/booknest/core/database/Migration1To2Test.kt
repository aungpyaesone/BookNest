package com.nps.booknest.core.database

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration1To2Test {
    @Test
    fun migrationPreservesBooksAndCreatesProgressTable() {
        val connection = BundledSQLiteDriver().open(":memory:")
        try {
            connection.execSQL(
                "CREATE TABLE books (book_id TEXT NOT NULL, title TEXT NOT NULL, author TEXT NOT NULL, PRIMARY KEY(book_id))",
            )
            connection.execSQL("INSERT INTO books VALUES ('existing', 'Existing Book', 'Existing Author')")

            Migration1To2.migrate(connection)

            connection.prepare("SELECT COUNT(*) FROM books WHERE book_id = 'existing'").use { statement ->
                assertTrue(statement.step())
                assertEquals(1L, statement.getLong(0))
            }
            connection.prepare("SELECT COUNT(*) FROM books WHERE book_id = '$DEMO_BOOK_ID'").use { statement ->
                assertTrue(statement.step())
                assertEquals(1L, statement.getLong(0))
            }
            connection.prepare("SELECT COUNT(*) FROM reading_progress").use { statement ->
                assertTrue(statement.step())
                assertEquals(0L, statement.getLong(0))
            }
        } finally {
            connection.close()
        }
    }
}
