package com.pdf.semantic.presentation.pdflist

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PdfListScreen(
    onGlobalSearchClick: () -> Unit,
    onPdfClick: (Long) -> Unit,
    viewModel: PdfListViewModel = hiltViewModel(),
) {
    val pdfItems by viewModel.pdfList.collectAsState()
    val filePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let { viewModel.onPdfAdded(it) }
        }

    val isMultiSelectMode by viewModel.isMultiSelectMode.collectAsState()
    val selectedPdfIds by viewModel.selectedPdfIds.collectAsState()
    val isAllSelected by viewModel.isAllSelected.collectAsState()

    BackHandler(enabled = isMultiSelectMode) {
        viewModel.disableMultiSelectMode()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "삭제 확인") },
            text = { Text(text = "${selectedPdfIds.size}개의 항목을 정말로 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSelectedPdf()
                        showDeleteDialog = false
                    },
                ) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            },
        )
    }

    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = isMultiSelectMode,
                label = "TopBarAnimation",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(8.dp),
                transitionSpec = {
                    (
                        slideInVertically(
                            animationSpec = tween(ANIMATION_DURATION_MILLIS),
                            initialOffsetY = { -it },
                        ) + fadeIn(animationSpec = tween(ANIMATION_DURATION_MILLIS))
                    ).togetherWith(
                        slideOutVertically(
                            animationSpec = tween(ANIMATION_DURATION_MILLIS),
                            targetOffsetY = { -it },
                        ) + fadeOut(animationSpec = tween(ANIMATION_DURATION_MILLIS)),
                    )
                },
            ) { isMultiMode ->
                if (isMultiMode) {
                    MultiSelectTopBar(
                        modifier = Modifier.fillMaxSize(),
                        isAllSelected = isAllSelected,
                        onSelectAll = viewModel::selectAll,
                        onDeselectAll = viewModel::deselectAll,
                        selectedCount = selectedPdfIds.size,
                    )
                } else {
                    PdfListTopBar(
                        modifier = Modifier.fillMaxSize(),
                        onGlobalSearchClick = onGlobalSearchClick,
                        onPdfAddClick = { filePickerLauncher.launch("application/pdf") },
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isMultiSelectMode,
                enter =
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(ANIMATION_DURATION_MILLIS),
                    ),
                exit =
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(ANIMATION_DURATION_MILLIS),
                    ),
            ) {
                MultiSelectBottomBar(
                    modifier = Modifier.fillMaxWidth(),
                    onDeleteClick = { showDeleteDialog = true },
                )
            }
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(pdfItems, key = { it.id }) { pdfItem ->
                val onItemClick =
                    remember(isMultiSelectMode, pdfItem.id) {
                        {
                            if (isMultiSelectMode) {
                                viewModel.onClickInMultiSelectMode(pdfItem.id)
                            } else {
                                onPdfClick(pdfItem.id)
                            }
                        }
                    }

                val onItemLongClick =
                    remember(pdfItem.id) {
                        {
                            viewModel.onLongClick(pdfItem.id)
                        }
                    }

                PdfListItem(
                    modifier = Modifier.fillMaxWidth(),
                    isMultiSelectMode = isMultiSelectMode,
                    isSelected = selectedPdfIds.contains(pdfItem.id),
                    onItemClick = onItemClick,
                    onItemLongClick = onItemLongClick,
                    pdfItem = pdfItem,
                )
            }
        }
    }
}

private const val ANIMATION_DURATION_MILLIS = 250
