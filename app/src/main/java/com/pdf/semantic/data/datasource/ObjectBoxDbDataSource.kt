package com.pdf.semantic.data.datasource

import com.pdf.semantic.data.dto.PageEmbeddingSearchResult
import com.pdf.semantic.data.entity.PageEmbeddingEntity
import com.pdf.semantic.data.entity.PageEmbeddingEntity_
import com.pdf.semantic.data.entity.PdfDocumentEntity
import com.pdf.semantic.domain.model.EmbeddingStatus
import io.objectbox.BoxStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObjectBoxDbDataSource
    @Inject
    constructor(
        boxStore: BoxStore,
    ) {
        private val pdfDocumentBox = boxStore.boxFor(PdfDocumentEntity::class.java)
        private val pageEmbeddingBox = boxStore.boxFor(PageEmbeddingEntity::class.java)

        suspend fun putPdfDocument(pdfDocument: PdfDocumentEntity) =
            withContext(Dispatchers.IO) {
                pdfDocumentBox.put(pdfDocument)
            }

        suspend fun putPageEmbedding(
            pdfId: Long,
            pageEmbedding: PageEmbeddingEntity,
        ) = withContext(Dispatchers.IO) {
            val pdfDocument = pdfDocumentBox.get(pdfId)
            pageEmbedding.pdfDocument.target = pdfDocument
            pdfDocument.pageEmbeddings.add(pageEmbedding)
            pdfDocumentBox.put(pdfDocument)
            pageEmbeddingBox.put(pageEmbedding)
        }

        suspend fun getPdfDocumentById(pdfId: Long): PdfDocumentEntity =
            withContext(Dispatchers.IO) {
                pdfDocumentBox.get(pdfId)
            }

        suspend fun getAllPdfDocuments(): List<PdfDocumentEntity> =
            withContext(Dispatchers.IO) {
                pdfDocumentBox.all
            }

        suspend fun searchSimilarityPageEmbedding(
            queryVector: FloatArray,
            topK: Int = 100,
        ): List<PageEmbeddingSearchResult> =
            withContext(Dispatchers.IO) {
                val query =
                    pageEmbeddingBox
                        .query(
                            PageEmbeddingEntity_.embeddingVector.nearestNeighbors(
                                queryVector,
                                topK,
                            ),
                        ).build()

                val results = query.findWithScores()
                query.close()

                results.map {
                    PageEmbeddingSearchResult(
                        entity = it.get(),
                        score = it.score,
                    )
                }
            }

        suspend fun updatePdfStatus(
            pdfId: Long,
            newProcessedPages: Int,
            newStatus: EmbeddingStatus,
        ) = withContext(Dispatchers.IO) {
            val targetPdfDocument = pdfDocumentBox.get(pdfId)
            targetPdfDocument.processedPages = newProcessedPages
            targetPdfDocument.embeddingStatus = newStatus
            pdfDocumentBox.put(targetPdfDocument)
        }

        suspend fun deletePdfDocument(pdfId: Long) =
            withContext(Dispatchers.IO) {
                val targetPdfDocument = pdfDocumentBox.get(pdfId)
                pageEmbeddingBox.remove(targetPdfDocument.pageEmbeddings)
                pdfDocumentBox.remove(targetPdfDocument)
            }

        fun observePdfDocumentById(pdfId: Long): Flow<PdfDocumentEntity> =
            pdfDocumentBox.observeById(pdfId).filterNotNull()

        fun observeAllPdfDocuments(): Flow<List<PdfDocumentEntity>> =
            pdfDocumentBox.query().build().asFlow(pdfDocumentBox)
    }
