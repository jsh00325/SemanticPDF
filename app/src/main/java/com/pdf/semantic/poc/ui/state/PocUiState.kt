package com.pdf.semantic.poc.ui.state

import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.model.Slide

sealed class PocUiState {
    object Idle : PocUiState()

    data class Processing(
        val message: String,
    ) : PocUiState()

    data class PdfParsed(
        val pdfDocument: PdfDocument,
    ) : PocUiState()

    data class SearchComplete(
        val pdfDocument: PdfDocument,
        val query: String,
        val topRelevantSlide: List<Slide>,
    ) : PocUiState()

    data class Error(
        val message: String,
    ) : PocUiState()
}
