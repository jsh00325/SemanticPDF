package com.pdf.semantic.data.repositoryImpl

import android.util.Log
import com.pdf.semantic.data.datasource.EmbeddingDataSource
import com.pdf.semantic.data.datasource.TokenizerDataSource
import com.pdf.semantic.domain.repository.EmbeddingRepository
import java.text.NumberFormat
import javax.inject.Inject
import kotlin.system.measureTimeMillis

class EmbeddingRepositoryImpl
    @Inject
    constructor(
        private val tokenizerDataSource: TokenizerDataSource,
        private val embeddingDataSource: EmbeddingDataSource,
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

        companion object {
            private const val EXECUTION_TIME_TAG = "ModelExecutionTime"
        }
    }
