package com.pdf.semantic.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
@Composable
fun PdfSelectorScreen(
    viewModel: PdfSelectorViewModel = hiltViewModel()
) {
    val pdfDocument by viewModel.pdfDocument
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    val selectPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            //파일이 선택되면 파싱을 시작
            uri?.let {
                viewModel.onPdfSelected(it)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            // 좌우 패딩은 16dp, 상하 패딩은 32dp로 설정
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(
            onClick = {
                selectPdfLauncher.launch("application/pdf")
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue
            )
        ) {
            Text("PDF 파일 선택하기")
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "파일을 분석하고 있습니다...")
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        pdfDocument?.let { doc ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(doc.slides) { slide ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "페이지: ${slide.slideNumber}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = if (slide.content.isNotBlank()) slide.content else "추출된 텍스트가 없습니다.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
