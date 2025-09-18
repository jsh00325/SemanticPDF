package com.pdf.semantic.domain.repository

interface EmbeddingRepository {
    suspend fun getSematicVector(text: String): FloatArray
}
