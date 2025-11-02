package com.pdf.semantic.domain.repository

interface EmbeddingRepository {
    suspend fun embedQueryForRetrieval(query: String): FloatArray

    suspend fun scheduleEmbedding(
        pdfId: Long,
        pdfTitle: String,
        internalPath: String,
        totalPages: Int,
    )
}
