package com.pdf.semantic.presentation.pdflist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.pdf.semantic.R
import com.pdf.semantic.presentation.components.IconButtonWithLabel

@Composable
fun MultiSelectBottomBar(
    modifier: Modifier = Modifier,
    onMoveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        IconButtonWithLabel(
            onClick = onMoveClick,
            iconPainter = painterResource(id = R.drawable.move_dir_24),
            contentDescription = "Move Items",
            labelText = "이동",
        )

        IconButtonWithLabel(
            onClick = onDeleteClick,
            iconPainter = painterResource(id = R.drawable.trash_24),
            contentDescription = "Delete Document",
            labelText = "삭제",
        )
    }
}
