package com.pdf.semantic.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdf.semantic.R
import com.pdf.semantic.domain.model.EmbeddingStatus
import com.pdf.semantic.domain.model.PdfItem
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PdfListItem(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    pdfItem: PdfItem,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .clickable { onItemClick() },
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
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "PDF Slide Image",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.41f)
                            .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop,
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
