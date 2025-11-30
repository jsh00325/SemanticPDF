package com.pdf.semantic.presentation.pdfreader

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.model.PdfReaderUiState
import com.pdf.semantic.domain.usecase.pdfreader.GetPdfDetailUsecase
import com.pdf.semantic.domain.usecase.pdfreader.LoadSpecificPdfUsecase
import com.pdf.semantic.domain.usecase.pdfreader.SearchInDocumentUsecase
import com.pdf.semantic.domain.usecase.setting.ObserveIsExpansionOnUsecase
import com.pdf.semantic.domain.usecase.setting.SetIsExpansionOnUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
        private val searchInDocumentUsecase: SearchInDocumentUsecase,
        private val observeIsExpansionOnUsecase: ObserveIsExpansionOnUsecase,
        private val setIsExpansionOnUsecase: SetIsExpansionOnUsecase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(PdfReaderUiState())
        val uiState = _uiState.asStateFlow()
        val initialPage: Int = savedStateHandle.get<Int>("page") ?: 1
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

        val isExpansionOn: StateFlow<Boolean> =
            observeIsExpansionOnUsecase()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = false,
                )

        fun onSearchTriggered() {
            val query = _uiState.value.searchQuery
            if (query.isBlank()) return

            viewModelScope.launch {
                _uiState.update { it.copy(isSearching = true) }

                try {
                    val results =
                        searchInDocumentUsecase(
                            pdfId = pdfId,
                            query = query,
                            useExpandQuery = isExpansionOn.value,
                        )

                    if (results.isNotEmpty()) {
                        _uiState.update {
                            it.copy(searchResults = results, currentResultIndex = 0)
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                searchResults = emptyList(),
                                currentResultIndex = -1,
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _uiState.update { it.copy(isSearching = false) }
                }
            }
        }

        fun toggleExpansion() {
            viewModelScope.launch {
                setIsExpansionOnUsecase(!isExpansionOn.value)
            }
        }

        fun moveToNextResult() {
            _uiState.update { state ->
                if (state.searchResults.isNotEmpty()) {
                    val nextIndex =
                        (state.currentResultIndex + 1) % state.searchResults.size

                    state.copy(currentResultIndex = nextIndex)
                } else {
                    state
                }
            }
        }

        fun moveToPrevResult() {
            _uiState.update { state ->
                if (state.searchResults.isNotEmpty()) {
                    val prevIndex =
                        if (state.currentResultIndex - 1 < 0) {
                            state.searchResults.size - 1
                        } else {
                            state.currentResultIndex - 1
                        }
                    state.copy(currentResultIndex = prevIndex)
                } else {
                    state
                }
            }
        }

        fun clearSearch() {
            _uiState.update {
                it.copy(
                    searchQuery = "",
                    searchResults = emptyList(),
                    currentResultIndex = -1,
                    isSearchExpanded = false,
                )
            }
        }
    }
