package com.pdf.semantic.domain.usecase.pdfreader

import com.pdf.semantic.domain.model.PdfDetailResult
import com.pdf.semantic.domain.repository.PdfFileRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import javax.inject.Inject

class LoadSpecificPdfUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
        private val pdfFileRepository: PdfFileRepository,
    ) {
        suspend operator fun invoke(pdfId: Long): Result<PdfDetailResult> =
            try {
                val metadata = pdfMetadataRepository.getPdfMetadata(pdfId)
                val internalPath = pdfMetadataRepository.getPdfInternalPath(pdfId)

                if (internalPath == null) {
                    throw IllegalStateException("PDF의 내부 경로를 찾을 수 없습니다. ID: $pdfId")
                }

                pdfFileRepository.preloadAllPages(
                    pdfId = pdfId,
                    internalPath = internalPath,
                    totalPages = metadata?.totalPages ?: 0,
                )

                val result =
                    PdfDetailResult(
                        title = metadata?.title ?: "",
                        totalPages = metadata?.totalPages ?: 0,
                        internalPath = internalPath,
                    )

                Result.success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
    }
