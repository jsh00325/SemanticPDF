package com.pdf.semantic.domain.usecase.pdfreader

import com.pdf.semantic.domain.model.PdfItem
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPdfDetailUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        suspend operator fun invoke(pdfId: Long): Flow<PdfItem> = pdfMetadataRepository.observePdfMetadata(pdfId)
    }
