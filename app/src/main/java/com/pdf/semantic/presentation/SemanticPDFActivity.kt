package com.pdf.semantic.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pdf.semantic.data.datasource.LlmDataSource
import com.pdf.semantic.presentation.globalsearch.GlobalSearchScreen
import com.pdf.semantic.presentation.pdflist.PdfListScreen
import com.pdf.semantic.presentation.pdfreader.PdfReaderScreen
import com.pdf.semantic.presentation.theme.SemanticPDFTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SemanticPDFActivity : ComponentActivity() {
    @Inject
    lateinit var llmDataSource: LlmDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SemanticPDFApp()
        }

        lifecycleScope.launch {
            val query = "삼성 갤럭시 스마트폰의 소프트웨어 기반"
            Log.d("EXPAND_TEST", "Query: $query")
            val expandedQuery = llmDataSource.expandQueryForRetrieval(query)
            Log.d("EXPAND_TEST", "Expanded Query: $expandedQuery")
        }
    }
}

@Composable
fun SemanticPDFApp() {
    SemanticPDFTheme {
        val navController = rememberNavController()

        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = PdfList.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(route = PdfList.route) {
                    PdfListScreen(
                        onGlobalSearchClick = {
                            navController.navigateSingleTopTo(GlobalSearch.route)
                        },
                        onPdfClick = { pdfId ->
                            navController.navigateToPdfReader(pdfId)
                        },
                    )
                }

                composable(
                    route = PdfReader.routeWithArgs,
                    arguments = PdfReader.arguments,
                ) {
                    val pdfId = it.arguments?.getLong(PdfReader.PDF_ID_ARG)
                    PdfReaderScreen(
                        pdfId = pdfId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                    )
                }

                composable(route = GlobalSearch.route) {
                    GlobalSearchScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                    )
                }
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id,
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

private fun NavHostController.navigateToPdfReader(pdfId: Long) {
    this.navigateSingleTopTo("${PdfReader.route}/$pdfId")
}
