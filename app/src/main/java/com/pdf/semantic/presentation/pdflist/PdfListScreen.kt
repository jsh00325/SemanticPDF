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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pdf.semantic.presentation.components.DeleteItemDialog
import com.pdf.semantic.presentation.components.NewItemDialog
import com.pdf.semantic.presentation.pdflist.folderlist.FolderListDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PdfListScreen(
    onGlobalSearchClick: () -> Unit,
    onNavigateToFolder: (Long?) -> Unit,
    onNavigateToMoveDialog: (List<Long>, List<Long>) -> Unit,
    onNavigateToPdfReader: (Long) -> Unit,
    viewModel: PdfListViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val filePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let { viewModel.onNewPdfClick(it) }
        }

    var showNewFolderDialog by remember { mutableStateOf(false) }
    if (showNewFolderDialog) {
        NewItemDialog(
            titleText = "폴더 추가",
            textFieldLabelName = "폴더 이름",
            onDismiss = { showNewFolderDialog = false },
            onConfirm = { folderName ->
                viewModel.onNewFolderClick(folderName)
                showNewFolderDialog = false
            },
        )
    }

    var showDeleteItemsDialog by remember { mutableStateOf(false) }
    if (showDeleteItemsDialog) {
        val selectedFolderSize = uiState.selectedFolderIds.size
        val selectedPdfSize = uiState.selectedPdfIds.size

        if (selectedPdfSize + selectedFolderSize > 0) {
            var bodyText = ""
            if (selectedFolderSize > 0) bodyText = "폴더 ${selectedFolderSize}개와 폴더 내 모든 항목"
            if (selectedPdfSize > 0) {
                if (bodyText.isNotEmpty()) bodyText += "과 "
                bodyText += "PDF ${selectedPdfSize}개를"
            } else {
                bodyText += "을"
            }
            bodyText += " 정말로 삭제하시겠습니까?"

            DeleteItemDialog(
                bodyText = bodyText,
                onDismiss = { showDeleteItemsDialog = false },
                onConfirm = {
                    viewModel.deleteSelectedItems()
                    showDeleteItemsDialog = false
                    viewModel.disableMultiSelectMode()
                },
            )
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = uiState.isMultiSelectMode || drawerState.isOpen) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (uiState.isMultiSelectMode) {
            viewModel.disableMultiSelectMode()
        }
    }

    val onFolderItemClick: (Long?) -> Unit =
        remember(uiState.isMultiSelectMode) {
            { folderId ->
                if (uiState.isMultiSelectMode) {
                    viewModel.onFolderClickInMultiMode(folderId)
                } else {
                    onNavigateToFolder(folderId)
                }
            }
        }

    val onPdfItemClick: (Long) -> Unit =
        remember(uiState.isMultiSelectMode) {
            { documentId ->
                if (uiState.isMultiSelectMode) {
                    viewModel.onPdfClickInMultiMode(documentId)
                } else {
                    onNavigateToPdfReader(documentId)
                }
            }
        }

    val actions =
        remember(viewModel, scope, uiState.folderPath, onNavigateToFolder, onNavigateToMoveDialog) {
            PdfListActions(
                onNavigateToFolder = onNavigateToFolder,
                onNavigateToMoveDialog = onNavigateToMoveDialog,
                onFolderItemTap = onFolderItemClick,
                onPdfItemTap = onPdfItemClick,
                onFolderLongPress = viewModel::onFolderLongClick,
                onPdfLongPress = viewModel::onPdfLongClick,
                onPathFolderTap = { folderId ->
                    if (folderId != uiState.folderPath.lastOrNull()?.id) {
                        onNavigateToFolder(folderId)
                    }
                },
                onOpenDrawerClick = { scope.launch { drawerState.open() } },
                onGlobalSearchClick = onGlobalSearchClick,
                onAddFolderClick = { showNewFolderDialog = true },
                onAddPdfClick = { filePickerLauncher.launch("application/pdf") },
                onSelectAllClick = viewModel::selectAll,
                onDeselectAllClick = viewModel::deselectAll,
                onDeleteClick = { showDeleteItemsDialog = true },
            )
        }

    PdfListContent(
        modifier = Modifier.fillMaxSize(),
        currentFolderId = viewModel.currentFolderId,
        uiState = uiState,
        actions = actions,
        drawerState = drawerState,
        scope = scope,
    )
}

@Composable
private fun PdfListContent(
    modifier: Modifier = Modifier,
    currentFolderId: Long?,
    uiState: PdfListUiState,
    actions: PdfListActions,
    drawerState: DrawerState,
    scope: CoroutineScope,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FolderListDrawer(
                currentFolderId = currentFolderId,
                onFolderClick = { targetFolderId ->
                    actions.onNavigateToFolder(targetFolderId)
                    scope.launch { drawerState.close() }
                },
            )
        },
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                AnimatedContent(
                    targetState = uiState.isMultiSelectMode,
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
                            isAllSelected = uiState.isAllSelected,
                            onSelectAll = actions.onSelectAllClick,
                            onDeselectAll = actions.onDeselectAllClick,
                            selectedCount =
                                uiState.selectedFolderIds.size + uiState.selectedPdfIds.size,
                        )
                    } else {
                        PdfListTopBar(
                            modifier = Modifier.fillMaxSize(),
                            onFolderListClick = actions.onOpenDrawerClick,
                            currentFolderName = uiState.currentFolderName,
                            onGlobalSearchClick = actions.onGlobalSearchClick,
                            onAddFolderClick = actions.onAddFolderClick,
                            onAddPdfClick = actions.onAddPdfClick,
                        )
                    }
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = uiState.isMultiSelectMode,
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
                        onMoveClick = {
                            actions.onNavigateToMoveDialog(
                                uiState.selectedFolderIds.toList(),
                                uiState.selectedPdfIds.toList(),
                            )
                        },
                        onDeleteClick = actions.onDeleteClick,
                    )
                }
            },
        ) { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                if (uiState.folderPath.isNotEmpty()) {
                    FilePathInfo(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        folderPath = uiState.folderPath,
                        onFolderClick = actions.onPathFolderTap,
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = uiState.folders,
                        key = { "folder-${it.id}" },
                        span = { GridItemSpan(1) },
                    ) { folderItem ->
                        FolderGridItem(
                            isMultiSelectMode = uiState.isMultiSelectMode,
                            isSelected = uiState.selectedFolderIds.contains(folderItem.id),
                            onItemClick = { actions.onFolderItemTap(folderItem.id) },
                            onItemLongClick = { actions.onFolderLongPress(folderItem.id) },
                            item = folderItem,
                        )
                    }

                    if (uiState.folders.isNotEmpty() && uiState.pdfs.isNotEmpty()) {
                        item(span = { GridItemSpan(4) }) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    items(
                        items = uiState.pdfs,
                        key = { "pdf-${it.id}" },
                        span = { GridItemSpan(2) },
                    ) { documentItem ->
                        PdfGridItem(
                            modifier = Modifier.padding(vertical = 8.dp),
                            isMultiSelectMode = uiState.isMultiSelectMode,
                            isSelected = uiState.selectedPdfIds.contains(documentItem.id),
                            onItemClick = { actions.onPdfItemTap(documentItem.id) },
                            onItemLongClick = { actions.onPdfLongPress(documentItem.id) },
                            item = documentItem,
                        )
                    }
                }
            }
        }
    }
}

private const val ANIMATION_DURATION_MILLIS = 250
