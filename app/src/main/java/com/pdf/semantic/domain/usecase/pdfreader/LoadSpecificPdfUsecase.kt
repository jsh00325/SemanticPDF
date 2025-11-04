package com.pdf.semantic.domain.usecase.pdfreader

import com.pdf.semantic.domain.model.PdfDetailResult
import com.pdf.semantic.domain.repository.PdfFileRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

                CoroutineScope(Dispatchers.IO).launch {
                    pdfFileRepository.preloadAllPages(
                        pdfId = pdfId,
                        internalPath = internalPath,
                        totalPages = metadata.totalPages,
                    )
                }

                val result =
                    PdfDetailResult(
                        title = metadata.title,
                        totalPages = metadata.totalPages,
                        internalPath = internalPath,
                    )

                Result.success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
    }
