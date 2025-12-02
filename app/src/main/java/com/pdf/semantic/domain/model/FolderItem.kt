package com.pdf.semantic.domain.model

data class FolderItem(
    val id: Long,
    val name: String,
    val parentId: Long?,
)
