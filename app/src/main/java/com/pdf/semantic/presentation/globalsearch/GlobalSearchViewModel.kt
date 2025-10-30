package com.pdf.semantic.presentation.globalsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalSearchViewModel
    @Inject
    constructor() : ViewModel() {
        private val _searchQuery = MutableStateFlow("")
        val searchQuery = _searchQuery.asStateFlow()

        // TODO: 추후 여러 UI 상태를 표현할 수 있도록 UiState 구현해서 교체
        private val _searchResults = MutableStateFlow<List<String>>(emptyList())
        val searchResults = _searchResults.asStateFlow()

        fun onSearchQueryChanged(query: String) {
            _searchQuery.value = query
        }

        fun searchQuery() {
            viewModelScope.launch {
                // TODO: 추후 로딩 화면 등을 구현하기

                if (_searchQuery.value.isBlank()) {
                    return@launch
                }

                // TODO: 추후 UseCase 구현 후 로직 수정
                _searchResults.value =
                    listOf(
                        "Result 1 for '${_searchQuery.value}'",
                        "Result 2 for '${_searchQuery.value}'",
                        "Result 3 for '${_searchQuery.value}'",
                    )
            }
        }
    }
