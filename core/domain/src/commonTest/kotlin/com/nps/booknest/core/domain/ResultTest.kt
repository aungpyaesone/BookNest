package com.nps.booknest.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResultTest {
    @Test
    fun `map transforms success data`() {
        val result = Result.Success(2).map { value -> value * 3 }

        assertEquals(Result.Success(6), result)
    }

    @Test
    fun `map preserves typed error`() {
        val result = Result.Error(DataError.Local.UNKNOWN).map { value: Int -> value * 3 }

        assertEquals(Result.Error(DataError.Local.UNKNOWN), result)
    }

    @Test
    fun `onFailure invokes action for error`() {
        var wasInvoked = false

        Result.Error(DataError.Local.DISK_FULL).onFailure { wasInvoked = true }

        assertTrue(wasInvoked)
    }
}
