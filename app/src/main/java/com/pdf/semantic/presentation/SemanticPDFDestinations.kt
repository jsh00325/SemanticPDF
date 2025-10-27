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
    const val PDF_ID_ARG = "pdf_id"
    val routeWithArgs = "$route/{$PDF_ID_ARG}"
    val arguments =
        listOf(
            navArgument(PDF_ID_ARG) { type = NavType.LongType },
        )
}

object GlobalSearch : SemanticPDFDestination {
    override val route = "global_search"
}
