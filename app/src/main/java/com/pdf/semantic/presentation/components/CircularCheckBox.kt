package com.pdf.semantic.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CircularCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
) {
    val checkedColor = MaterialTheme.colorScheme.primary
    val uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) checkedColor else Color.Transparent,
        animationSpec = tween(100),
        label = "backgroundColor",
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) checkedColor else uncheckedColor,
        animationSpec = tween(100),
        label = "borderColor",
    )

    val checkmarkColor = MaterialTheme.colorScheme.onPrimary

    IconButton(
        onClick = { onCheckedChange?.invoke(!checked) },
        modifier = modifier,
        enabled = enabled,
    ) {
        Box(
            modifier =
                Modifier
                    .size(20.dp)
                    .background(backgroundColor, shape = CircleShape)
                    .border(width = 2.dp, color = borderColor, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = checkmarkColor,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
