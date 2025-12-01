package com.pdf.semantic.domain.usecase.pdflist

import com.pdf.semantic.domain.repository.PdfMetadataRepository
import javax.inject.Inject

class AddFolderUsecase @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        suspend operator fun invoke(name: String, parentId: Long? = null) =
            pdfMetadataRepository.insertFolder(name, parentId)
    }
