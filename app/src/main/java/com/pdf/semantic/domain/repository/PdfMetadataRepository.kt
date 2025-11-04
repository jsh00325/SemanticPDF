package com.pdf.semantic.domain.repository

import com.pdf.semantic.domain.model.PdfItem
import kotlinx.coroutines.flow.Flow

interface PdfMetadataRepository {
    suspend fun getAllEmbeddedPdfIds(): List<Long>

    suspend fun getPdfInternalPath(pdfId: Long): String?

    suspend fun getPdfMetadata(pdfId: Long): PdfItem?

    suspend fun insertPdfMetadata(
        fileName: String,
        internalPath: String,
        totalPages: Int,
        thumbnailPath: String,
    ): Long

    suspend fun deletePdfMetadata(pdfId: Long)

    fun observeAllPdfMetadata(): Flow<List<PdfItem>>

    fun observePdfMetadata(pdfId: Long): Flow<PdfItem>
}
