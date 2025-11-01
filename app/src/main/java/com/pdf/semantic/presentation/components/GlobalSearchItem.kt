package com.pdf.semantic.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.pdf.semantic.domain.model.GlobalSearchResult

@Composable
fun GlobalSearchItem(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    globalSearchResult: GlobalSearchResult,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = globalSearchResult.pdfTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )

                Text(
                    text = "${globalSearchResult.slideNumber} / ${globalSearchResult.totalPages}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}
