package com.pdf.semantic.presentation.pdflist

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdf.semantic.domain.model.FolderInfo
import com.pdf.semantic.domain.model.FoldersAndPdfs
import com.pdf.semantic.domain.usecase.pdflist.AddFolderUsecase
import com.pdf.semantic.domain.usecase.pdflist.AddPdfUsecase
import com.pdf.semantic.domain.usecase.pdflist.DeleteFoldersAndPdfsUsecase
import com.pdf.semantic.domain.usecase.pdflist.ObserveFolderInfoUsecase
import com.pdf.semantic.domain.usecase.pdflist.ObserveFoldersAndPdfsUsecase
import com.pdf.semantic.presentation.PdfList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class SelectionState(
    val isMultiSelectMode: Boolean = false,
    val selectedFolderIds: Set<Long> = emptySet(),
    val selectedPdfIds: Set<Long> = emptySet(),
    val isAllSelected: Boolean = false,
)

@HiltViewModel
class PdfListViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        observeFoldersAndPdfs: ObserveFoldersAndPdfsUsecase,
        observeFolderInfo: ObserveFolderInfoUsecase,
        private val addFolder: AddFolderUsecase,
        private val addPdf: AddPdfUsecase,
        private val deleteFoldersAndPdfs: DeleteFoldersAndPdfsUsecase,
    ) : ViewModel() {
        val currentFolderId: Long? =
            savedStateHandle.get<Long>(PdfList.PARENT_ID_ARG)?.takeIf { it != -1L }

        private val currentFolderInfo: StateFlow<FolderInfo> =
            observeFolderInfo(currentFolderId).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = FolderInfo("폴더", emptyList()),
            )

        private val foldersAndPdfs: StateFlow<FoldersAndPdfs> =
            observeFoldersAndPdfs(currentFolderId).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = FoldersAndPdfs(emptyList(), emptyList()),
            )

        private val isMultiSelectMode = MutableStateFlow(false)
        private val selectedFolderIds = MutableStateFlow(emptySet<Long>())
        private val selectedPdfIds = MutableStateFlow(emptySet<Long>())
        private val isAllSelected = MutableStateFlow(false)

        private val selectionState: StateFlow<SelectionState> =
            combine(
                isMultiSelectMode,
                selectedFolderIds,
                selectedPdfIds,
                isAllSelected,
            ) { isMultiMode, folderIds, pdfIds, isAll ->
                SelectionState(
                    isMultiMode,
                    folderIds,
                    pdfIds,
                    isAll,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SelectionState(),
            )

        val uiState: StateFlow<PdfListUiState> =
            combine(
                currentFolderInfo,
                foldersAndPdfs,
                selectionState,
            ) { folderInfo, foldersAndPdfs, selectionState ->
                PdfListUiState(
                    currentFolderName = folderInfo.name,
                    folderPath = folderInfo.folderPath,
                    folders = foldersAndPdfs.folders,
                    pdfs = foldersAndPdfs.pdfs,
                    isMultiSelectMode = selectionState.isMultiSelectMode,
                    selectedFolderIds = selectionState.selectedFolderIds,
                    selectedPdfIds = selectionState.selectedPdfIds,
                    isAllSelected = selectionState.isAllSelected,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PdfListUiState(),
            )

        private fun updateIsAllSelected() {
            val items = foldersAndPdfs.value
            val totalSize = items.folders.size + items.folders.size
            val curSize = selectedPdfIds.value.size + selectedFolderIds.value.size
            isAllSelected.value = totalSize > 0 && curSize == totalSize
        }

        fun onNewFolderClick(name: String) {
            viewModelScope.launch {
                addFolder(
                    name = name,
                    parentId = currentFolderId,
                )
            }
        }

        fun onNewPdfClick(uri: Uri) {
            viewModelScope.launch {
                addPdf(
                    uriString = uri.toString(),
                    parentId = currentFolderId,
                )
            }
        }

        fun onFolderLongClick(folderId: Long) {
            isMultiSelectMode.value = true
            selectedFolderIds.value = selectedFolderIds.value + folderId
            updateIsAllSelected()
        }

        fun onPdfLongClick(pdfId: Long) {
            isMultiSelectMode.value = true
            selectedPdfIds.value = selectedPdfIds.value + pdfId
            updateIsAllSelected()
        }

        fun deleteSelectedPdf() {
            viewModelScope.launch {
                selectedPdfIds.value.forEach { pdfId ->
                    // deletePdf(pdfId)
                }
                isMultiSelectMode.value = false
                selectedPdfIds.value = emptySet()
            }
            disableMultiSelectMode()
        }

        fun onFolderClickInMultiMode(folderId: Long?) {
            if (folderId == null) return
            if (!isMultiSelectMode.value) return

            val currentFolderIds = selectedFolderIds.value
            selectedFolderIds.value =
                if (currentFolderIds.contains(folderId)) {
                    currentFolderIds - folderId
                } else {
                    currentFolderIds + folderId
                }
            updateIsAllSelected()
        }

        fun onPdfClickInMultiMode(pdfId: Long?) {
            if (pdfId == null) return
            if (!isMultiSelectMode.value) return

            val currentPdfIds = selectedPdfIds.value
            selectedPdfIds.value =
                if (currentPdfIds.contains(pdfId)) {
                    currentPdfIds - pdfId
                } else {
                    currentPdfIds + pdfId
                }
            updateIsAllSelected()
        }

        fun deleteSelectedItems() {
            viewModelScope.launch {
                deleteFoldersAndPdfs(
                    folderIds = selectedFolderIds.value.toList(),
                    pdfIds = selectedPdfIds.value.toList(),
                )
            }
        }

        fun onLongClick(pdfId: Long) {
            isMultiSelectMode.value = true
            selectedPdfIds.value = selectedPdfIds.value + pdfId
        }

        fun disableMultiSelectMode() {
            isMultiSelectMode.value = false
            selectedFolderIds.value = emptySet()
            selectedPdfIds.value = emptySet()
            isAllSelected.value = false
        }

        fun selectAll() {
            isAllSelected.value = true
            val folderItems = foldersAndPdfs.value.folders
            val pdfItems = foldersAndPdfs.value.pdfs
            selectedFolderIds.value = folderItems.map { it.id }.toSet()
            selectedPdfIds.value = pdfItems.map { it.id }.toSet()
        }

        fun deselectAll() {
            isAllSelected.value = false
            selectedFolderIds.value = emptySet()
            selectedPdfIds.value = emptySet()
        }
    }
