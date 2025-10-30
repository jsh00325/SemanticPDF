package com.pdf.semantic.domain.usecase.pdflist

import com.pdf.semantic.domain.model.PdfItem
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePdfListUsecase
    @Inject
    constructor(
        val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        operator fun invoke(): Flow<List<PdfItem>> = pdfMetadataRepository.observeAllPdfMetadata()
    }
