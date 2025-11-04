package com.pdf.semantic.presentation.pdfreader

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.model.PdfReaderUiState
import com.pdf.semantic.domain.repository.PdfFileRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import com.pdf.semantic.domain.usecase.pdfreader.GetPdfDetailUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
        private val pdfMetadataRepository: PdfMetadataRepository,
        private val pdfFileRepository: PdfFileRepository,
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
                try {
                    val metadata = pdfMetadataRepository.getPdfMetadata(pdfId)

                    internalPath = pdfMetadataRepository.getPdfInternalPath(pdfId)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            title = metadata.title,
                            totalPages = metadata.totalPages,
                        )
                    }

                    launch(Dispatchers.IO) {
                        getPdfDetailUsecase(pdfId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.update { it.copy(isLoading = false, title = "PDF 로드 오류") }
                }
            }
        }

        suspend fun getPageBitmap(pageNumber: Int): Bitmap? {
            if (internalPath.isEmpty()) return null
            return try {
                pdfFileRepository.getPageBitmap(pdfId, internalPath, pageNumber)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
