package com.pdf.semantic.data.mapper

import com.pdf.semantic.data.dto.PageEmbeddingSearchResult
import com.pdf.semantic.domain.model.SearchResult

object SearchResultMapper {
    fun PageEmbeddingSearchResult.toSearchResult(): SearchResult =
        SearchResult(
            pdfId = entity.pdfDocument.targetId,
            slideNumber = entity.pageNumber,
            similarityScore = score,
        )
}
