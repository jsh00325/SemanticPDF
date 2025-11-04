package com.pdf.semantic.presentation.pdfreader

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdf.semantic.presentation.components.SlideListItem

@Composable
fun PdfReaderScreen(
    onBackClick: () -> Unit,
    viewModel: PdfReaderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                ) {
                    items(
                        count = uiState.totalPages,
                        key = { index -> index },
                    ) { pageIndex ->

                        SlideLoader(
                            viewModel = viewModel,
                            pageNumber = pageIndex,
                            onItemClick = {
                                // TODO: 페이지 클릭 시 이벤트
                            },
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { /* TODO */ },
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
        ) {
            Icon(
                Icons.Filled.Search,
                contentDescription = "검색",
            )
        }
    }
}

@Composable
private fun SlideLoader(
    viewModel: PdfReaderViewModel,
    pageNumber: Int,
    onItemClick: () -> Unit,
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(key1 = pageNumber) {
        bitmap = viewModel.getPageBitmap(pageNumber)
    }

    SlideListItem(
        bitmap = bitmap,
        onItemClick = onItemClick,
    )
}
