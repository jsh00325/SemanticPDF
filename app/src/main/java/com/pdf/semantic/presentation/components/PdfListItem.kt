package com.pdf.semantic.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    onItemDelete: () -> Unit = {},
    pdfItem: PdfItem,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "삭제 확인") },
            text = { Text(text = "이 항목을 정말로 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onItemDelete()
                        showDeleteDialog = false
                    },
                ) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            },
        )
    }

    val thumbnailPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pdfItem.thumbnailPath)
            .placeholder(R.drawable.placeholder)
            .build()
    )

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { showDeleteDialog = true },
                        onTap = { onItemClick() },
                    )
                },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // TODO: 추후 이미지 받아서 처리
            Box {

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
                                .size(24.dp),
                    )
                } else {
                    CircularProgressIndicator(
                        modifier =
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(24.dp),
                        progress = {
                            pdfItem.progressedPages.toFloat() / pdfItem.totalPages.toFloat()
                        },
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = pdfItem.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )

                val format = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault())
                Text(
                    text = format.format(pdfItem.createdTime),
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}
