package com.pdf.semantic.domain.repository

interface EmbeddingRepository {
    suspend fun embedQueryForRetrieval(query: String): FloatArray

    suspend fun embedDocumentForRetrieval(
        title: String?,
        text: String,
    ): FloatArray
}
