package com.pdf.semantic.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun IconButtonWithLabel(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconPainter: Painter,
    contentDescription: String?,
    labelText: String,
) {
    Column(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.small)
                .clickable(
                    onClick = onClick,
                    role = Role.Button,
                ).padding(vertical = 8.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = contentDescription,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = labelText,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
