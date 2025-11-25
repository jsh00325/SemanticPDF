package com.pdf.semantic.presentation.pdfreader

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdf.semantic.R
import com.pdf.semantic.presentation.components.SlideListItem

@Composable
fun PdfReaderScreen(
    onBackClick: () -> Unit,
    viewModel: PdfReaderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(uiState.currentResultIndex) {
        if (uiState.currentResultIndex >= 0 && uiState.searchResults.isNotEmpty()) {
            val targetSlide = uiState.searchResults[uiState.currentResultIndex]
            val targetIndex = (targetSlide.slideNumber - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back",
                    )
                }

                Text(
                    text = uiState.title,
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                ) {
                    items(
                        count = uiState.totalPages,
                        key = { index -> index },
                    ) { index ->
                        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
                        val pageNumber = index + 1

                        LaunchedEffect(key1 = pageNumber) {
                            bitmap = viewModel.getPageBitmap(pageNumber)
                        }

                        SlideListItem(
                            bitmap = bitmap,
                            onItemClick = {
                                // TODO: 페이지 클릭 시 이벤트
                            },
                            pageText = "$pageNumber / ${uiState.totalPages}",
                        )
                    }
                }
            }
        }

        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
        ) {
            ExpandableSearchFab(
                isExpanded = uiState.isSearchExpanded,
                query = uiState.searchQuery,
                resultCount = uiState.searchResults.size,
                currentIndex = uiState.currentResultIndex,
                onExpandChange = viewModel::toggleSearchExpanded,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = viewModel::onSearchTriggered,
                onNextClick = viewModel::moveToNextResult,
                onPrevClick = viewModel::moveToPrevResult,
                onClearClick = viewModel::clearSearch,
            )
        }
    }
}

@Composable
private fun ExpandableSearchFab(
    isExpanded: Boolean,
    query: String,
    resultCount: Int,
    currentIndex: Int,
    onExpandChange: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val isNavMode = resultCount > 0

    val fabWidth by animateDpAsState(
        targetValue = if (isExpanded) 340.dp else 56.dp,
        animationSpec =
            tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing,
            ),
        label = "FabWidth",
    )

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            focusRequester.requestFocus()
        } else {
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
                        .clickable(enabled = !isNavMode && !isExpanded) { onExpandChange() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
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
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxSize().padding(end = 8.dp),
                    ) {
                        Text(
                            text = "${currentIndex + 1} / $resultCount",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )

                        IconButton(onClick = onPrevClick) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Previous Result",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }

                        IconButton(onClick = onNextClick) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Next Result",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }

                        IconButton(onClick = onClearClick) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear Search",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = query,
                            onValueChange = onQueryChange,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                            placeholder = {
                                Text(
                                    "검색어 입력",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color =
                                        MaterialTheme.colorScheme
                                            .onPrimaryContainer
                                            .copy(alpha = 0.7f),
                                    maxLines = 1,
                                )
                            },
                            singleLine = true,
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
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions =
                                KeyboardActions(onSearch = {
                                    onSearch()
                                    focusManager.clearFocus()
                                }),
                        )

                        IconButton(
                            onClick = {
                                onExpandChange()
                                onQueryChange("")
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}
