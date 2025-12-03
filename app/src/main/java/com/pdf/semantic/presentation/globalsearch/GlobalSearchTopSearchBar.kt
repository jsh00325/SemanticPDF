package com.pdf.semantic.presentation.globalsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdf.semantic.R
import com.pdf.semantic.presentation.components.AIExpansionToggleButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchTopSearchBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isExpansionOn: Boolean,
    onExpansionToggleClick: () -> Unit,
    hasShownGuide: Boolean,
    onGuideShown: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val performSearch = {
        onSearch()
        keyboardController?.hide()
        focusManager.clearFocus()
    }
    val contentTextStyle =
        androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            color = Color.Black,
        )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back",
                )
            }

            BasicTextField(
                modifier = Modifier.weight(1f),
                value = query,
                onValueChange = onQueryChange,
                textStyle = contentTextStyle,
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { performSearch() }),
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "전체에서 검색",
                                    style = LocalTextStyle.current.copy(color = Color.Gray),
                                )
                            }
                            innerTextField()
                        }

                        if (query.isNotEmpty()) {
                            Icon(
                                painter = painterResource(id = R.drawable.close_24),
                                contentDescription = "Clear search query",
                                tint = Color.Gray,
                                modifier =
                                    Modifier
                                        .size(32.dp)
                                        .padding(8.dp)
                                        .clickable { onQueryChange("") },
                            )
                        }
                    }
                },
            )

            val tooltipState = rememberTooltipState(isPersistent = true)
            if (!hasShownGuide) {
                onGuideShown()
                LaunchedEffect(Unit) {
                    tooltipState.show()
                }
            }

            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Below,
                    ),
                tooltip = {
                    PlainTooltip(caretShape = TooltipDefaults.caretShape()) {
                        Column {
                            Text("질문의 의도를 파악하여 검색 정확도를 높입니다.\n(인터넷 연결 필요)")
                        }
                    }
                },
                state = tooltipState,
            ) {
                AIExpansionToggleButton(
                    isEnabled = isExpansionOn,
                    onClick = onExpansionToggleClick,
                )
            }

            IconButton(onClick = performSearch) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = "Search",
                )
            }
        }

        HorizontalDivider()
    }
}
