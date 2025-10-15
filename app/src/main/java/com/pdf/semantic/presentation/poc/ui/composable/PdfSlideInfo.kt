package com.pdf.semantic.presentation.poc.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pdf.semantic.domain.model.Slide

@Composable
fun PdfSlideInfo(
    modifier: Modifier = Modifier,
    slide: Slide,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "페이지: ${slide.slideNumber}",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(Modifier.weight(1.0f))

                slide.similarity?.let {
                    Text(
                        text = "유사도: %.3f".format(it),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = slide.content,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
