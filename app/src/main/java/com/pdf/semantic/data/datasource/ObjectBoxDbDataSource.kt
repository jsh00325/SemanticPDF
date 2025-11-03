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
        private val boxStore: BoxStore,
    ) {
        private val pdfDocumentBox = boxStore.boxFor(PdfDocumentEntity::class.java)
        private val pageEmbeddingBox = boxStore.boxFor(PageEmbeddingEntity::class.java)

        private suspend fun <T> runInIoTx(block: () -> T): T =
            withContext(Dispatchers.IO) {
                boxStore.runInTx { block() } as T
            }

        suspend fun insertPdfDocument(pdfDocument: PdfDocumentEntity) =
            runInIoTx {
                pdfDocumentBox.put(pdfDocument)
            }

        suspend fun insertPageEmbeddingsInChunk(
            pdfId: Long,
            pageEmbeddings: List<PageEmbeddingEntity>,
        ) = runInIoTx {
            pdfDocumentBox.get(pdfId)?.also { pdfDocument ->
                pageEmbeddings.forEach { it.pdfDocument.target = pdfDocument }
                pdfDocument.pageEmbeddings.addAll(pageEmbeddings)
                pdfDocumentBox.put(pdfDocument)
            }
        }

        suspend fun getPdfDocumentById(pdfId: Long): PdfDocumentEntity? =
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
                pageEmbeddingBox
                    .query(
                        PageEmbeddingEntity_.embeddingVector.nearestNeighbors(
                            queryVector,
                            topK,
                        ),
                    ).build()
                    .use { query ->
                        val results = query.findWithScores()
                        results.map {
                            PageEmbeddingSearchResult(
                                entity = it.get(),
                                score = it.score,
                            )
                        }
                    }
            }

        suspend fun updatePdfStatus(
            pdfId: Long,
            newProcessedPages: Int,
            newStatus: EmbeddingStatus,
        ) = runInIoTx {
            pdfDocumentBox.get(pdfId)?.also {
                it.processedPages = newProcessedPages
                it.embeddingStatus = newStatus
                pdfDocumentBox.put(it)
            }
        }

        suspend fun deletePdfDocument(pdfId: Long) =
            runInIoTx {
                pdfDocumentBox.get(pdfId)?.also {
                    pageEmbeddingBox.remove(it.pageEmbeddings)
                    pdfDocumentBox.remove(it)
                }
            }

        fun observePdfDocumentById(pdfId: Long): Flow<PdfDocumentEntity> =
            pdfDocumentBox.observeById(pdfId).filterNotNull()

        fun observeAllPdfDocuments(): Flow<List<PdfDocumentEntity>> =
            pdfDocumentBox.query().build().asFlow(pdfDocumentBox)
    }
