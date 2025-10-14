package com.pdf.semantic.data.datasource

import android.content.Context
import android.util.Log
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import com.google.ai.edge.litert.TensorBuffer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class EmbeddingDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private var model: CompiledModel? = null
        private var inputBuffers: List<TensorBuffer>? = null
        private var outputBuffers: List<TensorBuffer>? = null
        private val singleThreadDispatcher = Dispatchers.IO.limitedParallelism(1)

        private suspend fun initialize() =
            withContext(singleThreadDispatcher) {
                if (model != null) return@withContext

                val compiledModel =
                    CompiledModel.create(
                        context.assets,
                        EMBEDDING_MODEL,
                        CompiledModel.Options(Accelerator.CPU),
                        null,
                    )
                model = compiledModel
                inputBuffers = compiledModel.createInputBuffers()
                outputBuffers = compiledModel.createOutputBuffers()
                Log.d(TAG, "Embedding Model initialized.")
            }

        suspend fun embed(tokens: LongArray): FloatArray {
            initialize()
            val model = requireNotNull(model) { "Embedding Model is not initialized." }
            val inputBuffers = requireNotNull(inputBuffers) { "Input Buffers are not initialized." }
            val outputBuffers = requireNotNull(outputBuffers) { "Output Buffers are not initialized." }

            return withContext(singleThreadDispatcher) {
                val intTokens = tokens.map { it.toInt() }.toIntArray()
                inputBuffers[0].writeInt(intTokens.copyOf(MAX_SEQ_LEN))

                model.run(inputBuffers, outputBuffers)

                outputBuffers[0].readFloat()
            }
        }

        private enum class ModelType(val modelName: String, val maxSeqLength: Int) {
            SEQ_512(
                modelName = "embeddinggemma-300M_seq512_mixed-precision.tflite",
                maxSeqLength = 512
            ),
            SEQ_2048(
                modelName = "embeddinggemma-300M_seq2048_mixed-precision.tflite",
                maxSeqLength = 2048
            )
        }

        companion object {
            private const val TAG = "EmbeddingDataSource"
            private const val EMBEDDING_MODEL = "embeddinggemma-300M_seq512_mixed-precision.tflite"
            private const val MAX_SEQ_LEN = 512
            private const val OUTPUT_DIM = 768
        }
    }
