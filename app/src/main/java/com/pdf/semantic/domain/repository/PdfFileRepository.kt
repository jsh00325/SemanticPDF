package com.pdf.semantic.domain.repository

import android.net.Uri
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.model.PdfInfo

interface PdfFileRepository {
    suspend fun parsePdf(uri: Uri): PdfDocument

    suspend fun getPdfDetail(uri: Uri): PdfInfo

    suspend fun savePdfFile(uri: Uri): String

    suspend fun deletePdfFile(internalPath: String)
}
