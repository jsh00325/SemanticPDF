package com.pdf.semantic.domain.repository

import android.net.Uri
import com.pdf.semantic.domain.model.PdfDocument

interface PdfFileRepository {
    suspend fun parsePdf(uri: Uri): PdfDocument
}
