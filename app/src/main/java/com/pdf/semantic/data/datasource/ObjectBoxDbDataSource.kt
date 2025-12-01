package com.pdf.semantic.data.datasource

import com.pdf.semantic.data.dto.PageEmbeddingSearchResult
import com.pdf.semantic.data.entity.FolderEntity
import com.pdf.semantic.data.entity.FolderEntity_
import com.pdf.semantic.data.entity.PageEmbeddingEntity
import com.pdf.semantic.data.entity.PageEmbeddingEntity_
import com.pdf.semantic.data.entity.PdfDocumentEntity
import com.pdf.semantic.data.entity.PdfDocumentEntity_
import com.pdf.semantic.domain.model.EmbeddingStatus
import io.objectbox.BoxStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList

@Singleton
class ObjectBoxDbDataSource
    @Inject
    constructor(
        private val boxStore: BoxStore,
    ) {
        private val folderBox = boxStore.boxFor(FolderEntity::class.java)
        private val pdfDocumentBox = boxStore.boxFor(PdfDocumentEntity::class.java)
        private val pageEmbeddingBox = boxStore.boxFor(PageEmbeddingEntity::class.java)

        private suspend fun runInIoTx(block: () -> Unit) =
            withContext(Dispatchers.IO) {
                boxStore.runInTx { block() }
            }

        // Create
        suspend fun insertPdfDocument(pdfDocument: PdfDocumentEntity) =
            runInIoTx {
                val parentFolder = pdfDocument.parentId?.let { folderBox.get(it) }
                val parentPath = parentFolder?.let { "${it.parentAbsolutePath}${it.id}/" } ?: "/"
                pdfDocument.parentAbsolutePath = parentPath
                pdfDocumentBox.put(pdfDocument)
            }

        suspend fun insertFolder(folder: FolderEntity) =
            runInIoTx {
                val parentAbsolutePath =
                    folder.parentId?.let { parentId ->
                        folderBox.get(parentId)?.let { parentFolder ->
                            parentFolder.parentAbsolutePath + parentId + "/"
                        } ?: "/"
                    } ?: "/"

                folder.parentAbsolutePath = parentAbsolutePath
                folderBox.put(folder)
            }

        suspend fun insertEmbeddingChunkAndUpdateStatus(
            pdfId: Long,
            pageEmbeddings: List<PageEmbeddingEntity>,
            lastPageInChunk: Int,
        ) = runInIoTx {
            pdfDocumentBox.get(pdfId)?.also { pdfDocument ->
                pageEmbeddings.forEach { it.pdfDocument.target = pdfDocument }
                pdfDocument.pageEmbeddings.addAll(pageEmbeddings)

                pdfDocument.processedPages = lastPageInChunk

                pdfDocumentBox.put(pdfDocument)
            }
        }

        // Read
        suspend fun getPdfDocumentById(pdfId: Long): PdfDocumentEntity? =
            withContext(Dispatchers.IO) {
                pdfDocumentBox.get(pdfId)
            }

        fun observeFoldersByParentId(parentId: Long?): Flow<List<FolderEntity>> =
            folderBox
                .query(
                    if (parentId == null) {
                        FolderEntity_.parentId.isNull()
                    } else {
                        FolderEntity_.parentId.equal(parentId)
                    },
                ).build()
                .asFlow(folderBox)

        fun observePdfDocumentsByParentId(parentId: Long?): Flow<List<PdfDocumentEntity>> =
            pdfDocumentBox
                .query(
                    if (parentId == null) {
                        PdfDocumentEntity_.parentId.isNull()
                    } else {
                        PdfDocumentEntity_.parentId.equal(parentId)
                    },
                ).build()
                .asFlow(pdfDocumentBox)

        fun observeAllFolders(): Flow<List<FolderEntity>> =
            folderBox.query().build().asFlow(folderBox)

        fun observeFolderNameById(folderId: Long?): Flow<String> =
            if (folderId == null) {
                flowOf("폴더")
            } else {
                folderBox.observeById(folderId).map { it?.name ?: "폴더" }
            }

        fun observeFolderPathById(folderId: Long?): Flow<List<FolderEntity>> =
            if (folderId == null) {
                flowOf(emptyList())
            } else {
                folderBox.observeById(folderId).map { uncheckedCurrentEntity ->
                    uncheckedCurrentEntity?.let { currentEntity ->
                        currentEntity
                            .parentAbsolutePath
                            .split("/")
                            .filter { it.isNotEmpty() }
                            .mapNotNull { idString ->
                                idString.toLongOrNull()?.let {
                                    folderBox.get(it)
                                }
                            } + currentEntity
                    } ?: emptyList()
                }
            }

        suspend fun getAllPdfDocuments(): List<PdfDocumentEntity> =
            withContext(Dispatchers.IO) {
                pdfDocumentBox.all
            }

        suspend fun getPdfDocumentIdByInternalPath(internalPath: String): Long? =
            withContext(Dispatchers.IO) {
                pdfDocumentBox
                    .query(
                        PdfDocumentEntity_.internalFilePath.equal(internalPath),
                    ).build()
                    .use { query ->
                        query.findFirst()?.id
                    }
            }

        suspend fun searchSimilarityPageEmbedding(
            queryVector: FloatArray,
            topK: Int = 100,
        ): List<PageEmbeddingSearchResult> =
            withContext(Dispatchers.IO) {
                pageEmbeddingBox
                    .query(
                        PageEmbeddingEntity_.embeddingVector.nearestNeighbors(
                            queryVector,
                            topK,
                        ),
                    ).build()
                    .use { query ->
                        val results = query.findWithScores()
                        results.map {
                            PageEmbeddingSearchResult(
                                entity = it.get(),
                                score = it.score,
                            )
                        }
                    }
            }

        fun observePdfDocumentById(pdfId: Long): Flow<PdfDocumentEntity> =
            pdfDocumentBox.observeById(pdfId).filterNotNull()

        fun observeAllPdfDocuments(): Flow<List<PdfDocumentEntity>> =
            pdfDocumentBox.query().build().asFlow(pdfDocumentBox)

        // Update
        suspend fun updatePdfStatus(
            pdfId: Long,
            newProcessedPages: Int,
            newStatus: EmbeddingStatus,
        ) = runInIoTx {
            pdfDocumentBox.get(pdfId)?.also {
                it.processedPages = newProcessedPages
                it.embeddingStatus = newStatus
                pdfDocumentBox.put(it)
            }
        }

        suspend fun rollbackAndSetFailStatus(pdfId: Long) =
            runInIoTx {
                pdfDocumentBox.get(pdfId)?.also { pdfDocument ->
                    pageEmbeddingBox.remove(pdfDocument.pageEmbeddings)

                    pdfDocument.processedPages = 0
                    pdfDocument.embeddingStatus = EmbeddingStatus.FAIL
                    pdfDocument.pageEmbeddings.clear()

                    pdfDocumentBox.put(pdfDocument)
                }
            }

        private fun movePdfDocument(
            id: Long,
            newParentId: Long?,
            newParentFullPath: String,
        ) {
            pdfDocumentBox.get(id)?.also { pdfDocument ->
                if (pdfDocument.parentId == newParentId) return

                pdfDocument.parentId = newParentId
                pdfDocument.parentAbsolutePath = newParentFullPath

                pdfDocumentBox.put(pdfDocument)
            }
        }

        private fun moveFolder(
            id: Long,
            newParentId: Long?,
            newParentFullPath: String,
        ) {
            folderBox.get(id)?.also { folder ->
                if (folder.parentId == newParentId) return

                folder.parentId = newParentId

                val oldPathPrefix = folder.parentAbsolutePath + id + "/"
                val newPathPrefix = newParentFullPath + id + "/"

                val childrenFolder =
                    folderBox
                        .query(
                            FolderEntity_.parentAbsolutePath.startsWith(oldPathPrefix),
                        ).build()
                        .find()

                childrenFolder.forEach { child ->
                    child.parentAbsolutePath =
                        child.parentAbsolutePath.replaceFirst(
                            oldPathPrefix,
                            newPathPrefix,
                        )
                }

                folderBox.put(childrenFolder)

                val childrenPdfDocument =
                    pdfDocumentBox
                        .query(
                            PdfDocumentEntity_.parentAbsolutePath.startsWith(oldPathPrefix),
                        ).build()
                        .find()

                childrenPdfDocument.forEach { child ->
                    child.parentAbsolutePath =
                        child.parentAbsolutePath.replaceFirst(
                            oldPathPrefix,
                            newPathPrefix,
                        )
                }

                pdfDocumentBox.put(childrenPdfDocument)

                folder.parentAbsolutePath = newParentFullPath

                folderBox.put(folder)
            }
        }

        suspend fun moveFoldersAndPdfs(
            folderIds: List<Long>,
            pdfDocumentIds: List<Long>,
            newParentId: Long?,
        ) = runInIoTx {
            val newParentFullPath =
                newParentId?.let { parentId ->
                    folderBox.get(parentId)?.let { parentEntity ->
                        parentEntity.parentAbsolutePath + parentId + "/"
                    } ?: "/"
                } ?: "/"

            pdfDocumentIds.forEach { id ->
                movePdfDocument(id, newParentId, newParentFullPath)
            }

            folderIds.forEach { id ->
                moveFolder(id, newParentId, newParentFullPath)
            }
        }

        // Delete
        suspend fun deletePdfDocument(pdfId: Long) =
            runInIoTx {
                pdfDocumentBox.get(pdfId)?.also {
                    pageEmbeddingBox.remove(it.pageEmbeddings)
                    pdfDocumentBox.remove(it)
                }
            }

        suspend fun deletePdfDocuments(pdfIds: List<Long>) =
            runInIoTx {
                pdfIds.forEach { id ->
                    pdfDocumentBox.get(id)?.also { targetEntity ->
                        pageEmbeddingBox.remove(targetEntity.pageEmbeddings)
                        pdfDocumentBox.remove(targetEntity)
                    }
                }
            }

        suspend fun deleteFolders(folderIds: List<Long>) =
            runInIoTx {
                folderIds.forEach { id ->
                    folderBox.get(id)?.also { targetEntity ->
                        val targetFullPath = targetEntity.parentAbsolutePath + id + '/'

                        val childFolderQuery =
                            FolderEntity_
                                .parentAbsolutePath
                                .startsWith(targetFullPath)

                        val childrenFolder =
                            folderBox
                                .query(childFolderQuery)
                                .build()
                                .find()
                        folderBox.remove(childrenFolder)

                        val childPdfQuery =
                            PdfDocumentEntity_
                                .parentAbsolutePath
                                .startsWith(targetFullPath)

                        val childrenPdfDocument =
                            pdfDocumentBox
                                .query(childPdfQuery)
                                .build()
                                .find()
                        pdfDocumentBox.remove(childrenPdfDocument)

                        folderBox.remove(targetEntity)
                    }
                }
            }
    }
