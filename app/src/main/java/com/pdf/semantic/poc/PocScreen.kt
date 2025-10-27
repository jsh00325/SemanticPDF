package com.pdf.semantic.poc

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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
import com.pdf.semantic.domain.model.Slide
import com.pdf.semantic.poc.ui.composable.LoadingIndicator
import com.pdf.semantic.poc.ui.composable.PdfSlideInfo
import com.pdf.semantic.poc.ui.composable.SearchBar
import com.pdf.semantic.poc.ui.state.PocUiState

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

            is PocUiState.Processing ->
                ProcessingStateContent(
                    modifier = Modifier.padding(innerPadding),
                    message = state.message,
                )

            is PocUiState.PdfParsed ->
                PdfParsedStateContent(
                    modifier = Modifier.padding(innerPadding),
                    pdfDocument = state.pdfDocument,
                    onSearch = { pocViewModel.onSearch(it, state.pdfDocument) },
                )

            is PocUiState.SearchComplete ->
                SearchCompleteStateContent(
                    modifier = Modifier.padding(innerPadding),
                    pdfDocument = state.pdfDocument,
                    query = state.query,
                    topRelevantPageNumbers = state.topRelevantSlide,
                )

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
private fun ProcessingStateContent(
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
    onSearch: (String) -> Unit,
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier =
                Modifier
                    .weight(1.0f),
        ) {
            items(pdfDocument.slides) { slide ->
                PdfSlideInfo(slide = slide)
            }
        }

        Spacer(Modifier.height(8.dp))

        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            onSearch = onSearch,
        )
    }
}

@Composable
private fun SearchCompleteStateContent(
    modifier: Modifier = Modifier,
    pdfDocument: PdfDocument,
    query: String,
    topRelevantPageNumbers: List<Slide>,
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = Icons.Default.Search,
                contentDescription = "QueryIcon",
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = query,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        HorizontalDivider(Modifier.padding(vertical = 16.dp))

        Text(
            text = "유사도 검색 결과",
            style = MaterialTheme.typography.headlineMedium,
        )

        LazyColumn(Modifier.padding(vertical = 8.dp)) {
            items(topRelevantPageNumbers) { slide ->
                PdfSlideInfo(slide = slide)
            }
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
