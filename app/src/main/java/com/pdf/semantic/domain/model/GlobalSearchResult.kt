package com.pdf.semantic.domain.model

data class GlobalSearchResult(
    val pdfId: Long,
    val pdfTitle: String,
    val totalPages: Int,
    val slideNumber: Int,
    val similarityScore: Double,
)
