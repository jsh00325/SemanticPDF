package com.pdf.semantic.data.repositoryImpl

import com.pdf.semantic.domain.model.EmbeddingStatus
import com.pdf.semantic.domain.model.PdfItem
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfMetadataRepositoryImpl @Inject constructor() : PdfMetadataRepository {
    override suspend fun getAllEmbeddedPdfIds(): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getPdfInternalPath(pdfId: Long): String {
        TODO("Not yet implemented")
    }

    override suspend fun insertPdfMetadata(
        fileName: String,
        internalPath: String,
        totalPages: Int,
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deletePdfMetadata(pdfId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updateEmbeddingStatus(
        pdfId: Long,
        status: EmbeddingStatus,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun observeAllPdfMetadata(): Flow<List<PdfItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun observePdfMetadata(pdfId: Long): Flow<PdfItem> {
        TODO("Not yet implemented")
    }
}
