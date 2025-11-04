package com.pdf.semantic.presentation.pdflist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.pdf.semantic.R

@Composable
fun PdfListTopBar(
    modifier: Modifier = Modifier,
    onGlobalSearchClick: () -> Unit,
    onPdfAddClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        IconButton(onClick = onGlobalSearchClick) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_search_24),
                contentDescription = "Global Search",
            )
        }

        IconButton(onClick = onPdfAddClick) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = "Add PDF",
            )
        }
    }
}
