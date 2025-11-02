package com.pdf.semantic.presentation.pdflist

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdf.semantic.domain.model.PdfItem
import com.pdf.semantic.presentation.components.PdfListItem

@Composable
fun PdfListScreen(
    onGlobalSearchClick: () -> Unit,
    onPdfClick: (Long) -> Unit,
    viewModel: PdfListViewModel = hiltViewModel(),
) {
    val pdfList by viewModel.pdfList.collectAsState()
    val filePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let { viewModel.onPdfAdded(it) }
        }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedPdfItem by remember { mutableStateOf<PdfItem?>(null) }

    if (showDeleteDialog && selectedPdfItem != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "삭제 확인") },
            text = { Text(text = "'${selectedPdfItem!!.title}' 항목을 정말로 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onPdfDeleted(selectedPdfItem!!.id)
                        showDeleteDialog = false
                    },
                ) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            },
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(onClick = onGlobalSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Global Search",
                )
            }

            IconButton(onClick = { filePickerLauncher.launch("application/pdf") }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add PDF",
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(pdfList, key = { it.id }) { item ->
                PdfListItem(
                    modifier = Modifier.fillMaxWidth(),
                    onItemClick = { onPdfClick(item.id) },
                    onItemLongClick = {
                        selectedPdfItem = item
                        showDeleteDialog = true
                    },
                    pdfItem = item,
                )
            }
        }
    }
}
