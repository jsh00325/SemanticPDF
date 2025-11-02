package com.pdf.semantic.data.repositoryImpl

import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.pdf.semantic.data.datasource.EmbeddingDataSource
import com.pdf.semantic.data.datasource.TokenizerDataSource
import com.pdf.semantic.data.worker.EmbedWorker
import com.pdf.semantic.domain.repository.EmbeddingRepository
import java.text.NumberFormat
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

@Singleton
class EmbeddingRepositoryImpl
    @Inject
    constructor(
        private val tokenizerDataSource: TokenizerDataSource,
        private val embeddingDataSource: EmbeddingDataSource,
        private val workManager: WorkManager,
    ) : EmbeddingRepository {
        override suspend fun embedQueryForRetrieval(query: String): FloatArray =
            embedInput("task: search result | query: $query")

        override suspend fun embedDocumentForRetrieval(
            title: String?,
            text: String,
        ): FloatArray = embedInput("title: ${title ?: "\"none\""} | text: $text")

        private suspend fun embedInput(input: String): FloatArray {
            var tokens: LongArray
            val tokenizeTime =
                measureTimeMillis {
                    tokens = tokenizerDataSource.tokenize(input)
                }

            var semanticVector: FloatArray
            val embeddingTime =
                measureTimeMillis {
                    semanticVector = embeddingDataSource.embed(tokens)
                }

            logExecutionTime(tokenizeTime, embeddingTime)

            return semanticVector
        }

        private fun logExecutionTime(
            tokenizeTime: Long,
            embeddingTime: Long,
        ) {
            val totalTime = tokenizeTime + embeddingTime
            val formatter = NumberFormat.getInstance()
            Log.d(
                EXECUTION_TIME_TAG,
                "Tokenize time:\t${formatter.format(tokenizeTime)}ms\n" +
                    "Embedding time:\t${formatter.format(embeddingTime)}ms\n" +
                    " -> Total time:\t${formatter.format(totalTime)}ms",
            )
        }

        override suspend fun scheduleEmbedding(
            pdfId: Long,
            pdfTitle: String,
            internalPath: String,
            totalPages: Int,
        ) {
            val inputData =
                Data
                    .Builder()
                    .putLong(EmbedWorker.KEY_PDF_ID, pdfId)
                    .putString(EmbedWorker.KEY_TITLE, pdfTitle)
                    .putString(EmbedWorker.KEY_INTERNAL_PATH, internalPath)
                    .putInt(EmbedWorker.KEY_TOTAL_PAGES, totalPages)
                    .build()

            val embedWorkRequest =
                OneTimeWorkRequestBuilder<EmbedWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(inputData)
                    .build()

            workManager.enqueueUniqueWork(
                uniqueWorkName = "embedding-work",
                existingWorkPolicy = ExistingWorkPolicy.APPEND,
                request = embedWorkRequest,
            )
        }

        companion object {
            private const val EXECUTION_TIME_TAG = "ModelExecutionTime"
        }
    }
