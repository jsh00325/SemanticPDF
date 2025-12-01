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
import kotlinx.coroutines.CancellationException

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
            return embeddingDataSource.embed(tokens)
        }

        private suspend fun checkFileExist(pdfId: Long): Boolean =
            objectBoxDbDataSource.getPdfDocumentById(pdfId) != null

        private suspend fun embedAndStorePdfPages(
            pdfId: Long,
            title: String,
            internalPath: String,
            totalPages: Int,
        ) {
            val currentPdfDocument = objectBoxDbDataSource.getPdfDocumentById(pdfId)

            if (currentPdfDocument == null) {
                Log.w(TAG, LOG_MSG_PDF_NOT_FOUND.format(pdfId))
                return
            }
            if (currentPdfDocument.embeddingStatus == EmbeddingStatus.COMPLETE) {
                Log.d(TAG, "Work for pdfId $pdfId already complete. Skipping.")
                return
            }

            val lastProcessedPage = currentPdfDocument.processedPages

            if (currentPdfDocument.embeddingStatus == EmbeddingStatus.FAIL) {
                objectBoxDbDataSource.updatePdfStatus(
                    pdfId = pdfId,
                    newProcessedPages = lastProcessedPage,
                    newStatus = EmbeddingStatus.IN_PROGRESS,
                )
            }

            val parsedPdfSlides =
                pdfFileDataSource.parsePdfByInternalPath(
                    internalPath = internalPath,
                    startPage = lastProcessedPage + 1,
                )

            parsedPdfSlides.chunked(UPDATE_STATUS_STEP).forEach { chunk ->
                if (!checkFileExist(pdfId)) {
                    Log.w(TAG, LOG_MSG_PDF_NOT_FOUND.format(pdfId))
                    return
                }

                val pageEmbeddings =
                    chunk.map { parsedSlide ->
                        val slideEmbeddingVector =
                            embedDocumentForRetrieval(
                                title = title.ifBlank { null },
                                text = parsedSlide.content,
                            )

                        PageEmbeddingEntity(
                            pageNumber = parsedSlide.slideNumber,
                            embeddingVector = slideEmbeddingVector,
                        )
                    }

                objectBoxDbDataSource.insertEmbeddingChunkAndUpdateStatus(
                    pdfId = pdfId,
                    pageEmbeddings = pageEmbeddings,
                    lastPageInChunk = chunk.last().slideNumber,
                )
            }

            objectBoxDbDataSource.updatePdfStatus(
                pdfId = pdfId,
                newProcessedPages = totalPages,
                newStatus = EmbeddingStatus.COMPLETE,
            )
        }

        override suspend fun doWork(): Result {
            val pdfId = inputData.getLong(KEY_PDF_ID, -1)
            if (pdfId == -1L) {
                Log.e(TAG, "Invalid pdfId. Work will not be retried.")
                return Result.failure()
            }

            val title = inputData.getString(KEY_TITLE) ?: ""
            val internalPath = inputData.getString(KEY_INTERNAL_PATH) ?: ""

            val totalPages = inputData.getInt(KEY_TOTAL_PAGES, -1)
            if (totalPages == -1) {
                Log.e(TAG, "Invalid totalPages. Work will not be retried.")
                return Result.failure()
            }

            return try {
                embedAndStorePdfPages(pdfId, title, internalPath, totalPages)
                Result.success()
            } catch (e: CancellationException) {
                Log.w(TAG, "Work for pdfId $pdfId was cancelled. Retrying.", e)
                Result.retry()
            } catch (tr: Throwable) {
                Log.e(TAG, "Error occurred for pdfId $pdfId. Setting status to FAIL.", tr)
                objectBoxDbDataSource.rollbackAndSetFailStatus(pdfId)
                Result.failure()
            }
        }

        override suspend fun getForegroundInfo(): ForegroundInfo = super.getForegroundInfo()

        companion object {
            private const val TAG = "EmbedWorker"
            const val KEY_PDF_ID = "pdf_id"
            const val KEY_TITLE = "title"
            const val KEY_INTERNAL_PATH = "internal_path"
            const val KEY_TOTAL_PAGES = "total_pages"
            private const val UPDATE_STATUS_STEP = 1 // TODO: 디버깅 용으로 줄임 -> 이후 10으로 바꾸기
            private const val LOG_MSG_PDF_NOT_FOUND =
                "PDF document with id %d not found. Assuming deleted. Work stopped."
        }
    }
