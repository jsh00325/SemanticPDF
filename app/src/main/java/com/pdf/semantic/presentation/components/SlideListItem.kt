package com.pdf.semantic.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.Bitmap
import kotlinx.coroutines.delay

private val HighlightColor = Color(0xFF1A237E)

@Composable
fun SlideListItem(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier,
    pageText: String,
    isHighlighted: Boolean = false,
) {
    val highlightAlpha = remember { Animatable(0f) }

    LaunchedEffect(isHighlighted) {
        if (isHighlighted) {
            highlightAlpha.snapTo(0.6f)
            delay(500)
            highlightAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            )
        } else {
            highlightAlpha.snapTo(0f)
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            color = Color.White,
            shadowElevation = 2.dp,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Page $pageText",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.414f / 1f)
                                .background(Color.White),
                    )
                }

                if (highlightAlpha.value > 0f) {
                    Box(
                        modifier =
                            Modifier
                                .matchParentSize()
                                .border(
                                    width = 5.dp,
                                    color = HighlightColor.copy(alpha = highlightAlpha.value),
                                    shape = RoundedCornerShape(4.dp),
                                ),
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = pageText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
