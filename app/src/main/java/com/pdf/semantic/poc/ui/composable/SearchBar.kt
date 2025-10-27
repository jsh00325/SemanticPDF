package com.pdf.semantic.poc.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = modifier,
        value = query,
        onValueChange = { query = it },
        placeholder = { Text("어떤 내용이 궁금하신가요?") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Search Icon",
                modifier =
                    Modifier.clickable {
                        onSearch(query)
                    },
            )
        },
        singleLine = true,
    )
}
