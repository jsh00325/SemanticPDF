package com.pdf.semantic.domain.usecase.pdfreader

import android.graphics.Bitmap
import com.pdf.semantic.domain.repository.PdfFileRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import javax.inject.Inject

class GetPdfDetailUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
        private val pdfFileRepository: PdfFileRepository,
    ) {
        suspend operator fun invoke(
            pdfId: Long,
            pageNumber: Int,
        ): Bitmap? {
            val internalPath = pdfMetadataRepository.getPdfInternalPath(pdfId)

            return pdfFileRepository.getPageBitmap(
                pdfId = pdfId,
                internalPath = internalPath,
                pageNumber = pageNumber,
            )
        }
    }
