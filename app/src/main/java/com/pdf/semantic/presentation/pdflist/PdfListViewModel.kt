package com.pdf.semantic.presentation.pdflist

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.model.PdfItem
import com.pdf.semantic.domain.usecase.pdflist.AddPdfUsecase
import com.pdf.semantic.domain.usecase.pdflist.DeletePdfUsecase
import com.pdf.semantic.domain.usecase.pdflist.ObservePdfListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfListViewModel
    @Inject
    constructor(
        observePdfList: ObservePdfListUsecase,
        private val addPdf: AddPdfUsecase,
        private val deletePdf: DeletePdfUsecase,
    ) : ViewModel() {
        val pdfList: StateFlow<List<PdfItem>> =
            observePdfList().stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

        fun onPdfAdded(uri: Uri) {
            viewModelScope.launch {
                addPdf(uri)
            }
        }

        fun onPdfDeleted(pdfId: Long) {
            viewModelScope.launch {
                deletePdf(pdfId)
            }
        }
    }
