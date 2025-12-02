package com.pdf.semantic.presentation.pdflist.folderlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.model.FolderTreeNode
import com.pdf.semantic.domain.usecase.pdflist.MoveFoldersAndPdfsUsecase
import com.pdf.semantic.domain.usecase.pdflist.ObserveFolderTreeUsecase
import com.pdf.semantic.presentation.LongArrayStringConverter.toLongList
import com.pdf.semantic.presentation.MoveItemsDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderListViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        observeFolderTree: ObserveFolderTreeUsecase,
        private val moveFoldersAndPdfs: MoveFoldersAndPdfsUsecase,
    ) : ViewModel() {
        private val selectedFolderIds: List<Long> =
            savedStateHandle.get<String>(MoveItemsDialog.FOLDER_IDS_JSON_ARG).toLongList()
        private val selectedPdfIds: List<Long> =
            savedStateHandle.get<String>(MoveItemsDialog.PDF_IDS_JSON_ARG).toLongList()
        val currentFolderIdForDialog: Long? =
            savedStateHandle.get<Long>(MoveItemsDialog.CURRENT_FOLDER_ID_ARG).takeIf { it != -1L }

        val folderTreeNode: StateFlow<FolderTreeNode> =
            observeFolderTree()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue =
                        FolderTreeNode(
                            id = null,
                            name = "폴더",
                            children = emptyList(),
                        ),
                )

        val forbiddenIds: StateFlow<Set<Long?>> =
            folderTreeNode
                .map { currentRootNode ->
                    findForbiddenTargetIds(currentRootNode, selectedFolderIds.toSet())
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptySet(),
                )

        private fun findForbiddenTargetIds(
            rootNode: FolderTreeNode,
            movingIds: Set<Long>,
        ): Set<Long?> {
            val forbiddenIds = mutableSetOf<Long?>()

            fun collectAllDescendants(node: FolderTreeNode) {
                forbiddenIds.add(node.id)
                node.children.forEach(::collectAllDescendants)
            }

            fun findMovingNodes(currentNode: FolderTreeNode) {
                if (currentNode.id in movingIds) {
                    collectAllDescendants(currentNode)
                } else {
                    currentNode.children.forEach(::findMovingNodes)
                }
            }

            findMovingNodes(rootNode)
            return forbiddenIds
        }

        fun onItemsMove(targetFolderId: Long?) {
            viewModelScope.launch {
                moveFoldersAndPdfs(
                    folderIds = selectedFolderIds,
                    pdfIds = selectedPdfIds,
                    newParentId = targetFolderId,
                )
            }
        }
    }
