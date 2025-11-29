package com.pdf.semantic.data.mapper

import com.pdf.semantic.data.entity.FolderEntity
import com.pdf.semantic.domain.model.FolderItem
import com.pdf.semantic.domain.model.FolderTreeNode

object FolderMapper {
    fun FolderEntity.toModel(): FolderItem = FolderItem(
        id = id,
        name = name,
        parentId = parentId,
    )

    fun List<FolderEntity>.toModels(): List<FolderItem> = map { it.toModel() }

    fun List<FolderEntity>.toFolderTreeNode(): FolderTreeNode {
        val childrenMap = this.groupBy { it.parentId }

        fun buildTree(parentId: Long?): List<FolderTreeNode> {
            val children = childrenMap[parentId] ?: emptyList()
            return children.map { child ->
                FolderTreeNode(
                    id = child.id,
                    name = child.name,
                    children = buildTree(child.id)
                )
            }
        }

        return FolderTreeNode(
            id = null,
            name = "폴더",
            children = buildTree(null)
        )
    }
}
