package com.pdf.semantic.presentation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface SemanticPDFDestination {
    val route: String
}

object PdfList : SemanticPDFDestination {
    override val route = "pdf_list"

    const val PARENT_ID_ARG = "parent_id"
    const val MOVE_SUCCESS_KEY = "move_success"

    val routeWithArgs = "$route/{$PARENT_ID_ARG}"
    val defaultRoute = "$route/${-1L}"

    val arguments =
        listOf(
            navArgument(PARENT_ID_ARG) {
                type = NavType.LongType
                defaultValue = -1L
            },
        )
}

object MoveItemsDialog : SemanticPDFDestination {
    override val route = "move_items_dialog"

    const val FOLDER_IDS_JSON_ARG = "folder_ids_json"
    const val PDF_IDS_JSON_ARG = "pdf_ids_json"
    const val CURRENT_FOLDER_ID_ARG = "current_folder_id"

    val routeWithArgs = "$route/{$FOLDER_IDS_JSON_ARG}/{$PDF_IDS_JSON_ARG}/{$CURRENT_FOLDER_ID_ARG}"

    val arguments =
        listOf(
            navArgument(FOLDER_IDS_JSON_ARG) {
                type = NavType.StringType
            },
            navArgument(PDF_IDS_JSON_ARG) {
                type = NavType.StringType
            },
            navArgument(CURRENT_FOLDER_ID_ARG) {
                type = NavType.LongType
                defaultValue = -1L
            },
        )
}

object PdfReader : SemanticPDFDestination {
    override val route = "pdf_reader"

    const val PDF_ID_ARG = "pdfId"
    const val PAGE_ARG = "page"

    val routeWithArgs = "$route/{$PDF_ID_ARG}?$PAGE_ARG={$PAGE_ARG}"
    val arguments =
        listOf(
            navArgument(PDF_ID_ARG) { type = NavType.LongType },
            navArgument(PAGE_ARG) {
                type = NavType.IntType
                defaultValue = -1
            },
        )
}

object GlobalSearch : SemanticPDFDestination {
    override val route = "global_search"
}
