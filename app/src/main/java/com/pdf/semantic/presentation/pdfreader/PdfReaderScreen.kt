package com.pdf.semantic.presentation.pdfreader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PdfReaderScreen(
    pdfId: Long? = null,
    onBackClick: () -> Unit,
) {
    // TODO: 추후 구현 예정
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Button(onBackClick) {
                Text(text = "Back")
            }
            Text(text = "Pdf Reader Screen")
            Text(text = "Pdf ID: $pdfId")
        }
    }
}
