package com.pdf.semantic.domain.usecase.pdflist

import com.pdf.semantic.domain.model.FoldersAndDocuments
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveFoldersAndPdfsUsecase
    @Inject
    constructor(
        private val pdfMetadataRepository: PdfMetadataRepository,
    ) {
        operator fun invoke(parentId: Long? = null): Flow<FoldersAndDocuments> {
            val foldersFlow = pdfMetadataRepository.observeFolders(parentId)
            val documentsFlow = pdfMetadataRepository.observePdfs(parentId)
            return combine(foldersFlow, documentsFlow) { folders, documents ->
                FoldersAndDocuments(folders, documents)
            }
        }
    }
