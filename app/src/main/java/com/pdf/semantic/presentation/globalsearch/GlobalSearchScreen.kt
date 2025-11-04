package com.pdf.semantic.presentation.globalsearch

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdf.semantic.presentation.components.GlobalSearchItem
import com.pdf.semantic.presentation.components.SearchTopAppBar

@Composable
fun GlobalSearchScreen(
    onBackClick: () -> Unit,
    viewModel: GlobalSearchViewModel = hiltViewModel(),
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SearchTopAppBar(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                onBackClick = onBackClick,
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onSearch = viewModel::searchQuery,
                onSettingClick = {},
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(searchResults) { result ->
                GlobalSearchItem(
                    onItemClick = { /* TODO: 클릭 시 상세 페이지로 이동 */ },
                    globalSearchResult = result,
                )
            }
        }
    }
}
