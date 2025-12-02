package com.pdf.semantic.presentation.pdflist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pdf.semantic.R

@Composable
fun PdfListTopBar(
    modifier: Modifier = Modifier,
    onFolderListClick: () -> Unit,
    currentFolderName: String,
    onGlobalSearchClick: () -> Unit,
    onAddFolderClick: () -> Unit,
    onAddPdfClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onFolderListClick) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_menu_24),
                contentDescription = "Folder List",
            )
        }

        Text(
            text = currentFolderName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp).weight(1.0f),
        )

        IconButton(onClick = onGlobalSearchClick) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_search_24),
                contentDescription = "Global Search",
            )
        }

        IconButton(onClick = onAddFolderClick) {
            Icon(
                painter = painterResource(id = R.drawable.folder_24),
                contentDescription = "New Folder",
            )
        }

        IconButton(onClick = onAddPdfClick) {
            Icon(
                painter = painterResource(id = R.drawable.file_24),
                contentDescription = "Add PDF",
            )
        }
    }
}
