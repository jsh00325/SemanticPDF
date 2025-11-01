package com.pdf.semantic.domain.repository

import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.model.PdfInfo

interface PdfFileRepository {
    suspend fun parsePdf(uriString: String): PdfDocument

    suspend fun getPdfDetail(uriString: String): PdfInfo

    suspend fun savePdfFile(uriString: String): String

    suspend fun deletePdfFile(internalPath: String)
}
