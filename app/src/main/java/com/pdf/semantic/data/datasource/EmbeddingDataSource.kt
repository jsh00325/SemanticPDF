package com.pdf.semantic.data.datasource

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmbeddingDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var interpreter: Interpreter? = null
    private val mutex = Mutex()
    private val inputBuffer = ByteBuffer.allocateDirect(MAX_SEQ_LEN * Long.SIZE_BYTES)
    private val outputBuffer = ByteBuffer.allocateDirect(OUTPUT_DIM * 4)

    private suspend fun initialize() {
        mutex.withLock {
            if (interpreter != null) return

            withContext(Dispatchers.IO) {
                val compatList = CompatibilityList()
                val options = Interpreter.Options().apply {
                    if (compatList.isDelegateSupportedOnThisDevice) {
                        // if the device has a supported GPU, add the GPU delegate
                        val delegateOptions = compatList.bestOptionsForThisDevice
                        this.addDelegate(GpuDelegate(delegateOptions))
                    } else {
                        this.setNumThreads(Runtime.getRuntime().availableProcessors())
                    }
                }
                interpreter = Interpreter(loadModelFile(), options)
            }
        }
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

        val paddedTokens = tokens.copyOf(MAX_SEQ_LEN)

        inputBuffer.rewind()
        inputBuffer.asLongBuffer().put(paddedTokens)

        outputBuffer.rewind()
        interpreter.run(inputBuffer, outputBuffer)

        outputBuffer.rewind()
        val embedding = FloatArray(OUTPUT_DIM)
        outputBuffer.asFloatBuffer().get(embedding)
        return embedding
    }

    companion object {
        private const val TAG = "EmbeddingDataSource"
        private const val EMBEDDING_MODEL = "embeddinggemma-300M_seq2048_mixed-precision.tflite"
        private const val MAX_SEQ_LEN = 2048
        private const val OUTPUT_DIM = 768
    }
}
