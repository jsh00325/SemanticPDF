package com.pdf.semantic.domain.usecase.pdflist

import com.pdf.semantic.domain.repository.PdfFileRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import javax.inject.Inject

class AddPdfUsecase
    @Inject
    constructor(
        private val pdfFileRepository: PdfFileRepository,
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        suspend operator fun invoke(uriString: String): Result<Long> =
            try {
                val info = pdfFileRepository.getPdfDetail(uriString)

                val internalPath = pdfFileRepository.savePdfFile(uriString)

                val newPdfId =
                    pdfMetadataRepository.insertPdfMetadata(
                        fileName = info.title,
                        internalPath = internalPath,
                        totalPages = info.totalPages,
                        thumbnailPath = info.thumbnailFilePath,
                    )

                Result.success(newPdfId)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
    }
