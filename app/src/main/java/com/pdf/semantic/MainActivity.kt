package com.pdf.semantic

// MainActivity.kt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdf.semantic.data.repositoryImpl.EmbeddingRepositoryImpl
import com.pdf.semantic.ui.theme.SemanticPDFTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var embeddingRepository: EmbeddingRepositoryImpl

    private val textsToEmbed = mapOf(
        "Query" to "Which planet is known as the Red Planet?",
//        "Doc 1" to "Venus is often called Earth's twin because of its similar size and proximity.",
//        "Doc 2" to "Mars, known for its reddish appearance, is often referred to as the Red Planet.",
//        "Doc 3" to "Jupiter, the largest planet in our solar system, has a prominent red spot.",
//        "Doc 4" to "Saturn, famous for its rings, is sometimes mistaken for the Red Planet."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SemanticPDFTheme { // 앱 테마에 맞게 수정
                Scaffold { innerPadding ->
                    EmbeddingTestScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun EmbeddingTestScreen(modifier: Modifier) {
        // 1. 상태 정의: 결과(벡터)를 저장할 Map과 진행 상태를 저장할 Boolean
        var embeddingResults by remember { mutableStateOf<Map<String, FloatArray>>(emptyMap()) }
        var isProcessing by remember { mutableStateOf(false) }
        var triggerEmbedding by remember { mutableStateOf(false) }

        // 2. LaunchedEffect: triggerEmbedding이 true가 되면 순차적으로 임베딩 실행
        LaunchedEffect(triggerEmbedding) {
            if (triggerEmbedding) {
                isProcessing = true
                embeddingResults = emptyMap() // 결과 초기화

                for ((key, text) in textsToEmbed) {
                    try {
                        // Repository의 suspend 함수 호출
                        val vector = embeddingRepository.getSematicVector(text)

                        // 현재 결과 Map에 새로운 결과를 추가하여 상태 업데이트
                        embeddingResults = embeddingResults + (key to vector)

                        // 순차적으로 진행되는 것을 시각적으로 확인하기 위해 약간의 딜레이 추가 (선택 사항)
                        delay(1000)

                    } catch (e: Exception) {
                        Log.e("EmbeddingTestScreen", "Error embedding text: $text", e)
                        // 에러 처리
                        embeddingResults = embeddingResults + (key to floatArrayOf(-1f)) // 에러 표시
                    }
                }
                isProcessing = false
            }
        }

        // 3. UI 구성
        Surface(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        // 버튼을 누르면 triggerEmbedding 상태를 변경하여 LaunchedEffect 실행
                        if (!isProcessing) {
                            triggerEmbedding = !triggerEmbedding
                        }
                    },
                    enabled = !isProcessing // 처리 중일 때는 버튼 비활성화
                ) {
                    Text(if (isProcessing) "임베딩 중..." else "임베딩 시작")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(embeddingResults.toList()) { (key, vector) ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = key,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = textsToEmbed[key] ?: "",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Vector(size=${vector.size}): [${vector.take(5).joinToString(", ")}...]",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
