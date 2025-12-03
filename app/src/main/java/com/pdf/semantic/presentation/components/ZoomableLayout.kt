package com.pdf.semantic.presentation.components

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun ZoomableLayout(
    modifier: Modifier = Modifier,
    resetKey: Any? = null,
    minScale: Float = 1f,
    maxScale: Float = 5f,
    content: @Composable () -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(resetKey) {
        if (resetKey != null) {
            scale = 1f
            offset = Offset.Zero
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clipToBounds()
                .onSizeChanged { size = it }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                        val maxOffsetX = (size.width * (newScale - 1)) / 2f
                        val maxOffsetY = (size.height * (newScale - 1)) / 2f

                        val newOffset =
                            if (newScale > 1f) {
                                val newX = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                val newY = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                                Offset(newX, newY)
                            } else {
                                Offset.Zero
                            }

                        scale = newScale
                        offset = newOffset
                    }
                }.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
    ) {
        content()
    }
}
