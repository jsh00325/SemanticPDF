package com.pdf.semantic.presentation.globalsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun GlobalSearchScreen(
    onBackClick: () -> Unit,
    onSearchResultClick: (Long, Int) -> Unit,
    viewModel: GlobalSearchViewModel = hiltViewModel(),
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val isExpansionOn by viewModel.isExpansionOn.collectAsState()
    val hasShownGuide by viewModel.hasShownGuide.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GlobalSearchTopSearchBar(
                modifier =
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                onBackClick = onBackClick,
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onSearch = viewModel::searchQuery,
                isExpansionOn = isExpansionOn,
                onExpansionToggleClick = viewModel::onExpansionToggled,
                hasShownGuide = hasShownGuide,
                onGuideShown = viewModel::onGuideShown,
            )
        },
    ) { innerPadding ->
        when (uiState) {
            GlobalSearchUiState.Idle -> {}

            GlobalSearchUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is GlobalSearchUiState.SearchingSuccess -> {
                val searchResults = (uiState as GlobalSearchUiState.SearchingSuccess).results

                if (searchResults.isEmpty()) {
                    // TODO: 추후 디자인 필요
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("검색 결과가 없습니다.")
                    }
                } else {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                    ) {
                        items(searchResults) { result ->
                            GlobalSearchResultItem(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onItemClick = {
                                    onSearchResultClick(result.pdfId, result.slideNumber)
                                },
                                uiItem = result,
                            )
                        }
                    }
                }
            }

            is GlobalSearchUiState.SearchingError -> {
                val errorMessage = (uiState as GlobalSearchUiState.SearchingError).message

                // TODO: 추후 디자인 필요
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = errorMessage)
                }
            }
        }
    }
}
