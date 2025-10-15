package com.pdf.semantic.domain.usecase

import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.model.Slide
import com.pdf.semantic.domain.repository.EmbeddingRepository
import javax.inject.Inject
import kotlin.math.sqrt

class SearchRelatedSlideUseCase
    @Inject
    constructor(
        private val embeddingRepository: EmbeddingRepository,
    ) {
        suspend operator fun invoke(
            query: String,
            pdfDocument: PdfDocument,
            topN: Int = 5,
        ): List<Slide> {
            val queryEmbedding = embeddingRepository.embedQueryForRetrieval(query)

            // TODO: 추후 Vector DB를 통한 검색 기능 구현

            val slideEmbeddings =
                pdfDocument.slides.map { slide ->
                    embeddingRepository.embedDocumentForRetrieval(
                        title = null,
                        text = slide.content,
                    )
                }

            val sortedSlides =
                pdfDocument.slides
                    .zip(slideEmbeddings)
                    .map { (slide, slideEmbedding) ->
                        slide.copy(similarity = cosineSimilarity(queryEmbedding, slideEmbedding))
                    }.sortedByDescending { it.similarity }

            return sortedSlides.take(topN)
        }
    }

private fun cosineSimilarity(
    vectorA: FloatArray,
    vectorB: FloatArray,
): Float {
    var dotProduct = 0.0f
    var normA = 0.0f
    var normB = 0.0f
    for (i in vectorA.indices) {
        dotProduct += vectorA[i] * vectorB[i]
        normA += vectorA[i] * vectorA[i]
        normB += vectorB[i] * vectorB[i]
    }

    val denominator = sqrt(normA) * sqrt(normB)
    if (denominator == 0.0f) {
        return 0.0f
    }

    return dotProduct / denominator
}
