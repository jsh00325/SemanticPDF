package com.pdf.semantic.domain.repository

import com.pdf.semantic.domain.model.SearchResult

interface VectorSearchRepository {
    suspend fun searchGlobalSimilaritySlides(
        queryVector: FloatArray,
        pdfIds: List<Long>,
        topK: Int,
    ): List<SearchResult>

    suspend fun searchSinglePdfSimilaritySlides(
        queryVector: FloatArray,
        pdfId: Long,
        topK: Int,
    ): List<SearchResult>

    suspend fun insertEmbeddingVector(
        pdfId: Long,
        pageNumber: Int,
        embeddingVector: FloatArray,
    )

    suspend fun expandQuery(query: String): String
}
