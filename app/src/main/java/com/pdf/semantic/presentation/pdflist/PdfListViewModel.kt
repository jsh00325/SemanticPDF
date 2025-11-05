package com.pdf.semantic.presentation.pdflist

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.model.PdfItem
import com.pdf.semantic.domain.usecase.pdflist.AddPdfUsecase
import com.pdf.semantic.domain.usecase.pdflist.DeletePdfUsecase
import com.pdf.semantic.domain.usecase.pdflist.ObservePdfListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

        private val _isMultiSelectMode = MutableStateFlow(false)
        val isMultiSelectMode: StateFlow<Boolean> = _isMultiSelectMode

        private val _selectedPdfIds = MutableStateFlow(emptySet<Long>())
        val selectedPdfIds: StateFlow<Set<Long>> = _selectedPdfIds

        private val _isAllSelected = MutableStateFlow(false)
        val isAllSelected: StateFlow<Boolean> = _isAllSelected

        fun onPdfAdded(uri: Uri) {
            viewModelScope.launch {
                addPdf(uri.toString())
            }
        }

        fun deleteSelectedPdf() {
            viewModelScope.launch {
                _selectedPdfIds.value.forEach { pdfId ->
                    deletePdf(pdfId)
                }
                _isMultiSelectMode.value = false
                _selectedPdfIds.value = emptySet()
            }
        }

        fun onClickInMultiSelectMode(pdfId: Long) {
            if (_isMultiSelectMode.value) {
                val currentIds = _selectedPdfIds.value
                _selectedPdfIds.value =
                    if (currentIds.contains(pdfId)) {
                        currentIds - pdfId
                    } else {
                        currentIds + pdfId
                    }

                _isAllSelected.value = _selectedPdfIds.value.size == pdfList.value.size
            }
        }

        fun onLongClick(pdfId: Long) {
            _isMultiSelectMode.value = true
            _selectedPdfIds.value = _selectedPdfIds.value + pdfId
        }

        fun disableMultiSelectMode() {
            _isMultiSelectMode.value = false
            _selectedPdfIds.value = emptySet()
            _isAllSelected.value = false
        }

        fun selectAll() {
            _isAllSelected.value = true
            _selectedPdfIds.value = pdfList.value.map { it.id }.toSet()
        }

        fun deselectAll() {
            _isAllSelected.value = false
            _selectedPdfIds.value = emptySet()
        }
    }
