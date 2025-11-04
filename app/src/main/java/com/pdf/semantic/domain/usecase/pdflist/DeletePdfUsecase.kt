package com.pdf.semantic.domain.usecase.pdflist

import com.pdf.semantic.domain.repository.PdfFileRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import javax.inject.Inject

class DeletePdfUsecase
    @Inject
    constructor(
        private val pdfFileRepository: PdfFileRepository,
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        suspend operator fun invoke(pdfId: Long): Result<Unit> =
            try {
                pdfMetadataRepository.getPdfInternalPath(pdfId)?.let {
                    pdfFileRepository.deletePdfFile(it)
                }
                pdfMetadataRepository.deletePdfMetadata(pdfId)

                Result.success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
    }
