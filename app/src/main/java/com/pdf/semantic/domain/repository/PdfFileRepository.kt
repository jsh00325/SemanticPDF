package com.pdf.semantic.domain.repository

import com.pdf.semantic.domain.model.PdfInfo

interface PdfFileRepository {
    suspend fun getPdfDetail(uriString: String): PdfInfo

    suspend fun savePdfFile(uriString: String): String

    suspend fun deletePdfFile(internalPath: String)
}
