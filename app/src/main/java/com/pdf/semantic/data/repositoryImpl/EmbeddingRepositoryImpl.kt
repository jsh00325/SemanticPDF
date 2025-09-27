package com.pdf.semantic.data.repositoryImpl

import android.util.Log
import com.pdf.semantic.data.datasource.EmbeddingDataSource
import com.pdf.semantic.data.datasource.TokenizerDataSource
import com.pdf.semantic.domain.repository.EmbeddingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmbeddingRepositoryImpl @Inject constructor(
    private val tokenizerDataSource: TokenizerDataSource,
    private val embeddingDataSource: EmbeddingDataSource
) : EmbeddingRepository {
    override suspend fun getSematicVector(text: String): FloatArray
        = withContext(Dispatchers.IO) {
            val tokens = tokenizerDataSource.tokenize(text)
            Log.d(TAG, "Tokenized token: ${tokens.contentToString()}")
            embeddingDataSource.embed(tokens)
        }

    companion object {
        private const val TAG = "EmbeddingRepositoryImpl"
    }
}
