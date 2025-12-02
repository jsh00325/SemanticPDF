package com.pdf.semantic.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    current: Int,
    total: Int,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val progress = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier.background(Color.White.copy(alpha = 0.5f)),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color),
        )
    }
}
