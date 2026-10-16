package com.nps.booknest.feature.reader.data

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.nps.booknest.core.database.DEMO_BOOK_ID
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.domain.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class BundledBookContentDataSourceTest {
    @Test
    fun `demo book contains three readable chapters`() =
        runTest {
            val result = BundledBookContentDataSource().getBook(DEMO_BOOK_ID)

            assertThat(result).isInstanceOf(Result.Success::class)
            val book = (result as Result.Success).data
            assertThat(book.chapters).hasSize(3)
            book.chapters.forEach { chapter -> assertThat(chapter.paragraphs).hasSize(12) }
        }

    @Test
    fun `unknown book returns not found`() =
        runTest {
            val result = BundledBookContentDataSource().getBook("missing")

            assertThat(result).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
        }
}
