package com.nps.booknest.core.database

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.nps.booknest.core.database.explore.CatalogSnapshotEntity
import com.nps.booknest.core.database.explore.ExploreTaxonomyEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ExploreCachePersistenceTest {
    @Test
    fun taxonomyAndIndependentQuerySnapshotsSurviveDatabaseReopen() =
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val name = "explore-persistence-test.db"
            context.deleteDatabase(name)
            var database = createBookNestDatabase(Room.databaseBuilder<BookNestDatabase>(context, name))
            val taxonomy = ExploreTaxonomyEntity(payload = "{\"categories\":[]}")
            val popular = CatalogSnapshotEntity("1:POPULAR:20", "popular page snapshot")
            val price = CatalogSnapshotEntity("1:PRICE:20", "price page snapshot")
            try {
                database.exploreCacheDao().upsertCategories(taxonomy)
                database.exploreCacheDao().upsertCatalog(popular)
                database.exploreCacheDao().upsertCatalog(price)
                database.close()
                database = createBookNestDatabase(Room.databaseBuilder<BookNestDatabase>(context, name))
                assertEquals(taxonomy, database.exploreCacheDao().observeCategories().first())
                assertEquals(popular, database.exploreCacheDao().getCatalog(popular.queryKey))
                assertEquals(price, database.exploreCacheDao().getCatalog(price.queryKey))
            } finally {
                database.close()
                context.deleteDatabase(name)
            }
        }
}
