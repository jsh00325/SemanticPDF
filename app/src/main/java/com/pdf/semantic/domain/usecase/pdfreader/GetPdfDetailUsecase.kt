package com.pdf.semantic.domain.usecase.pdfreader

import com.pdf.semantic.domain.repository.PdfFileRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import javax.inject.Inject

class GetPdfDetailUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
        private val pdfFileRepository: PdfFileRepository,
    ) {
        suspend operator fun invoke(pdfId: Long): Result<Unit> =
            try {
                val metadata = pdfMetadataRepository.getPdfMetadata(pdfId)
                val internalPath = pdfMetadataRepository.getPdfInternalPath(pdfId)

                pdfFileRepository.preloadAllPages(
                    pdfId = pdfId,
                    internalPath = internalPath,
                    totalPages = metadata.totalPages,
                )

                Result.success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
    }
