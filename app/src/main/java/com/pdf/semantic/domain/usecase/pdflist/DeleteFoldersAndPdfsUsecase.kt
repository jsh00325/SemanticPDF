package com.pdf.semantic.domain.usecase.pdflist

import com.pdf.semantic.domain.repository.PdfMetadataRepository
import javax.inject.Inject

class DeleteFoldersAndPdfsUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        suspend operator fun invoke(
            folderIds: List<Long>,
            pdfIds: List<Long>,
        ) {
            pdfMetadataRepository.deleteFolders(folderIds)
            pdfMetadataRepository.deletePdfs(pdfIds)
        }
    }
