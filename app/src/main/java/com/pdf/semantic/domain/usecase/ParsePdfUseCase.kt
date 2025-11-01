package com.pdf.semantic.domain.usecase

import android.net.Uri
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.repository.PdfFileRepository
import javax.inject.Inject

class ParsePdfUseCase
    @Inject
    constructor(
        private val repository: PdfFileRepository,
    ) {
        suspend operator fun invoke(uri: Uri): Result<PdfDocument> =
            try {
                val document = repository.parsePdf(uri.toString())
                Result.success(document)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
    }
