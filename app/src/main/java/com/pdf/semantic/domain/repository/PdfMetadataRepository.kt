package com.pdf.semantic.domain.repository

import com.pdf.semantic.domain.model.EmbeddingStatus
import com.pdf.semantic.domain.model.PdfItem
import kotlinx.coroutines.flow.Flow

interface PdfMetadataRepository {
    suspend fun getAllEmbeddedPdfIds(): List<Long>

    suspend fun getPdfInternalPath(pdfId: Long): String

    suspend fun insertPdfMetadata(
        fileName: String,
        internalPath: String,
        totalPages: Int,
    ): Long

    suspend fun deletePdfMetadata(pdfId: Long)

    suspend fun updateEmbeddingStatus(
        pdfId: Long,
        status: EmbeddingStatus,
    )

    suspend fun observeAllPdfMetadata(): Flow<List<PdfItem>>

    suspend fun observePdfMetadata(pdfId: Long): Flow<PdfItem>
}
