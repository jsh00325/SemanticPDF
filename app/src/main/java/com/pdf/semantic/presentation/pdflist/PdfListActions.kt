package com.pdf.semantic.presentation.pdflist

import androidx.compose.runtime.Immutable

@Immutable
data class PdfListActions(
    val onNavigateToFolder: (Long?) -> Unit,
    val onNavigateToMoveDialog: (List<Long>, List<Long>) -> Unit,
    val onFolderItemTap: (Long) -> Unit,
    val onPdfItemTap: (Long) -> Unit,
    val onFolderLongPress: (Long) -> Unit,
    val onPdfLongPress: (Long) -> Unit,
    val onPathFolderTap: (Long?) -> Unit,
    val onOpenDrawerClick: () -> Unit,
    val onGlobalSearchClick: () -> Unit,
    val onAddFolderClick: () -> Unit,
    val onAddPdfClick: () -> Unit,
    val onSelectAllClick: () -> Unit,
    val onDeselectAllClick: () -> Unit,
    val onDeleteClick: () -> Unit,
)
