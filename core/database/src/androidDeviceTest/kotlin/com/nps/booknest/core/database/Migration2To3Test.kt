package com.nps.booknest.core.database

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration2To3Test {
    @Test
    fun migrationPreservesBooksAndProgressAndCreatesSnapshot() {
        val connection = BundledSQLiteDriver().open(":memory:")
        try {
            connection.execSQL(
                "CREATE TABLE books (book_id TEXT NOT NULL, title TEXT NOT NULL, author TEXT NOT NULL, PRIMARY KEY(book_id))",
            )
            connection.execSQL("INSERT INTO books VALUES ('existing', 'Existing', 'Author')")
            Migration1To2.migrate(connection)
            connection.execSQL("INSERT INTO reading_progress VALUES ('existing', 'chapter', 'paragraph', 0.5)")
            Migration2To3.migrate(connection)
            connection.prepare("SELECT title FROM books WHERE book_id = 'existing'").use {
                assertTrue(it.step())
                assertEquals("Existing", it.getText(0))
            }
            connection.prepare("SELECT overall_progress FROM reading_progress WHERE book_id = 'existing'").use {
                assertTrue(it.step())
                assertEquals(0.5, it.getDouble(0), 0.0)
            }
            connection.execSQL("INSERT INTO home_feeds VALUES (1, '{}')")
            connection.prepare("SELECT payload FROM home_feeds WHERE feed_id = 1").use {
                assertTrue(it.step())
                assertEquals("{}", it.getText(0))
            }
        } finally {
            connection.close()
        }
    }
}
