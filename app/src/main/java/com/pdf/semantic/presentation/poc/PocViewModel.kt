package com.pdf.semantic.presentation.poc

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.usecase.ParsePdfUseCase
import com.pdf.semantic.presentation.poc.ui.state.PocUiState
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PocViewModel
    @Inject
    constructor(
        private val parsePdfUseCase: ParsePdfUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<PocUiState>(PocUiState.Idle)
        val uiState: StateFlow<PocUiState> = _uiState.asStateFlow()

        fun onPdfSelected(uri: Uri) {
            viewModelScope.launch {
                _uiState.emit(PocUiState.PdfProcessing("PDF 파싱 중입니다..."))

                parsePdfUseCase(uri)
                    .onSuccess { document ->
                        _uiState.emit(PocUiState.PdfParsed(document))
                    }.onFailure {
                        val errorMessage =
                            if (it is InvalidPasswordException) {
                                "PDF 파일이 암호화되어 있습니다."
                            } else {
                                it.printStackTrace()
                                "PDF 파싱 중 오류가 발생했습니다."
                            }
                        _uiState.emit(PocUiState.Error(errorMessage))
                    }
            }
        }
    }
