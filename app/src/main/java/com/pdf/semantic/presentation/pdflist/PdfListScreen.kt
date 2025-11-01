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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(pdfList, key = { it.id }) { item ->
                PdfListItem(
                    onItemClick = { onPdfClick(item.id) },
                    onItemDelete = { viewModel.onPdfDeleted(item.id) },
                    pdfItem = item,
                )
            }
        }
    }
}
