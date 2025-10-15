package com.pdf.semantic.domain.usecase

import android.net.Uri
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.repository.PdfRepository

class ParsePdfUseCase(
    private val repository: PdfRepository,
) {
    suspend operator fun invoke(uri: Uri): Result<PdfDocument> =
        try {
            val document = repository.parsePdf(uri)
            Result.success(document)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
}
