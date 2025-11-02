package com.pdf.semantic.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.placeholder
import com.pdf.semantic.R
import com.pdf.semantic.domain.model.EmbeddingStatus
import com.pdf.semantic.domain.model.PdfItem
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PdfListItem(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    onItemLongClick: () -> Unit = {},
    pdfItem: PdfItem,
) {
    val thumbnailPainter =
        rememberAsyncImagePainter(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(pdfItem.thumbnailPath)
                    .placeholder(R.drawable.placeholder)
                    .build(),
        )

    Column(
        modifier =
            modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onItemLongClick() },
                        onTap = { onItemClick() },
                    )
                },
    ) {
        Box(
            modifier = Modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(4.dp)),
        ) {
            Image(
                painter = thumbnailPainter,
                contentDescription = "PDF Slide Image",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.41f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray),
                contentScale = ContentScale.Fit,
            )

            if (pdfItem.status == EmbeddingStatus.COMPLETE) {
                Image(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(16.dp),
                )
            } else {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(16.dp),
                    progress = {
                        pdfItem.progressedPages.toFloat() / pdfItem.totalPages.toFloat()
                    },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            text = pdfItem.title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
        )

        val format = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault())
        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            text = format.format(pdfItem.createdTime),
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
    }
}
