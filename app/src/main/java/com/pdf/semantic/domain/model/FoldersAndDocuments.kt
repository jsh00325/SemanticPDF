package com.pdf.semantic.domain.model

data class FoldersAndDocuments(
    val folders: List<FolderItem> = emptyList(),
    val documents: List<PdfItem> = emptyList(),
)
