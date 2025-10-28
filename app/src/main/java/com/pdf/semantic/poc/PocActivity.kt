package com.pdf.semantic.poc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pdf.semantic.poc.ui.theme.SemanticPDFTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PocActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SemanticPDFTheme {
                PocScreen()
            }
        }
    }
}
