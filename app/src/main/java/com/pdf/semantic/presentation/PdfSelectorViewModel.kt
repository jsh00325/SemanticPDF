package com.pdf.semantic.presentation

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.usecase.ParsePdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
@HiltViewModel
class PdfSelectorViewModel @Inject constructor(
    private val parsePdfUseCase: ParsePdfUseCase
) : ViewModel() {

    // 1. 성공적으로 파싱된 PdfDocument 데이터를 담을 상태
    private val _pdfDocument = mutableStateOf<PdfDocument?>(null)
    val pdfDocument: State<PdfDocument?> = _pdfDocument

    // 2. 로딩 상태를 관리할 상태
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // 3. 에러 메시지를 담을 상태
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _selectedPdfUri = mutableStateOf<Uri?>(null)
    val selectedPdfUri: State<Uri?> = _selectedPdfUri


    fun onPdfSelected(uri: Uri?) {
        _selectedPdfUri.value = uri
        uri?.let {
            parseDocument(it)
        }
    }

    private fun parseDocument(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _pdfDocument.value = null

            val result = parsePdfUseCase(uri)

            result.onSuccess { document ->
                _pdfDocument.value = document
            }.onFailure {
                _errorMessage.value = "PDF 파싱 중 오류가 발생했습니다."
            }

            _isLoading.value = false
        }
    }
}
