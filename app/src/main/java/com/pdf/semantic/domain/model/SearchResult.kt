package com.pdf.semantic.domain.model

data class SearchResult(
    val pdfId: Long,
    val slideNumber: Int,
    val similarityScore: Double,
)
