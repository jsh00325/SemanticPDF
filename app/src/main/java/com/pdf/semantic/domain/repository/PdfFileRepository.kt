package com.pdf.semantic.domain.repository

import android.graphics.Bitmap
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.model.PdfInfo

interface PdfFileRepository {
    suspend fun parsePdf(uriString: String): PdfDocument

    suspend fun getPdfDetail(uriString: String): PdfInfo

    suspend fun savePdfFile(uriString: String): String

    suspend fun deletePdfFile(internalPath: String)

    suspend fun renderPage(
        internalPath: String,
        pageNumber: Int,
    ): Bitmap

    suspend fun preloadAllPages(
        pdfId: Long,
        internalPath: String,
        totalPages: Int,
    )

    suspend fun getPageBitmap(
        pdfId: Long,
        internalPath: String,
        pageNumber: Int,
    ): Bitmap?
}
