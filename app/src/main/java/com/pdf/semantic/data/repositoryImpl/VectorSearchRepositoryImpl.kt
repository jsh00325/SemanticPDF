package com.pdf.semantic.data.repositoryImpl

import com.pdf.semantic.data.datasource.ObjectBoxDbDataSource
import com.pdf.semantic.data.entity.PageEmbeddingEntity
import com.pdf.semantic.data.mapper.SearchResultMapper.toSearchResult
import com.pdf.semantic.domain.model.SearchResult
import com.pdf.semantic.domain.repository.VectorSearchRepository
import javax.inject.Inject

class VectorSearchRepositoryImpl
    @Inject
    constructor(
        private val objectBoxDbDataSource: ObjectBoxDbDataSource,
    ) : VectorSearchRepository {
        private suspend fun searchSimilaritySlides(
            queryVector: FloatArray,
            pdfIds: List<Long>,
            topK: Int,
        ): List<SearchResult> =
            objectBoxDbDataSource
                .searchSimilarityPageEmbedding(queryVector)
                .filter { it.entity.pdfDocument.targetId in pdfIds }
                .take(topK)
                .map { pageEmbeddingSearchResult ->
                    pageEmbeddingSearchResult.toSearchResult()
                }

        override suspend fun searchGlobalSimilaritySlides(
            queryVector: FloatArray,
            pdfIds: List<Long>,
            topK: Int,
        ): List<SearchResult> = searchSimilaritySlides(queryVector, pdfIds, topK)

        override suspend fun searchSinglePdfSimilaritySlides(
            queryVector: FloatArray,
            pdfId: Long,
            topK: Int,
        ): List<SearchResult> = searchSimilaritySlides(queryVector, listOf(pdfId), topK)

        override suspend fun insertEmbeddingVector(
            pdfId: Long,
            pageNumber: Int,
            embeddingVector: FloatArray,
        ) {
            objectBoxDbDataSource.putPageEmbedding(
                pdfId = pdfId,
                pageEmbedding =
                    PageEmbeddingEntity(
                        pageNumber = pageNumber,
                        embeddingVector = embeddingVector,
                    ),
            )
        }
    }
