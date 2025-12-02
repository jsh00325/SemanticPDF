package com.pdf.semantic.presentation.pdflist.folderlist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdf.semantic.R
import com.pdf.semantic.domain.model.FolderTreeNode

@Composable
fun FolderTreeItem(
    modifier: Modifier = Modifier,
    node: FolderTreeNode,
    depth: Int,
    currentFolderId: Long?,
    isExpanded: Boolean,
    isSelectable: Boolean,
    onExpandClick: (FolderTreeNode) -> Unit,
    onFolderClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = (depth * 20).dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FolderIcon(
            hasChildren = node.children.isNotEmpty(),
            isExpanded = isExpanded,
            isSelectable = isSelectable,
            onExpandClick = { onExpandClick(node) },
        )
        Text(
            text = node.name,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .weight(1.0f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        enabled = isSelectable,
                        onClick = { onFolderClick() },
                    ).padding(12.dp),
            color = LocalContentColor.current.copy(alpha = if (isSelectable) 1f else 0.5f),
            fontWeight =
                if (node.id == currentFolderId) {
                    androidx.compose.ui.text.font.FontWeight.Bold
                } else {
                    androidx.compose.ui.text.font.FontWeight.Normal
                },
        )
    }
}

@Composable
private fun FolderIcon(
    hasChildren: Boolean,
    isExpanded: Boolean,
    isSelectable: Boolean,
    onExpandClick: () -> Unit,
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "ArrowRotation",
    )

    Row(
        modifier =
            Modifier
                .size(width = 52.dp, height = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    onClick = onExpandClick,
                    enabled = hasChildren,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Spacer(Modifier.width(4.dp))

        Box(modifier = Modifier.size(16.dp)) {
            if (hasChildren) {
                Icon(
                    painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
                    contentDescription = "Expand/Collapse Icon",
                    modifier =
                        Modifier
                            .size(16.dp)
                            .rotate(rotationAngle),
                    tint =
                        LocalContentColor.current.copy(
                            alpha = if (isSelectable) 1f else 0.5f,
                        ),
                )
            }
        }

        Icon(
            painter =
                painterResource(
                    if (hasChildren && isExpanded) {
                        R.drawable.open_folder_24
                    } else {
                        R.drawable.close_folder_24
                    },
                ),
            contentDescription = "Folder Icon",
            modifier = Modifier.size(24.dp),
            tint = LocalContentColor.current.copy(alpha = if (isSelectable) 1f else 0.5f),
        )

        Spacer(modifier = Modifier.width(8.dp))
    }
}
