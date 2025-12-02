package com.pdf.semantic.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeleteItemDialog(
    bodyText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomDialog(onDismissRequest = onDismiss) {
        Text(
            text = bodyText,
            fontSize = 16.sp,
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1.0f),
            ) {
                Text(
                    text = "취소",
                    fontSize = 16.sp,
                )
            }

            VerticalDivider(Modifier.height(24.dp).padding(horizontal = 8.dp))

            TextButton(
                onClick = onConfirm,
                modifier = Modifier.weight(1.0f),
            ) {
                Text(
                    text = "삭제",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
