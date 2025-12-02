package com.pdf.semantic.domain.usecase.pdflist

import com.pdf.semantic.domain.model.FolderTreeNode
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFolderTreeUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        operator fun invoke(): Flow<FolderTreeNode> = pdfMetadataRepository.observeFolderTrees()
    }
