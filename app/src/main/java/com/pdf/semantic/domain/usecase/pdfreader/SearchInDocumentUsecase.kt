package com.pdf.semantic.domain.usecase.pdfreader

import com.pdf.semantic.domain.model.SearchResult
import com.pdf.semantic.domain.repository.EmbeddingRepository
import com.pdf.semantic.domain.repository.VectorSearchRepository
import javax.inject.Inject

class SearchInDocumentUsecase
    @Inject
    constructor(
        private val vectorSearchRepository: VectorSearchRepository,
        private val embeddingRepository: EmbeddingRepository,
    ) {
        suspend operator fun invoke(
            pdfId: Long,
            query: String,
            useExpandQuery: Boolean = false,
        ): List<SearchResult> {
            val searchQuery =
                if (useExpandQuery) {
                    vectorSearchRepository.expandQuery(
                        query,
                    )
                } else {
                    query
                }
            val queryVector = embeddingRepository.embedQueryForRetrieval(searchQuery)

            return vectorSearchRepository.searchSinglePdfSimilaritySlides(
                pdfId = pdfId,
                queryVector = queryVector,
                topK = 10,
            )
        }
    }
