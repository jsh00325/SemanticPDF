package com.pdf.semantic.domain.usecase

import android.net.Uri
import com.pdf.semantic.domain.repository.PdfRepository
import javax.inject.Inject

class ParsePdfUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(uri: Uri): List<String> {
        return pdfRepository.parsePdf(uri)
    }
}
