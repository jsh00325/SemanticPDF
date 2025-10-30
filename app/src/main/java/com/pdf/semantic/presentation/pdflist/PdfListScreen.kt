package com.pdf.semantic.presentation.pdflist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PdfListScreen(
    onGlobalSearchClick: () -> Unit,
    onPdfClick: (Long) -> Unit,
    viewModel: PdfListViewModel = viewModel(),
) {
    // TODO: 추후 구현 예정
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Card(onClick = onGlobalSearchClick) {
                Text(text = "Global Search")
            }

            Card(onClick = { onPdfClick(1) }) {
                Text(text = "Pdf 1")
            }

            Card(onClick = { onPdfClick(2) }) {
                Text(text = "Pdf 2")
            }
        }
    }
}
