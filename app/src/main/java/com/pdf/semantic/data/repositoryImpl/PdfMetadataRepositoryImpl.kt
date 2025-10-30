package com.pdf.semantic.data.repositoryImpl

import com.pdf.semantic.data.datasource.ObjectBoxDbDataSource
import com.pdf.semantic.data.entity.PdfDocumentEntity
import com.pdf.semantic.data.mapper.PdfDocumentMapper.toModel
import com.pdf.semantic.data.mapper.PdfDocumentMapper.toModels
import com.pdf.semantic.domain.model.EmbeddingStatus
import com.pdf.semantic.domain.model.PdfItem
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfMetadataRepositoryImpl
    @Inject
    constructor(
        private val objectBoxDbDataSource: ObjectBoxDbDataSource,
    ) : PdfMetadataRepository {
        override suspend fun getAllEmbeddedPdfIds(): List<Long> =
            objectBoxDbDataSource
                .getAllPdfDocuments()
                .filter { it.embeddingStatus == EmbeddingStatus.COMPLETE }
                .map { it.id }

        override suspend fun getPdfInternalPath(pdfId: Long): String =
            objectBoxDbDataSource.getPdfDocumentById(pdfId).internalFilePath

        override suspend fun insertPdfMetadata(
            fileName: String,
            internalPath: String,
            totalPages: Int,
        ): Long =
            objectBoxDbDataSource.putPdfDocument(
                PdfDocumentEntity(
                    title = fileName,
                    internalFilePath = internalPath,
                    totalPages = totalPages,
                    createdAt = Date(),
                ),
            )

        override suspend fun deletePdfMetadata(pdfId: Long) {
            objectBoxDbDataSource.deletePdfDocument(pdfId)
        }

        override suspend fun updateEmbeddingStatus(
            pdfId: Long,
            status: EmbeddingStatus,
        ) {
            val pdfDocument = objectBoxDbDataSource.getPdfDocumentById(pdfId)
            pdfDocument.embeddingStatus = status
            objectBoxDbDataSource.putPdfDocument(pdfDocument)
        }

        override suspend fun observeAllPdfMetadata(): Flow<List<PdfItem>> =
            objectBoxDbDataSource.observeAllPdfDocuments().map { entityList ->
                entityList.toModels()
            }

        override suspend fun observePdfMetadata(pdfId: Long): Flow<PdfItem> =
            objectBoxDbDataSource.observePdfDocumentById(pdfId).map { entity ->
                entity.toModel()
            }
    }
