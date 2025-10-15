package com.pdf.semantic.data.datasource

import android.content.Context
import android.util.Log
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
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
        private val modelCache = mutableMapOf<ModelType, CompiledModel>()
        private val singleThreadDispatcher = Dispatchers.IO.limitedParallelism(1)

        private suspend fun getModel(modelType: ModelType): CompiledModel =
            withContext(singleThreadDispatcher) {
                modelCache.getOrPut(modelType) {
                    CompiledModel
                        .create(
                            context.assets,
                            modelType.modelName,
                            CompiledModel.Options(Accelerator.CPU),
                            null,
                        ).also {
                            Log.d(TAG, "${modelType.maxSeqLength} Model initialized.")
                        }
                }
            }

        private suspend fun getModelForTokens(tokenCount: Int): Pair<CompiledModel, Int> {
            val modelType =
                if (tokenCount > ModelType.SEQ_512.maxSeqLength) {
                    ModelType.SEQ_2048
                } else {
                    ModelType.SEQ_512
                }

            return getModel(modelType) to modelType.maxSeqLength
        }

        suspend fun embed(tokens: LongArray): FloatArray {
            val (model, inputDimension) = getModelForTokens(tokens.size)

            return withContext(singleThreadDispatcher) {
                val inputBuffers = model.createInputBuffers()
                val outputBuffers = model.createOutputBuffers()

                val intTokens = tokens.map { it.toInt() }.toIntArray()
                val paddedTokens = intTokens.copyOf(inputDimension)

                inputBuffers[0].writeInt(paddedTokens)

                model.run(inputBuffers, outputBuffers)

                outputBuffers[0].readFloat()
            }
        }

        private enum class ModelType(
            val modelName: String,
            val maxSeqLength: Int,
        ) {
            SEQ_512(
                modelName = "embeddinggemma-300M_seq512_mixed-precision.tflite",
                maxSeqLength = 512,
            ),
            SEQ_2048(
                modelName = "embeddinggemma-300M_seq2048_mixed-precision.tflite",
                maxSeqLength = 2048,
            ),
        }

        companion object {
            private const val TAG = "EmbeddingDataSource"
        }
    }
