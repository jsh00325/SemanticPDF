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
            "Query" to "파이썬에서 리스트랑 튜플 차이가 뭐야?",
            "Doc 1" to "파이썬에서 리스트(List)와 튜플(Tuple)의 가장 결정적인 차이점은 '변경 가능성'(Mutability)입니다. 리스트는 생성된 후에 원소를 추가, 삭제, 수정하는 것이 자유로운 '변경 가능한(mutable)' 객체입니다. 반면, 튜플은 한 번 생성되면 그 내용을 바꿀 수 없는 '변경 불가능한(immutable)' 객체입니다.",
            "Doc 2" to "리스트(List)는 파이썬의 대표적인 순서형 자료구조로, 대괄호 `[]`를 사용하여 생성합니다. `append()` 메소드로 새로운 원소를 추가하거나, `del` 키워드로 특정 인덱스의 원소를 삭제하는 등 다양한 연산을 통해 내용을 자유롭게 수정할 수 있습니다.",
            "Doc 3" to "튜플(Tuple)은 소괄호 `()`를 사용하여 정의하며, 한 번 생성되면 내부 원소를 변경할 수 없습니다. 이처럼 데이터의 불변성이 보장되어야 할 때, 예를 들어 함수의 반환 값으로 여러 값을 안전하게 전달하고 싶을 때 유용하게 사용됩니다.",
            "Doc 4" to "딕셔너리(Dictionary)는 'Key'와 'Value'를 한 쌍으로 묶어 관리하는 자료구조입니다. `{}`를 사용하며, 순서가 없는 것이 특징입니다. 각 값에 고유한 키를 부여하여 데이터를 효율적으로 탐색하고 관리할 수 있습니다.",
            "Doc 5" to "`for` 반복문은 리스트, 튜플, 문자열 등 순회 가능한(iterable) 객체의 원소를 하나씩 차례대로 방문하여 코드 블록을 실행하는 데 사용됩니다. 데이터를 순차적으로 처리해야 할 때 필수적인 구문입니다.",
            "Doc 6" to "IP 주소는 인터넷에 연결된 각 컴퓨터를 식별하는 고유한 번호입니다. IPv4는 32비트 주소 체계를 사용하며, 4개의 8비트 숫자를 점으로 구분하여 표현합니다."
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
