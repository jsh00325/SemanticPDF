package com.pdf.semantic.domain.model

import java.util.Date

data class PdfItem(
    val id: Long,
    val title: String,
    val createdTime: Date,
    val status: EmbeddingStatus,
    val totalPages: Int,
    val progressedPages: Int,
)
