package com.pdf.semantic.presentation.pdflist.folderlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun FolderListDrawer(
    modifier: Modifier = Modifier,
    currentFolderId: Long? = null,
    onFolderClick: (Long?) -> Unit,
    viewModel: FolderListViewModel = hiltViewModel(),
) {
    val folderTreeNode by viewModel.folderTreeNode.collectAsState()

    ModalDrawerSheet(modifier = modifier.fillMaxWidth(0.85f)) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            FolderTree(
                modifier = Modifier.fillMaxWidth(),
                currentFolderId = currentFolderId,
                rootTreeNode = folderTreeNode,
                onFolderClick = { targetFolderId ->
                    onFolderClick(targetFolderId)
                },
                initiallyExpandedFolderId = currentFolderId,
            )
        }
    }
}
