package com.pdf.semantic.data.datasource

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class EmbeddingDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private var interpreter: Interpreter? = null
        private val singleThreadDispatcher = Dispatchers.IO.limitedParallelism(1)

        private suspend fun initialize() =
            withContext(singleThreadDispatcher) {
                if (interpreter != null) return@withContext

                val compatList = CompatibilityList()
                val options =
                    Interpreter.Options().apply {
                        if (compatList.isDelegateSupportedOnThisDevice) {
                            val delegateOptions = compatList.bestOptionsForThisDevice
                            this.addDelegate(GpuDelegate(delegateOptions))
                        } else {
                            this.setNumThreads(Runtime.getRuntime().availableProcessors())
                        }
                    }
                interpreter = Interpreter(loadModelFile(), options)
                Log.d(TAG, "Interpreter initialized.")
            }

        private fun loadModelFile(): MappedByteBuffer {
            val fileDescriptor = context.assets.openFd(EMBEDDING_MODEL)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            val retFile = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            fileDescriptor.close()
            return retFile
        }

        suspend fun embed(tokens: LongArray): FloatArray {
            initialize()
            val interpreter = requireNotNull(interpreter) { "Interpreter is not initialized." }

            return withContext(singleThreadDispatcher) {
                val intTokens = tokens.map { it.toInt() }.toIntArray()
                val modelInput = arrayOf(intTokens.copyOf(MAX_SEQ_LEN))
                val modelOutput = Array(1) { FloatArray(OUTPUT_DIM) }

                interpreter.run(modelInput, modelOutput)
                modelOutput[0]
            }
        }

        companion object {
            private const val TAG = "EmbeddingDataSource"
            private const val EMBEDDING_MODEL = "embeddinggemma-300M_seq2048_mixed-precision.tflite"
            private const val MAX_SEQ_LEN = 2048
            private const val OUTPUT_DIM = 768
        }
    }
