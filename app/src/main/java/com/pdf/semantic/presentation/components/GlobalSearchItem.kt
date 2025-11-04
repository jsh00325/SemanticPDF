package com.pdf.semantic.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdf.semantic.presentation.globalsearch.GlobalSearchUiItem

@Composable
fun GlobalSearchItem(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    uiItem: GlobalSearchUiItem,
) {
    Column(modifier = modifier.clickable(onClick = onItemClick)) {
        if (uiItem.slidePreviewImage == null) {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.41f),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            Image(
                bitmap = uiItem.slidePreviewImage.asImageBitmap(),
                contentDescription = "PDF Slide Image",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = uiItem.pdfTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )

            Text(
                text = "Page ${uiItem.slideNumber}/${uiItem.totalPages}",
                fontSize = 14.sp,
                color = Color.Gray,
            )
        }
    }
}
