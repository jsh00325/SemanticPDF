package com.pdf.semantic.domain.usecase.pdfreader

import com.pdf.semantic.domain.model.PdfItem
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSinglePdfProgressUsecase
    @Inject
    constructor(
        val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        operator fun invoke(pdfId: Long): Flow<PdfItem> =
            pdfMetadataRepository.observePdfMetadata(pdfId)
    }
