package com.pdf.semantic.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.pdf.semantic.data.datasource.EmbeddingDataSource
import com.pdf.semantic.data.datasource.ObjectBoxDbDataSource
import com.pdf.semantic.data.datasource.PdfFileDataSource
import com.pdf.semantic.data.datasource.TokenizerDataSource
import com.pdf.semantic.data.entity.PageEmbeddingEntity
import com.pdf.semantic.domain.model.EmbeddingStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmbedWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted params: WorkerParameters,
        private val tokenizerDataSource: TokenizerDataSource,
        private val embeddingDataSource: EmbeddingDataSource,
        private val pdfFileDataSource: PdfFileDataSource,
        private val objectBoxDbDataSource: ObjectBoxDbDataSource,
    ) : CoroutineWorker(appContext, params) {
        private suspend fun embedDocumentForRetrieval(
            title: String?,
            text: String,
        ): FloatArray {
            val input = "title: ${title ?: "\"none\""} | text: $text"
            val tokens = tokenizerDataSource.tokenize(input)
            Log.d(TOKEN_COUNT_TAG, "Token count: ${tokens.size}")
            return embeddingDataSource.embed(tokens)
        }

        override suspend fun doWork(): Result {
            val pdfId = inputData.getLong(KEY_PDF_ID, DEFAULT_PDF_ID)
            val title = inputData.getString(KEY_TITLE) ?: ""
            val internalPath = inputData.getString(KEY_INTERNAL_PATH) ?: DEFAULT_INTERNAL_PATH
            val totalPages = inputData.getInt(KEY_TOTAL_PAGES, DEFAULT_TOTAL_PAGES)

            return try {
                require(pdfId != DEFAULT_PDF_ID) { "Invalid pdfId" }
                require(internalPath != DEFAULT_INTERNAL_PATH) { "Invalid internalPath" }
                require(totalPages != DEFAULT_TOTAL_PAGES) { "Invalid totalPages" }

                val parsedPdfSlides = pdfFileDataSource.parsePdfByInternalPath(internalPath)

                objectBoxDbDataSource.updatePdfStatus(
                    pdfId = pdfId,
                    newProcessedPages = 0,
                    newStatus = EmbeddingStatus.IN_PROGRESS,
                )

                for (parsedSlide in parsedPdfSlides) {
                    val slideEmbeddingVector =
                        embedDocumentForRetrieval(
                            title = title.ifBlank { null },
                            text = parsedSlide.content,
                        )

                    objectBoxDbDataSource.putPageEmbedding(
                        pdfId = pdfId,
                        pageEmbedding =
                            PageEmbeddingEntity(
                                pageNumber = parsedSlide.slideNumber,
                                embeddingVector = slideEmbeddingVector,
                            ),
                    )

                    if (parsedSlide.slideNumber % UPDATE_STATUS_STEP == 0) {
                        objectBoxDbDataSource.updatePdfStatus(
                            pdfId = pdfId,
                            newProcessedPages = parsedSlide.slideNumber,
                            newStatus = EmbeddingStatus.IN_PROGRESS,
                        )
                    }
                }

                objectBoxDbDataSource.updatePdfStatus(
                    pdfId = pdfId,
                    newProcessedPages = totalPages,
                    newStatus = EmbeddingStatus.COMPLETE,
                )

                Result.success()
            } catch (throwable: Throwable) {
                Log.e(TAG, "Error embedding document", throwable)

                objectBoxDbDataSource.updatePdfStatus(
                    pdfId = pdfId,
                    newProcessedPages = 0,
                    newStatus = EmbeddingStatus.FAIL,
                )

                Result.failure()
            }
        }

        override suspend fun getForegroundInfo(): ForegroundInfo = super.getForegroundInfo()

        companion object {
            private const val TAG = "EmbedWorker"
            private const val TOKEN_COUNT_TAG = "TokenCount"
            private const val DEFAULT_PDF_ID = -1L
            private const val DEFAULT_INTERNAL_PATH = ""
            private const val DEFAULT_TOTAL_PAGES = -1
            const val KEY_PDF_ID = "pdf_id"
            const val KEY_TITLE = "title"
            const val KEY_INTERNAL_PATH = "internal_path"
            const val KEY_TOTAL_PAGES = "total_pages"
            private const val UPDATE_STATUS_STEP = 10
        }
    }
