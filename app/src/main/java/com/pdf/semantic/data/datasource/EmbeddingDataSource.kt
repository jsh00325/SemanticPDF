package com.pdf.semantic.data.datasource

import android.content.Context
import android.util.Log
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import com.pdf.semantic.di.ModelDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmbeddingDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        @ModelDispatcher private val modelDispatcher: CoroutineDispatcher,
    ) {
        private val model: CompiledModel by lazy {
            Log.d(TAG, "Initializing EmbeddingModel...")
            CompiledModel.create(
                context.assets,
                MODEL_NAME,
                CompiledModel.Options(Accelerator.CPU),
                null,
            )
        }

        suspend fun embed(tokens: LongArray): FloatArray =
            withContext(modelDispatcher) {
                val inputBuffers = model.createInputBuffers()
                val outputBuffers = model.createOutputBuffers()

                val intTokens = tokens.map { it.toInt() }.toIntArray()
                val paddedTokens = intTokens.copyOf(MAX_SEQ_LENGTH)

                inputBuffers[0].writeInt(paddedTokens)

                model.run(inputBuffers, outputBuffers)

                outputBuffers[0].readFloat()
            }

        companion object {
            private const val TAG = "EmbeddingDataSource"
            private const val MODEL_NAME = "embeddinggemma-300M_seq512_mixed-precision.tflite"
            private const val MAX_SEQ_LENGTH = 512
        }
    }
