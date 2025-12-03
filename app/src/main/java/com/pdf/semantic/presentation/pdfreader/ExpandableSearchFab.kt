package com.pdf.semantic.presentation.pdfreader

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pdf.semantic.presentation.components.AIExpansionToggleButton

@Composable
fun ExpandableSearchFab(
    isExpanded: Boolean,
    query: String,
    resultCount: Int,
    currentIndex: Int,
    isSearching: Boolean,
    isExpansionOn: Boolean,
    onExpandChange: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onExpansionToggle: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isNavMode = resultCount > 0

    val fabWidth by animateDpAsState(
        targetValue = if (isExpanded || isNavMode) 380.dp else 56.dp,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "FabWidth",
    )

    LaunchedEffect(isExpanded) {
        if (isExpanded && !isNavMode) {
            focusRequester.requestFocus()
        } else if (!isExpanded) {
            focusManager.clearFocus()
        }
    }

    Surface(
        modifier =
            Modifier
                .height(56.dp)
                .width(fabWidth),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(56.dp)
                        .clickable(
                            enabled = !isNavMode && !isExpanded && !isSearching,
                        ) { onExpandChange() },
                contentAlignment = Alignment.Center,
            ) {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        strokeWidth = 3.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded || isNavMode,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
                modifier = Modifier.weight(1f),
            ) {
                if (isNavMode) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(end = 8.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "${currentIndex + 1} / $resultCount",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        IconButton(onClick = onPrevClick) {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                "Prev",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        IconButton(onClick = onNextClick) {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                "Next",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        IconButton(onClick = onClearClick) {
                            Icon(
                                Icons.Filled.Close,
                                "Clear",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp),
                    ) {
                        BasicTextField(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            maxLines = 1,
                            keyboardOptions =
                                KeyboardOptions(
                                    imeAction =
                                        ImeAction.Search,
                                ),
                            keyboardActions =
                                KeyboardActions(onSearch = {
                                    onSearch()
                                    focusManager.clearFocus()
                                }),
                            decorationBox = { innerTextField ->
                                TextFieldDefaults.DecorationBox(
                                    value = query,
                                    innerTextField = innerTextField,
                                    enabled = true,
                                    singleLine = true,
                                    visualTransformation = VisualTransformation.None,
                                    interactionSource = remember { MutableInteractionSource() },
                                    placeholder = {
                                        Text(
                                            "검색어 입력",
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                        )
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    colors =
                                        TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor =
                                                MaterialTheme
                                                    .colorScheme.onPrimaryContainer,
                                            focusedTextColor =
                                                MaterialTheme
                                                    .colorScheme.onPrimaryContainer,
                                            unfocusedTextColor =
                                                MaterialTheme
                                                    .colorScheme.onPrimaryContainer,
                                        ),
                                )
                            },
                        )
//                        TextField(
//                            value = query,
//                            onValueChange = onQueryChange,
//                            modifier =
//                                Modifier
//                                    .weight(1f)
//                                    .focusRequester(focusRequester),
//                            placeholder = {
//                                Text(
//                                    "검색어 입력",
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    maxLines = 1,
//                                )
//                            },
//                            singleLine = true,
//                            colors =
//                                TextFieldDefaults.colors(
//                                    focusedContainerColor = Color.Transparent,
//                                    unfocusedContainerColor = Color.Transparent,
//                                    focusedIndicatorColor = Color.Transparent,
//                                    unfocusedIndicatorColor = Color.Transparent,
//                                    cursorColor =
//                                        MaterialTheme
//                                            .colorScheme.onPrimaryContainer,
//                                    focusedTextColor =
//                                        MaterialTheme
//                                            .colorScheme.onPrimaryContainer,
//                                    unfocusedTextColor =
//                                        MaterialTheme
//                                            .colorScheme.onPrimaryContainer,
//                                ),
//                            keyboardOptions =
//                                KeyboardOptions(
//                                    imeAction =
//                                        ImeAction.Search,
//                                ),
//                            keyboardActions =
//                                KeyboardActions(onSearch = {
//                                    onSearch()
//                                    focusManager.clearFocus()
//                                }),
//                        )
                        AIExpansionToggleButton(
                            isEnabled = isExpansionOn,
                            onClick = onExpansionToggle,
                        )

                        IconButton(
                            onClick = {
                                onExpandChange()
                                onQueryChange("")
                            },
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                "Close",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
)
@Composable
fun ExpandableSearchFabPreview() {
}
