package com.pdf.semantic.presentation.pdfreader

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.model.PdfReaderUiState
import com.pdf.semantic.domain.usecase.pdfreader.GetPdfDetailUsecase
import com.pdf.semantic.domain.usecase.pdfreader.LoadSpecificPdfUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfReaderViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val loadSpecificPdfUsecase: LoadSpecificPdfUsecase,
        private val getPdfDetailUsecase: GetPdfDetailUsecase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(PdfReaderUiState())
        val uiState = _uiState.asStateFlow()

        private var internalPath: String = ""
        private var pdfId: Long = 0L

        init {
            pdfId = savedStateHandle.get<Long>("pdfId") ?: 0L
            if (pdfId > 0) {
                loadInfoAndTriggerPreload()
            } else {
                _uiState.update { it.copy(isLoading = false, title = "잘못된 PDF ID") }
            }
        }

        private fun loadInfoAndTriggerPreload() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                val result = loadSpecificPdfUsecase(pdfId)

                result
                    .onSuccess { pdfDetail ->
                        internalPath = pdfDetail.internalPath

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                title = pdfDetail.title,
                                totalPages = pdfDetail.totalPages,
                            )
                        }
                    }.onFailure { e ->
                        e.printStackTrace()
                        _uiState.update { it.copy(isLoading = false, title = "PDF 로드 오류") }
                    }
            }
        }

        suspend fun getPageBitmap(pageNumber: Int): Bitmap? =
            try {
                getPdfDetailUsecase(pdfId, pageNumber)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        fun toggleSearchExpanded() {
            android.util.Log.d("ViewModel", "toggleSearchExpanded 호출")
            _uiState.update {
                it.copy(isSearchExpanded = !it.isSearchExpanded)
            }
        }

        fun updateSearchQuery(query: String) {
            _uiState.update { it.copy(searchQuery = query) }
        }

        fun onSearchTriggered() {
            val query = _uiState.value.searchQuery
            if (query.isNotBlank()) {
                // TODO: 여기서 Embedding 검색 UseCase 호출
                println("Search Triggered: $query")
            }
        }
    }
