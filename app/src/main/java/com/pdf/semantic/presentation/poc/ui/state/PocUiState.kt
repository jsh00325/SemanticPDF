package com.pdf.semantic.presentation.poc.ui.state

import com.pdf.semantic.domain.model.PdfDocument

sealed class PocUiState {
    object Idle : PocUiState()

    data class PdfProcessing(
        val message: String,
    ) : PocUiState()

    data class PdfParsed(
        val pdfDocument: PdfDocument,
    ) : PocUiState()

    data class QueryProcessing(
        val message: String,
    ) : PocUiState()

    data class SearchComplete(
        val pdfDocument: PdfDocument,
        val query: String,
        val topRelevantPageNumbers: List<Int>,
    ) : PocUiState()

    data class Error(
        val message: String,
    ) : PocUiState()
}
