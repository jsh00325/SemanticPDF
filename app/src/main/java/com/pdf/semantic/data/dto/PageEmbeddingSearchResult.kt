package com.pdf.semantic.data.dto

import com.pdf.semantic.data.entity.PageEmbeddingEntity

data class PageEmbeddingSearchResult(
    val entity: PageEmbeddingEntity,
    val score: Double,
)
