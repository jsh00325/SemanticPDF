package com.pdf.semantic.domain.usecase.globalsearch

import com.pdf.semantic.domain.model.SearchResult
import com.pdf.semantic.domain.repository.EmbeddingRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import com.pdf.semantic.domain.repository.VectorSearchRepository
import javax.inject.Inject

class SearchGlobalUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
        private val vectorSearchRepository: VectorSearchRepository,
        private val embeddingRepository: EmbeddingRepository,
    ) {
        suspend operator fun invoke(
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

            val embeddedPdfIds = pdfMetadataRepository.getAllEmbeddedPdfIds()

            return vectorSearchRepository.searchGlobalSimilaritySlides(
                pdfIds = embeddedPdfIds,
                queryVector = queryVector,
                topK = 10,
            )
        }
    }
