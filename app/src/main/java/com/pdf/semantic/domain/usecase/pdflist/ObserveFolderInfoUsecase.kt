package com.pdf.semantic.domain.usecase.pdflist

import com.pdf.semantic.domain.model.FolderInfo
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveFolderInfoUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        operator fun invoke(currentFolderId: Long? = null): Flow<FolderInfo> {
            val nameFlow = pdfMetadataRepository.observeFolderName(currentFolderId)
            val pathFlow = pdfMetadataRepository.observeFolderPath(currentFolderId)
            return combine(nameFlow, pathFlow) { name, path ->
                FolderInfo(name, path)
            }
        }
    }
