package com.pdf.semantic.data.repositoryImpl

import com.pdf.semantic.domain.model.SearchResult
import com.pdf.semantic.domain.repository.VectorSearchRepository
import javax.inject.Inject

class VectorSearchRepositoryImpl @Inject constructor(): VectorSearchRepository {
    override suspend fun searchGlobalSimilaritySlides(
        queryVector: FloatArray,
        pdfIds: List<Long>,
        topK: Int,
    ): List<SearchResult> {
        TODO("Not yet implemented")
    }

    override suspend fun searchSinglePdfSimilaritySlides(
        queryVector: FloatArray,
        pdfId: Long,
        topK: Int,
    ): List<SearchResult> {
        TODO("Not yet implemented")
    }

    override suspend fun insertEmbeddingVector(
        pdfId: Long,
        pageNumber: Int,
        embeddingVector: FloatArray,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEmbeddingVector(pdfId: Long) {
        TODO("Not yet implemented")
    }
}
