package com.pdf.semantic.presentation.pdfreader

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdf.semantic.R
import com.pdf.semantic.presentation.components.SlideListItem
import kotlinx.coroutines.delay

@Composable
fun PdfReaderScreen(
    onBackClick: () -> Unit,
    viewModel: PdfReaderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val isExpansionOn by viewModel.isExpansionOn.collectAsState()

    LaunchedEffect(uiState.currentResultIndex, uiState.searchResults) {
        if (uiState.currentResultIndex >= 0 && uiState.searchResults.isNotEmpty()) {
            val targetSlide = uiState.searchResults[uiState.currentResultIndex]
            val targetIndex = (targetSlide.slideNumber - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
            viewModel.triggerHighlight(targetSlide.slideNumber)
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading && viewModel.initialPage > 0) {
            val targetIndex = (viewModel.initialPage - 1).coerceAtLeast(0)
            delay(100)
            listState.animateScrollToItem(targetIndex)
            viewModel.triggerHighlight(viewModel.initialPage)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
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
        },
        floatingActionButton = {},
    ) { paddingValues ->

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val imeBottomPadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()

                LazyColumn(
                    state = listState,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5)),
                    contentPadding =
                        PaddingValues(
                            start = 0.dp,
                            end = 0.dp,
                            top = 0.dp,
                            bottom = 0.dp + imeBottomPadding,
                        ),
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

                        val isHighlighted = (uiState.highlightedPage == pageNumber)

                        SlideListItem(
                            bitmap = bitmap,
                            pageText = "$pageNumber / ${uiState.totalPages}",
                            isHighlighted = isHighlighted,
                        )
                    }
                }
            }
            val imeHeightDp = with(density) { WindowInsets.ime.getBottom(density).toDp() }

            val navHeightWithPadding =
                with(density) {
                    WindowInsets.navigationBars.getBottom(density).toDp() + 0.dp
                }

            val finalBottomPadding = max(imeHeightDp, navHeightWithPadding)

            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(start = 16.dp)
                        .padding(end = 16.dp)
                        .padding(bottom = finalBottomPadding),
            ) {
                ExpandableSearchFab(
                    isExpanded = uiState.isSearchExpanded,
                    query = uiState.searchQuery,
                    resultCount = uiState.searchResults.size,
                    currentIndex = uiState.currentResultIndex,
                    isSearching = uiState.isSearching,
                    isExpansionOn = isExpansionOn,
                    onExpandChange = viewModel::toggleSearchExpanded,
                    onQueryChange = viewModel::updateSearchQuery,
                    onSearch = viewModel::onSearchTriggered,
                    onExpansionToggle = viewModel::toggleExpansion,
                    onNextClick = viewModel::moveToNextResult,
                    onPrevClick = viewModel::moveToPrevResult,
                    onClearClick = viewModel::clearSearch,
                )
            }
        }
    }
}
