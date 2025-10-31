package com.pdf.semantic.domain.usecase.pdflist

import android.net.Uri
import com.pdf.semantic.domain.repository.PdfFileRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import javax.inject.Inject

class AddPdfUsecase
    @Inject
    constructor(
        private val pdfFileRepository: PdfFileRepository,
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        suspend operator fun invoke(uri: Uri): Result<Long> =
            try {
                val document = pdfFileRepository.parsePdf(uri)

                val internalPath = pdfFileRepository.savePdfFile(uri)

                val newPdfId =
                    pdfMetadataRepository.insertPdfMetadata(
                        fileName = document.title,
                        internalPath = internalPath,
                        totalPages = document.slides.size,
                    )

                Result.success(newPdfId)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
    }
