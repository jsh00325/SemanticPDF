package com.pdf.semantic.presentation.pdflist

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdf.semantic.domain.model.FolderItem
import com.pdf.semantic.presentation.components.CircularCheckBox

@Composable
fun FolderGridItem(
    modifier: Modifier = Modifier,
    isMultiSelectMode: Boolean,
    isSelected: Boolean,
    onItemClick: () -> Unit = {},
    onItemLongClick: () -> Unit = {},
    item: FolderItem,
) {
    Card(
        modifier =
            modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onItemLongClick() },
                        onTap = { onItemClick() },
                    )
                }.aspectRatio(1.3f),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 4.dp,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
        ) {
            if (isMultiSelectMode) {
                CircularCheckBox(
                    checked = isSelected,
                    onCheckedChange = { onItemClick() },
                    modifier = Modifier.align(Alignment.TopStart).size(24.dp),
                )
            }

            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
            )
        }
    }
}
