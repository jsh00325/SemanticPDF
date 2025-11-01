package com.pdf.semantic.presentation.pdfreader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PdfReaderScreen(
    pdfId: Long? = null,
    onBackClick: () -> Unit,
    viewModel: PdfReaderViewModel = viewModel(),
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }

                Text(
                    text = "PDF 제목 (ID: $pdfId)",
                    modifier = Modifier.padding(start = 16.dp),
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(100) { index ->
                    Text(
                        text = "슬라이드 ${index + 1}",
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { },
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
        ) {
            Icon(
                Icons.Filled.Search,
                contentDescription = "검색",
            )
        }
    }
}
