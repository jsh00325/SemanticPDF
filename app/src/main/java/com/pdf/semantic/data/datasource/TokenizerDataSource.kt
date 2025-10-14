package com.pdf.semantic.data.datasource

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class TokenizerDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private var tokenizer: HuggingFaceTokenizer? = null
        private val singleThreadDispatcher = Dispatchers.IO.limitedParallelism(1)

        private suspend fun initialize() =
            withContext(singleThreadDispatcher) {
                if (tokenizer != null) return@withContext

                val tokenizerFile = prepareTokenizerFile()
                tokenizer = HuggingFaceTokenizer.newInstance(tokenizerFile.toPath())
                Log.d(TAG, "Tokenizer initialized.")
            }

        private fun prepareTokenizerFile(): File {
            val cacheFile = File(context.cacheDir, TOKENIZER_FILENAME)
            if (!cacheFile.exists()) {
                Log.d(TAG, "Cache file not found. Copying from assets...")
                copyFromAssetsToCache(cacheFile)
            }
            return cacheFile
        }

        private fun copyFromAssetsToCache(destinationFile: File) {
            try {
                context.assets.open(TOKENIZER_ASSET_PATH).use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.d(TAG, "Tokenizer file copied to cache.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to copy tokenizer from assets", e)
                throw e
            }
        }

        suspend fun tokenize(text: String): LongArray {
            initialize()
            val tokenizer = requireNotNull(tokenizer) { "Tokenizer is not initialized." }

            return withContext(singleThreadDispatcher) {
                tokenizer.encode(text).ids
            }
        }

        companion object {
            private const val TAG = "TokenizerDataSource"
            private const val TOKENIZER_FILENAME = "tokenizer.json"
            private const val TOKENIZER_ASSET_PATH = "tokenizer.json"
        }
    }
