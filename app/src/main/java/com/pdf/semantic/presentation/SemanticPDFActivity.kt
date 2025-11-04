package com.pdf.semantic.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pdf.semantic.presentation.globalsearch.GlobalSearchScreen
import com.pdf.semantic.presentation.pdflist.PdfListScreen
import com.pdf.semantic.presentation.pdfreader.PdfReaderScreen
import com.pdf.semantic.presentation.theme.SemanticPDFTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SemanticPDFActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SemanticPDFApp()
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
                    PdfReaderScreen(
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
