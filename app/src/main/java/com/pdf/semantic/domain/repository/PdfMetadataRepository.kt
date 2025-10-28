package com.pdf.semantic.domain.repository

interface PdfMetadataRepository {
    suspend fun getAllEmbeddedPdfIds(): List<Long>
}
