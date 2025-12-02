package com.pdf.semantic.domain.model

data class FoldersAndPdfs(
    val folders: List<FolderItem> = emptyList(),
    val pdfs: List<PdfItem> = emptyList(),
)
