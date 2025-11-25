package com.pdf.semantic.domain.model

data class PdfReaderUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val totalPages: Int = 0,
    val isSearchExpanded: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val currentResultIndex: Int = -1,
)
