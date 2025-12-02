package com.pdf.semantic.presentation.pdflist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdf.semantic.R
import com.pdf.semantic.domain.model.FolderItem
import com.pdf.semantic.presentation.components.CircularCheckBox

private val folderColor = Color(0xFFFBC02D)

@Composable
fun FolderGridItem(
    modifier: Modifier = Modifier,
    isMultiSelectMode: Boolean,
    isSelected: Boolean,
    onItemClick: () -> Unit = {},
    onItemLongClick: () -> Unit = {},
    item: FolderItem,
) {
    val borderColor =
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        }
    val backgroundColor =
        if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        }
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier =
            modifier
                .aspectRatio(1.2f)
                .clip(shape)
                .background(backgroundColor)
                .border(width = 1.dp, color = borderColor, shape = shape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onItemLongClick() },
                        onTap = { onItemClick() },
                    )
                },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_folder_24),
                contentDescription = "Folder Icon",
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxSize(0.5f),
                tint = folderColor,
            )

            Text(
                text = item.name,
                style =
                    MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (isMultiSelectMode) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
            ) {
                CircularCheckBox(
                    checked = isSelected,
                    onCheckedChange = { onItemClick() },
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
