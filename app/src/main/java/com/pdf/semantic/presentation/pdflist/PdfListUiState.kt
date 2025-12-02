package com.pdf.semantic.presentation.pdflist

import com.pdf.semantic.domain.model.FolderItem
import com.pdf.semantic.domain.model.PdfItem

data class PdfListUiState(
    val currentFolderName: String = "폴더",
    val folderPath: List<FolderItem> = emptyList(),
    val folders: List<FolderItem> = emptyList(),
    val pdfs: List<PdfItem> = emptyList(),
    val isMultiSelectMode: Boolean = false,
    val selectedFolderIds: Set<Long> = emptySet(),
    val selectedPdfIds: Set<Long> = emptySet(),
    val isAllSelected: Boolean = false,
)
