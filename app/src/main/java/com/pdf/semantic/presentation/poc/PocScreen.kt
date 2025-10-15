package com.pdf.semantic.presentation.poc

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.presentation.poc.ui.composable.LoadingIndicator
import com.pdf.semantic.presentation.poc.ui.composable.PdfSlideInfo
import com.pdf.semantic.presentation.poc.ui.state.PocUiState

@Composable
fun PocScreen(pocViewModel: PocViewModel = viewModel()) {
    val uiState = pocViewModel.uiState.collectAsState()
    val filePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let { pocViewModel.onPdfSelected(it) }
        }

    Scaffold { innerPadding ->
        when (val state = uiState.value) {
            is PocUiState.Idle ->
                IdleStateContent(
                    modifier = Modifier.padding(innerPadding),
                    onUploadClick = { filePickerLauncher.launch("application/pdf") },
                )

            is PocUiState.PdfProcessing ->
                PdfProcessingStateContent(
                    modifier = Modifier.padding(innerPadding),
                    message = state.message,
                )

            is PocUiState.PdfParsed ->
                PdfParsedStateContent(
                    modifier = Modifier.padding(innerPadding),
                    pdfDocument = state.pdfDocument,
                )

            is PocUiState.QueryProcessing -> {
            }

            is PocUiState.SearchComplete -> {
            }

            is PocUiState.Error ->
                ErrorStateContent(
                    modifier = Modifier.padding(innerPadding),
                    errorMessage = state.message,
                )
        }
    }
}

@Composable
private fun IdleStateContent(
    modifier: Modifier = Modifier,
    onUploadClick: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.clickable(onClick = onUploadClick),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Upload",
                    modifier =
                        Modifier
                            .padding(24.dp)
                            .size(100.dp),
                )
                Text(
                    text = "Upload a PDF",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Composable
private fun PdfProcessingStateContent(
    modifier: Modifier = Modifier,
    message: String,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LoadingIndicator(message = message)
    }
}

@Composable
private fun PdfParsedStateContent(
    modifier: Modifier = Modifier,
    pdfDocument: PdfDocument,
) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(8.dp)) {
        items(pdfDocument.slides) { slide ->
            PdfSlideInfo(slide = slide)
        }
    }
}

@Composable
private fun ErrorStateContent(
    modifier: Modifier = Modifier,
    errorMessage: String,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = errorMessage)
    }
}
