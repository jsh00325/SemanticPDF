package com.pdf.semantic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pdf.semantic.presentation.PdfSelectorScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint //Hilt DI 시작점
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PdfSelectorScreen()
        }
    }
}
