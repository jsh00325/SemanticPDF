package com.pdf.semantic.presentation

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.usecase.ParsePdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val parsePdfUseCase: ParsePdfUseCase
) : ViewModel() {

    val parsedTextList = mutableStateOf<List<String>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    fun parsePdf(uri: Uri) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val result = parsePdfUseCase(uri)
                parsedTextList.value = result
            } catch (e: Exception) {
                errorMessage.value = "PDF 파싱 중 오류가 발생했습니다."
            } finally {
                isLoading.value = false
            }
        }
    }
}
