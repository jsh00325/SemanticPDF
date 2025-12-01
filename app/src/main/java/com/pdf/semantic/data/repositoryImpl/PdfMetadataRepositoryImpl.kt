package com.pdf.semantic.data.repositoryImpl

import com.pdf.semantic.data.datasource.ObjectBoxDbDataSource
import com.pdf.semantic.data.entity.FolderEntity
import com.pdf.semantic.data.entity.PdfDocumentEntity
import com.pdf.semantic.data.mapper.FolderMapper.toFolderTreeNode
import com.pdf.semantic.data.mapper.FolderMapper.toModels
import com.pdf.semantic.data.mapper.PdfDocumentMapper.toModel
import com.pdf.semantic.data.mapper.PdfDocumentMapper.toModels
import com.pdf.semantic.domain.model.EmbeddingStatus
import com.pdf.semantic.domain.model.FolderItem
import com.pdf.semantic.domain.model.FolderTreeNode
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

        override suspend fun getPdfInternalPath(pdfId: Long): String? =
            objectBoxDbDataSource.getPdfDocumentById(pdfId)?.internalFilePath

        override suspend fun getPdfMetadata(pdfId: Long): PdfItem? =
            objectBoxDbDataSource.getPdfDocumentById(pdfId)?.toModel()

        override suspend fun insertFolder(name: String, parentId: Long?) =
            objectBoxDbDataSource.insertFolder(
                FolderEntity(
                    parentId = parentId,
                    name = name,
                )
            )

        override suspend fun insertPdfMetadata(
                fileName: String,
                internalPath: String,
                totalPages: Int,
                thumbnailPath: String,
            ): Long {
                // 1. 삽입
                objectBoxDbDataSource.insertPdfDocument(
                    PdfDocumentEntity(
                        title = fileName,
                        internalFilePath = internalPath,
                        totalPages = totalPages,
                        createdAt = Date(),
                        thumbnail = thumbnailPath,
                    ),
                )

                // 2. 내부 파일 경로로 ID 조회
                return objectBoxDbDataSource.getPdfDocumentIdByInternalPath(internalPath)
                    ?: throw IllegalStateException("PdfDocumentEntity not found")
            }

        override suspend fun deletePdfMetadata(pdfId: Long) {
            objectBoxDbDataSource.deletePdfDocument(pdfId)
        }

        override suspend fun deleteFolders(folderIds: List<Long>) =
            objectBoxDbDataSource.deleteFolders(folderIds)

        override suspend fun deletePdfs(pdfIds: List<Long>) =
            objectBoxDbDataSource.deletePdfDocuments(pdfIds)

        override suspend fun moveFoldersAndPdfs(
            folderIds: List<Long>,
            pdfIds: List<Long>,
            newParentId: Long?,
        ) = objectBoxDbDataSource.moveFoldersAndPdfs(folderIds, pdfIds, newParentId)

        override fun observeAllPdfMetadata(parentId: Long?): Flow<List<PdfItem>> =
            objectBoxDbDataSource.observeAllPdfDocuments().map { entityList ->
                entityList.toModels()
            }

        override fun observePdfMetadata(pdfId: Long): Flow<PdfItem> =
            objectBoxDbDataSource.observePdfDocumentById(pdfId).map { entity ->
                entity.toModel()
            }

        override fun observeFolders(parentId: Long?): Flow<List<FolderItem>> =
            objectBoxDbDataSource.observeFoldersByParentId(parentId).map { it.toModels() }

        override fun observePdfs(parentId: Long?): Flow<List<PdfItem>> =
            objectBoxDbDataSource.observePdfDocumentsByParentId(parentId).map { it.toModels() }

        override fun observeFolderTrees(): Flow<FolderTreeNode> =
            objectBoxDbDataSource.observeAllFolders().map { it.toFolderTreeNode() }

        override fun observeFolderName(currentFolderId: Long?): Flow<String> =
            objectBoxDbDataSource.observeFolderNameById(currentFolderId)

        override fun observeFolderPath(currentFolderId: Long?): Flow<List<FolderItem>> =
            objectBoxDbDataSource.observeFolderPathById(currentFolderId).map { it.toModels() }
}
