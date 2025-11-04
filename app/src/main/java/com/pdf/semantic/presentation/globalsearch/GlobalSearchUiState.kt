package com.pdf.semantic.presentation.globalsearch

sealed class GlobalSearchUiState {
    object Idle : GlobalSearchUiState()

    object Loading : GlobalSearchUiState()

    data class SearchingSuccess(
        val results: List<GlobalSearchUiItem>,
    ) : GlobalSearchUiState()

    data class SearchingError(
        val message: String,
    ) : GlobalSearchUiState()
}
