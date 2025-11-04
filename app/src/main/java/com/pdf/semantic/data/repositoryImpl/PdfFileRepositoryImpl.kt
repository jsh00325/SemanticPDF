package com.pdf.semantic.data.repositoryImpl

import android.graphics.Bitmap
import com.pdf.semantic.data.datasource.PdfFileDataSource
import com.pdf.semantic.domain.model.PdfInfo
import com.pdf.semantic.domain.repository.PdfFileRepository
import javax.inject.Inject

class PdfFileRepositoryImpl
    @Inject
    constructor(
        private val dataSource: PdfFileDataSource,
    ) : PdfFileRepository {
        override suspend fun getPdfDetail(uriString: String): PdfInfo =
            dataSource.getPdfDetail(uriString)

        override suspend fun savePdfFile(uriString: String): String =
            dataSource.savePdfFile(uriString)

        override suspend fun deletePdfFile(internalPath: String) {
            dataSource.deletePdfFile(internalPath)
        }

        override suspend fun renderPage(
            internalPath: String,
            pageNumber: Int,
        ): Bitmap = dataSource.renderPage(internalPath, pageNumber)

        override suspend fun preloadAllPages(
            pdfId: Long,
            internalPath: String,
            totalPages: Int,
        ) {
            dataSource.preloadAllPages(pdfId, internalPath, totalPages)
        }

        override suspend fun getPageBitmap(
            pdfId: Long,
            internalPath: String,
            pageNumber: Int,
        ): Bitmap = dataSource.getPageBitmap(pdfId, internalPath, pageNumber)
    }
