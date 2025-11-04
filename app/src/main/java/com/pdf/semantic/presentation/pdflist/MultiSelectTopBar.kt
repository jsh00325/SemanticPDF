package com.pdf.semantic.presentation.pdflist

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MultiSelectTopBar(
    modifier: Modifier = Modifier,
    isAllSelected: Boolean,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    selectedCount: Int,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isAllSelected,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    onSelectAll()
                } else {
                    onDeselectAll()
                }
            },
        )

        Text("${selectedCount}개 선택됨")
    }
}
