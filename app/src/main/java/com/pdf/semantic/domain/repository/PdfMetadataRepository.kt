package com.pdf.semantic.domain.repository

import com.pdf.semantic.domain.model.FolderItem
import com.pdf.semantic.domain.model.FolderTreeNode
import com.pdf.semantic.domain.model.PdfItem
import kotlinx.coroutines.flow.Flow

interface PdfMetadataRepository {
    suspend fun getAllEmbeddedPdfIds(): List<Long>

    suspend fun getPdfInternalPath(pdfId: Long): String?

    suspend fun getPdfMetadata(pdfId: Long): PdfItem?

    suspend fun insertFolder(name: String, parentId: Long? = null)

    suspend fun insertPdfMetadata(
        fileName: String,
        internalPath: String,
        totalPages: Int,
        thumbnailPath: String,
    ): Long

    suspend fun deletePdfMetadata(pdfId: Long)

    fun observeAllPdfMetadata(parentId: Long? = null): Flow<List<PdfItem>>

    fun observePdfMetadata(pdfId: Long): Flow<PdfItem>

    fun observeFolders(parentId: Long? = null): Flow<List<FolderItem>>

    fun observePdfs(parentId: Long? = null): Flow<List<PdfItem>>

    fun observeFolderTrees(): Flow<FolderTreeNode>

    fun observeFolderName(currentFolderId: Long?): Flow<String>

    fun observeFolderPath(currentFolderId: Long?): Flow<List<FolderItem>>
}
