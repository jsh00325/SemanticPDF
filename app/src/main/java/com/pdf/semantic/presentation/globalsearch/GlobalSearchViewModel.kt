package com.pdf.semantic.presentation.globalsearch

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.usecase.globalsearch.SearchGlobalUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalSearchViewModel
    @Inject
    constructor(
        private val searchGlobal: SearchGlobalUsecase,
    ) : ViewModel() {
        private val _searchQuery = MutableStateFlow("")
        val searchQuery = _searchQuery.asStateFlow()

        private val _uiState = MutableStateFlow<GlobalSearchUiState>(GlobalSearchUiState.Idle)
        val uiState = _uiState.asStateFlow()

        fun onSearchQueryChanged(query: String) {
            _searchQuery.value = query
        }

        fun searchQuery() {
            viewModelScope.launch {
                _uiState.value = GlobalSearchUiState.Loading

                if (_searchQuery.value.isBlank()) {
                    _uiState.value = GlobalSearchUiState.Idle
                    return@launch
                }

                val rawSearchResult = searchGlobal(_searchQuery.value, true)
                val initialUiItems =
                    rawSearchResult.map {
                        GlobalSearchUiItem(
                            pdfId = it.pdfId,
                            pdfTitle = it.pdfTitle,
                            totalPages = it.totalPages,
                            slideNumber = it.slideNumber,
                            similarityScore = it.similarityScore,
                            slidePreviewImage = null,
                        )
                    }
                _uiState.value = GlobalSearchUiState.SearchingSuccess(initialUiItems)

//                TODO: 추후 Usecase 구현 완료 후 주석 해제
//                initialUiItems.forEach { item ->
//                    viewModelScope.launch(Dispatchers.IO) {
//                        getPdfPageBitmap(item.pdfId, item.slideNumber).onSuccess { bitmap ->
//                            updateImageForItem(item.pdfId, item.slideNumber, bitmap)
//                        }
//                    }
//                }
            }
        }

        private fun updateImageForItem(
            pdfId: Long,
            slideNumber: Int,
            bitmap: Bitmap?,
        ) {
            if (bitmap == null) return

            _uiState.update { currentState ->
                if (currentState is GlobalSearchUiState.SearchingSuccess) {
                    val updatedItems =
                        currentState.results.map { item ->
                            if (item.pdfId == pdfId && item.slideNumber == slideNumber) {
                                item.copy(slidePreviewImage = bitmap)
                            } else {
                                item
                            }
                        }
                    currentState.copy(results = updatedItems)
                } else {
                    currentState
                }
            }
        }
    }
