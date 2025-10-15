package com.pdf.semantic.presentation

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.usecase.ParsePdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val parsePdfUseCase: ParsePdfUseCase,
    ) : ViewModel() {
        // 1. 상태 변수를 PdfDocument를 저장할 수 있도록 변경합니다.
        private val _pdfDocument = mutableStateOf<PdfDocument?>(null)
        val pdfDocument = _pdfDocument

        val isLoading = mutableStateOf(false)
        val errorMessage = mutableStateOf<String?>(null)

        fun parsePdf(uri: Uri) {
            viewModelScope.launch {
                isLoading.value = true
                errorMessage.value = null
                _pdfDocument.value = null

                val result = parsePdfUseCase(uri)

                result
                    .onSuccess { document ->
                        // 성공
                        _pdfDocument.value = document
                    }.onFailure { exception ->
                        // 실패
                        errorMessage.value = "PDF 파싱 중 오류가 발생했습니다: ${exception.message}"
                    }

                isLoading.value = false
            }
        }
    }
