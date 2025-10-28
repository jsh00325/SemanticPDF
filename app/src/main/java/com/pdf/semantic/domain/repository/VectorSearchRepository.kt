package com.pdf.semantic.domain.repository

import com.pdf.semantic.domain.model.SearchResult

interface VectorSearchRepository {
    suspend fun searchSimilaritySlides(
        queryVector: FloatArray,
        pdfIds: List<Long>,
        topK: Int,
    ): List<SearchResult>
}
