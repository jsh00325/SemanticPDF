package com.pdf.semantic.presentation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface SemanticPDFDestination {
    val route: String
}

object PdfList : SemanticPDFDestination {
    override val route = "pdf_list"
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
                defaultValue = 1
            },
        )
}

object GlobalSearch : SemanticPDFDestination {
    override val route = "global_search"
}
