package com.pdf.semantic

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdf.semantic.data.repositoryImpl.EmbeddingRepositoryImpl
import com.pdf.semantic.ui.theme.SemanticPDFTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.sqrt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var embeddingRepository: EmbeddingRepositoryImpl

    private val textsToEmbed =
        mapOf(
            "Query" to "Which planet is known as the Red Planet?",
            "Doc 1" to "Venus is often called Earth's twin because of its similar size and proximity.",
            "Doc 2" to "Mars, known for its reddish appearance, is often referred to as the Red Planet.",
            "Doc 3" to "Jupiter, the largest planet in our solar system, has a prominent red spot.",
            "Doc 4" to "Saturn, famous for its rings, is sometimes mistaken for the Red Planet.",
        )

    private fun cosineSimilarity(
        vectorA: FloatArray,
        vectorB: FloatArray,
    ): Float {
        if (vectorA.size != vectorB.size) {
            throw IllegalArgumentException("Vectors must be of the same size")
        }
        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in vectorA.indices) {
            dotProduct += vectorA[i] * vectorB[i]
            normA += vectorA[i] * vectorA[i]
            normB += vectorB[i] * vectorB[i]
        }

        val denominator = sqrt(normA) * sqrt(normB)
        if (denominator == 0.0) {
            return 0.0f
        }

        return (dotProduct / denominator).toFloat()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SemanticPDFTheme {
                Scaffold { innerPadding ->
                    EmbeddingTestScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun EmbeddingTestScreen(modifier: Modifier) {
        var isProcessing by remember { mutableStateOf(false) }
        var triggerEmbedding by remember { mutableStateOf(false) }
        var displayResults by remember { mutableStateOf<List<EmbeddingDisplayResult>>(emptyList()) }

        LaunchedEffect(triggerEmbedding) {
            if (triggerEmbedding) {
                isProcessing = true
                displayResults = emptyList()

                val queryText = textsToEmbed["Query"] ?: ""
                val queryVector =
                    try {
                        embeddingRepository.embedQueryForRetrieval(queryText)
                    } catch (e: Exception) {
                        Log.e("EmbeddingTestScreen", "Error embedding Query", e)
                        floatArrayOf()
                    }

                displayResults = displayResults + EmbeddingDisplayResult("Query", queryText, queryVector)

                if (queryVector.isEmpty()) {
                    isProcessing = false
                    return@LaunchedEffect
                }

                for ((key, text) in textsToEmbed) {
                    if (key == "Query") continue

                    val docVector =
                        try {
                            embeddingRepository.embedDocumentForRetrieval(null, text)
                        } catch (e: Exception) {
                            Log.e("EmbeddingTestScreen", "Error embedding text: $text", e)
                            floatArrayOf()
                        }

                    val similarity = cosineSimilarity(queryVector, docVector)

                    displayResults = displayResults + EmbeddingDisplayResult(key, text, docVector, similarity)
                }

                isProcessing = false
            }
        }

        Surface(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = {
                        if (!isProcessing) {
                            triggerEmbedding = !triggerEmbedding
                        }
                    },
                    enabled = !isProcessing,
                ) {
                    Text(if (isProcessing) "임베딩 중..." else "임베딩 시작")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(displayResults) { result ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = result.key,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.text,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                result.similarity?.let {
                                    Text(
                                        text = "Cosine Similarity: %.4f".format(it),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Text(
                                    text = "Vector(size=${result.vector.size}): [${result.vector.take(
                                        5,
                                    ).joinToString(", ")}...]",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    data class EmbeddingDisplayResult(
        val key: String,
        val text: String,
        val vector: FloatArray,
        val similarity: Float? = null,
    )
}
