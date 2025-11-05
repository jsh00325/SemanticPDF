package com.pdf.semantic.presentation.globalsearch

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.usecase.globalsearch.SearchGlobalUsecase
import com.pdf.semantic.domain.usecase.pdfreader.GetPdfDetailUsecase
import com.pdf.semantic.domain.usecase.setting.ObserveHasShownGuideUsecase
import com.pdf.semantic.domain.usecase.setting.ObserveIsExpansionOnUsecase
import com.pdf.semantic.domain.usecase.setting.SetHasShownGuideUsecase
import com.pdf.semantic.domain.usecase.setting.SetIsExpansionOnUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalSearchViewModel
    @Inject
    constructor(
        private val searchGlobal: SearchGlobalUsecase,
        observeIsExpansionOn: ObserveIsExpansionOnUsecase,
        observeHasShownGuide: ObserveHasShownGuideUsecase,
        private val setIsExpansionOn: SetIsExpansionOnUsecase,
        private val setHasShownGuide: SetHasShownGuideUsecase,
        private val getPdfPageBitmap: GetPdfDetailUsecase,
    ) : ViewModel() {
        private val _searchQuery = MutableStateFlow("")
        val searchQuery = _searchQuery.asStateFlow()

        private val _uiState = MutableStateFlow<GlobalSearchUiState>(GlobalSearchUiState.Idle)
        val uiState = _uiState.asStateFlow()

        val isExpansionOn: StateFlow<Boolean> =
            observeIsExpansionOn()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = false,
                )

        private val _hasShownGuide = MutableStateFlow(true)
        val hasShownGuide = _hasShownGuide.asStateFlow()

        init {
            viewModelScope.launch {
                _hasShownGuide.value = observeHasShownGuide().first()
            }
        }

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

                val rawSearchResult =
                    searchGlobal(
                        query = _searchQuery.value,
                        useExpandQuery = isExpansionOn.value,
                    )

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

                initialUiItems.forEach { item ->
                    viewModelScope.launch(Dispatchers.IO) {
                        getPdfPageBitmap(item.pdfId, item.slideNumber)?.let {
                            updateImageForItem(item.pdfId, item.slideNumber, it)
                        }
                    }
                }
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

        fun onExpansionToggled() {
            viewModelScope.launch {
                val currentEnabled = isExpansionOn.value
                setIsExpansionOn(!currentEnabled)
            }
        }

        fun onGuideShown() {
            viewModelScope.launch {
                setHasShownGuide()
            }
        }
    }
