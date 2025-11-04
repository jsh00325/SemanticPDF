package com.pdf.semantic.domain.model

data class PdfReaderUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val totalPages: Int = 0,
)
