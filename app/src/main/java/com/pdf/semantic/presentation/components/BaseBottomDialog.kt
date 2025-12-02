package com.pdf.semantic.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun BaseBottomDialog(
    onDismissRequest: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit),
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismissRequest,
                    ).padding(horizontal = 16.dp, vertical = 32.dp)
                    .imePadding(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 5.dp,
            ) {
                Column(
                    modifier =
                        Modifier
                            .heightIn(max = screenHeight * 0.9f)
                            .padding(24.dp),
                    content = content,
                )
            }
        }
    }
}
