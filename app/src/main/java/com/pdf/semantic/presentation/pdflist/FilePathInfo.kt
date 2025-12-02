package com.pdf.semantic.presentation.pdflist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdf.semantic.R
import com.pdf.semantic.domain.model.FolderItem

@Composable
fun FilePathInfo(
    modifier: Modifier = Modifier,
    folderPath: List<FolderItem>,
    onFolderClick: (Long?) -> Unit,
) {
    val scrollState = rememberLazyListState()
    LaunchedEffect(folderPath) {
        if (folderPath.isNotEmpty()) {
            scrollState.animateScrollToItem(scrollState.layoutInfo.totalItemsCount - 1)
        }
    }

    LazyRow(
        modifier = modifier,
        state = scrollState,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            IconButton(
                onClick = { onFolderClick(null) },
            ) {
                Icon(
                    painter = painterResource(R.drawable.folder_24),
                    contentDescription = "root",
                )
            }
        }

        itemsIndexed(
            items = folderPath,
            key = { _, folder -> folder.id },
        ) { index, folder ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = LocalContentColor.current.copy(alpha = 0.4f),
                )

                Box(
                    modifier =
                        Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onFolderClick(folder.id) },
                ) {
                    Text(
                        text = folder.name,
                        fontSize = 16.sp,
                        fontWeight =
                            if (index ==
                                folderPath.lastIndex
                            ) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}
