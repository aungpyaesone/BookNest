package com.nps.booknest.feature.reader.domain

data class ReadableBook(
    val id: String,
    val title: String,
    val author: String,
    val chapters: List<ReadableChapter>,
)

data class ReadableChapter(
    val id: String,
    val title: String,
    val paragraphs: List<ReadableParagraph>,
)

data class ReadableParagraph(
    val id: String,
    val text: String,
)
