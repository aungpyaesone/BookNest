package com.nps.booknest.feature.home.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

class HomeGridLayoutTest {
    @Test
    fun `tablet portrait has four columns in medium and expanded windows`() {
        listOf(600f, 820f, 840f, 1024f, 1180f).forEach { width ->
            assertEquals(4, homePopularBookColumns(width, width + 300f, HomeLayoutMode.EXPANDED))
            assertEquals(4, homePopularBookColumns(width, width + 300f, HomeLayoutMode.MEDIUM))
        }
    }

    @Test
    fun `landscape compact and narrow split windows preserve their rules`() {
        assertEquals(6, homePopularBookColumns(1180f, 820f, HomeLayoutMode.EXPANDED))
        assertEquals(3, homePopularBookColumns(820f, 600f, HomeLayoutMode.MEDIUM))
        assertEquals(3, homePopularBookColumns(800f, 360f, HomeLayoutMode.MEDIUM))
        assertEquals(1, homePopularBookColumns(360f, 800f, HomeLayoutMode.COMPACT))
        assertEquals(1, homePopularBookColumns(540f, 1180f, HomeLayoutMode.COMPACT))
    }
}
