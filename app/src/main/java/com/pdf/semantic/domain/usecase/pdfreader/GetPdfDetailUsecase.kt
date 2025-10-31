package com.pdf.semantic.domain.usecase.pdfreader

import com.pdf.semantic.domain.repository.PdfMetadataRepository
import javax.inject.Inject

class GetPdfDetailUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        suspend operator fun invoke(pdfId: Long): Result<String> =
            try {
                val internalPath = pdfMetadataRepository.getPdfInternalPath(pdfId)
                Result.success(internalPath)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
    }
