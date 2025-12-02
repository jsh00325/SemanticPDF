package com.pdf.semantic.presentation.pdflist.folderlist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pdf.semantic.domain.model.FolderTreeNode

private fun findParentIds(
    nodes: List<FolderTreeNode>,
    targetId: Long,
    currentPath: List<Long?> = emptyList(),
): List<Long?>? {
    for (node in nodes) {
        val newPath = currentPath + node.id
        if (node.id == targetId) {
            return newPath
        }
        findParentIds(node.children, targetId, newPath)?.let {
            return it
        }
    }
    return null
}

private data class FolderTreeNodeWithDepth(
    val node: FolderTreeNode,
    val depth: Int,
)

@Composable
fun FolderTree(
    modifier: Modifier = Modifier,
    currentFolderId: Long?,
    rootTreeNode: FolderTreeNode,
    onFolderClick: (Long?) -> Unit,
    forbiddenIds: Set<Long?>? = null,
    initiallyExpandedFolderId: Long? = null,
) {
    var expandedFolderIds by remember(rootTreeNode, initiallyExpandedFolderId) {
        val initialIds = mutableSetOf<Long?>(null)
        if (initiallyExpandedFolderId != null) {
            val parentIds = findParentIds(listOf(rootTreeNode), initiallyExpandedFolderId)
            parentIds?.forEach { initialIds.add(it) }
        }
        mutableStateOf(initialIds.toSet())
    }
    val flattenedList =
        remember(rootTreeNode, expandedFolderIds) {
            flattenTree(listOf(rootTreeNode), expandedFolderIds)
        }

    LazyColumn(modifier = modifier) {
        items(
            items = flattenedList,
            key = { it.node.id ?: -1L },
        ) { displayNode ->

            FolderTreeItem(
                node = displayNode.node,
                depth = displayNode.depth,
                currentFolderId = currentFolderId,
                isExpanded = displayNode.node.id in expandedFolderIds,
                isSelectable = forbiddenIds?.contains(displayNode.node.id)?.not() ?: true,
                onExpandClick = { clickedNode ->
                    val id = clickedNode.id
                    if (clickedNode.children.isNotEmpty()) {
                        expandedFolderIds =
                            if (id in expandedFolderIds) {
                                expandedFolderIds - id
                            } else {
                                expandedFolderIds + id
                            }
                    }
                },
                onFolderClick = {
                    if (displayNode.node.id != currentFolderId) {
                        onFolderClick(displayNode.node.id)
                    }
                },
            )
        }
    }
}

private fun flattenTree(
    nodes: List<FolderTreeNode>,
    expandedFolderIds: Set<Long?>,
    depth: Int = 0,
): List<FolderTreeNodeWithDepth> =
    nodes.flatMap { node ->
        val isExpanded = node.id in expandedFolderIds
        val folderTreeNodeWithDepth = FolderTreeNodeWithDepth(node, depth)

        listOf(folderTreeNodeWithDepth) +
            if (isExpanded && node.children.isNotEmpty()) {
                flattenTree(node.children, expandedFolderIds, depth + 1)
            } else {
                emptyList()
            }
    }
