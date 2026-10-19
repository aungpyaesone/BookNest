package com.nps.booknest.feature.library.presentation.overview

private const val COVER_ORIGIN = "https://booknest-api-3jl3.onrender.com/images/figma"

// UI-only sample metadata never enters Room and grants no access to book content.
internal fun libraryOverviewBooks() =
    listOf(
        LibraryBookUi(
            "atomic",
            "Atomic Habits",
            "James Clear",
            "$COVER_ORIGIN/atomic-habits.png",
            8,
            progress = 38,
            chapter = 5,
            currentPage = 120,
            pageCount = 320,
            isReading = true,
            isSaved = true,
            isDownloaded = true,
            noteCount = 2,
            category = LibraryCategory.SELF_HELP,
            chapterCount = 20,
            lastReadOrder = 0,
        ),
        LibraryBookUi(
            "psychology",
            "The Psychology of Money",
            "Morgan Housel",
            "$COVER_ORIGIN/psychology-of-money.png",
            5,
            progress = 72,
            chapter = 8,
            currentPage = 184,
            pageCount = 256,
            isReading = true,
            isSaved = true,
            noteCount = 1,
            category = LibraryCategory.FINANCE,
            chapterCount = 18,
            lastReadOrder = 1,
        ),
        LibraryBookUi(
            "rich-dad",
            "Rich Dad Poor Dad",
            "Robert T. Kiyosaki",
            "$COVER_ORIGIN/rich-dad-poor-dad.png",
            12,
            progress = 45,
            chapter = 4,
            currentPage = 151,
            category = LibraryCategory.BUSINESS,
            chapterCount = 12,
            lastReadOrder = 2,
            pageCount = 336,
            isReading = true,
        ),
        LibraryBookUi(
            "ikigai",
            "Ikigai",
            "Hector Garcia",
            "$COVER_ORIGIN/ikigai.png",
            2,
            progress = 100,
            category = LibraryCategory.LIFESTYLE,
            chapterCount = 16,
            lastReadOrder = 3,
            isSaved = true,
            isFinished = true,
            isDownloaded = true,
        ),
    ) + libraryAdditionalBooks()

private fun libraryAdditionalBooks() =
    listOf(
        LibraryBookUi(
            "dune",
            "Dune",
            "Frank Herbert",
            "$COVER_ORIGIN/dune.png",
            14,
            progress = 100,
            isFinished = true,
            category = LibraryCategory.SCIENCE_FICTION,
            chapterCount = 48,
            lastReadOrder = 4,
        ),
        LibraryBookUi(
            "midnight",
            "The Midnight Library",
            "Matt Haig",
            "$COVER_ORIGIN/midnight-library.png",
            10,
            isDownloaded = true,
            category = LibraryCategory.FICTION,
            chapterCount = 24,
            lastReadOrder = 5,
        ),
    )

internal val librarySummaryCounts: Map<LibraryTab, Int> =
    linkedMapOf(
        LibraryTab.ALL to 32,
        LibraryTab.READING to 6,
        LibraryTab.SAVED to 8,
        LibraryTab.FINISHED to 12,
        LibraryTab.DOWNLOADS to 4,
    )
