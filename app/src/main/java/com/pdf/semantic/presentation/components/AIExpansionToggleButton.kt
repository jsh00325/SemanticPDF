package com.pdf.semantic.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import com.pdf.semantic.R

@Composable
fun AIExpansionToggleButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.sparkles_24),
            contentDescription = if (isEnabled) "AI 확장 검색 켜짐" else "AI 확장 검색 꺼짐",
            tint =
                if (isEnabled) {
                    Color.Unspecified
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            modifier =
                if (isEnabled) {
                    val rainbowGradientImage =
                        ImageBitmap.imageResource(
                            id = R.drawable.rainbow_gradient,
                        )
                    Modifier
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawImage(
                                    image = rainbowGradientImage,
                                    dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                                    blendMode = BlendMode.SrcIn,
                                )
                            }
                        }
                } else {
                    Modifier
                },
        )
    }
}
