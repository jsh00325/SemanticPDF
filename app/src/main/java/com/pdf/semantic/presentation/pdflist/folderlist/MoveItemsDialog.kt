package com.pdf.semantic.presentation.pdflist.folderlist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pdf.semantic.presentation.components.BaseBottomDialog

@Composable
fun MoveItemsDialog(
    onDismiss: () -> Unit,
    onMoveConfirm: () -> Unit,
    viewModel: FolderListViewModel = hiltViewModel(),
) {
    val folderTreeNode by viewModel.folderTreeNode.collectAsState()
    val forbiddenIds by viewModel.forbiddenIds.collectAsState()

    BaseBottomDialog(onDismissRequest = onDismiss) {
        Text(
            text = "이동할 폴더 선택",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        HorizontalDivider(Modifier.fillMaxWidth().padding(vertical = 8.dp))

        FolderTree(
            modifier = Modifier.fillMaxWidth(),
            currentFolderId = viewModel.currentFolderIdForDialog,
            rootTreeNode = folderTreeNode,
            forbiddenIds = forbiddenIds,
            onFolderClick = { targetFolderId ->
                viewModel.onItemsMove(targetFolderId)
                onMoveConfirm()
            },
        )
    }
}
