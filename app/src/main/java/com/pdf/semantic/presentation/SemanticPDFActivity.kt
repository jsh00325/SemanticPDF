package com.pdf.semantic.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.pdf.semantic.presentation.LongArrayStringConverter.toSerializedString
import com.pdf.semantic.presentation.globalsearch.GlobalSearchScreen
import com.pdf.semantic.presentation.pdflist.PdfListScreen
import com.pdf.semantic.presentation.pdflist.PdfListViewModel
import com.pdf.semantic.presentation.pdflist.folderlist.MoveItemsDialog
import com.pdf.semantic.presentation.pdfreader.PdfReaderScreen
import com.pdf.semantic.presentation.theme.SemanticPDFTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow

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
                startDestination = PdfList.defaultRoute,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(
                    route = PdfList.routeWithArgs,
                    arguments = PdfList.arguments,
                ) { navBackStackEntry ->
                    val viewModel: PdfListViewModel = hiltViewModel()
                    val moveSuccessFlow: StateFlow<Boolean> =
                        navBackStackEntry.savedStateHandle
                            .getStateFlow(PdfList.MOVE_SUCCESS_KEY, false)

                    LaunchedEffect(Unit) {
                        moveSuccessFlow.collect { moveSuccess ->
                            if (moveSuccess) {
                                viewModel.disableMultiSelectMode()
                                navBackStackEntry.savedStateHandle[PdfList.MOVE_SUCCESS_KEY] = false
                            }
                        }
                    }

                    PdfListScreen(
                        onGlobalSearchClick = {
                            navController.navigateToGlobalSearch()
                        },
                        onNavigateToFolder = { folderId ->
                            navController.navigateToFolder(folderId)
                        },
                        onNavigateToPdfReader = { pdfId ->
                            navController.navigateToPdfReader(pdfId)
                        },
                        onNavigateToMoveDialog = { folderIds, pdfIds ->
                            navController.navigateToMoveItemsDialog(
                                folderIds = folderIds,
                                pdfIds = pdfIds,
                                currentFolderId = viewModel.currentFolderId,
                            )
                        },
                        viewModel = viewModel,
                    )
                }

                dialog(
                    route = MoveItemsDialog.routeWithArgs,
                    arguments = MoveItemsDialog.arguments,
                ) {
                    val folderScreenEntry = navController.previousBackStackEntry

                    MoveItemsDialog(
                        onDismiss = { navController.popBackStack() },
                        onMoveConfirm = {
                            folderScreenEntry?.savedStateHandle?.set(PdfList.MOVE_SUCCESS_KEY, true)
                            navController.popBackStack()
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
                        onSearchResultClick = { pdfId, pageNumber ->
                            navController.navigateToPdfReader(pdfId, pageNumber)
                        },
                    )
                }
            }
        }
    }
}

private fun NavHostController.navigateToGlobalSearch() = navigate(GlobalSearch.route)

private fun NavHostController.navigateToFolder(folderId: Long?) =
    navigate("${PdfList.route}/${folderId ?: -1L}")

private fun NavHostController.navigateToMoveItemsDialog(
    folderIds: List<Long>,
    pdfIds: List<Long>,
    currentFolderId: Long?,
) = navigate(
    "${MoveItemsDialog.route}/" +
        "${folderIds.toSerializedString()}/" +
        "${pdfIds.toSerializedString()}/" +
        "${currentFolderId ?: -1L}",
)

private fun NavHostController.navigateToPdfReader(
    pdfId: Long,
    pageNumber: Int = 1,
) {
    val route = "${PdfReader.route}/$pdfId?${PdfReader.PAGE_ARG}=$pageNumber"
    this.navigate(route)
}
