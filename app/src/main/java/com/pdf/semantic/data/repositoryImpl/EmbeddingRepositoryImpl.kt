package com.pdf.semantic.data.repositoryImpl

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.pdf.semantic.data.datasource.EmbeddingDataSource
import com.pdf.semantic.data.datasource.TokenizerDataSource
import com.pdf.semantic.data.worker.EmbedWorker
import com.pdf.semantic.domain.repository.EmbeddingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmbeddingRepositoryImpl
    @Inject
    constructor(
        private val tokenizerDataSource: TokenizerDataSource,
        private val embeddingDataSource: EmbeddingDataSource,
        private val workManager: WorkManager,
    ) : EmbeddingRepository {
        override suspend fun embedQueryForRetrieval(query: String): FloatArray {
            val input = "task: search result | query: $query"
            val tokens = tokenizerDataSource.tokenize(input)
            return embeddingDataSource.embed(tokens)
        }

        override fun scheduleEmbedding(
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
    }
