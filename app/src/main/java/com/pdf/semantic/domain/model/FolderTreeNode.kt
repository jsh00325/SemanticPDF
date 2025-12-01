package com.pdf.semantic.domain.model

data class FolderTreeNode(
    val id: Long?,
    val name: String,
    val children: List<FolderTreeNode>,
)
