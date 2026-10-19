package com.nps.booknest.core.database

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Migration3To4Test {
    @Test
    fun migrationPreservesLibraryReadingAndHomeAndAddsIndependentQueryCaches() {
        val connection = BundledSQLiteDriver().open(":memory:")
        try {
            connection.execSQL(
                "CREATE TABLE books (book_id TEXT NOT NULL, title TEXT NOT NULL, author TEXT NOT NULL, PRIMARY KEY(book_id))",
            )
            connection.execSQL("INSERT INTO books VALUES ('existing', 'Existing', 'Author')")
            Migration1To2.migrate(connection)
            connection.execSQL("INSERT INTO reading_progress VALUES ('existing', 'chapter', 'paragraph', 0.5)")
            Migration2To3.migrate(connection)
            connection.execSQL("INSERT INTO home_feeds VALUES (1, 'home payload')")
            Migration3To4.migrate(connection)
            connection.prepare("SELECT title FROM books WHERE book_id = 'existing'").use {
                assertTrue(it.step())
                assertEquals("Existing", it.getText(0))
            }
            connection.prepare("SELECT overall_progress FROM reading_progress WHERE book_id = 'existing'").use {
                assertTrue(it.step())
                assertEquals(0.5, it.getDouble(0), 0.0)
            }
            connection.prepare("SELECT payload FROM home_feeds").use {
                assertTrue(it.step())
                assertEquals("home payload", it.getText(0))
            }
            connection.execSQL("INSERT INTO explore_taxonomy VALUES (1, 'taxonomy')")
            connection.execSQL("INSERT INTO catalog_snapshots VALUES ('1:POPULAR:20', 'popular')")
            connection.execSQL("INSERT INTO catalog_snapshots VALUES ('1:PRICE:20', 'price')")
            connection.prepare("SELECT COUNT(*) FROM catalog_snapshots").use {
                assertTrue(it.step())
                assertEquals(2L, it.getLong(0))
            }
        } finally {
            connection.close()
        }
    }
}
