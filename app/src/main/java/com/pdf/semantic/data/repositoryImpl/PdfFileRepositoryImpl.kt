package com.pdf.semantic.data.repositoryImpl

import androidx.core.net.toUri
import com.pdf.semantic.data.datasource.PdfFileDataSource
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.model.PdfInfo
import com.pdf.semantic.domain.repository.PdfFileRepository
import javax.inject.Inject

class PdfFileRepositoryImpl
    @Inject
    constructor(
        private val dataSource: PdfFileDataSource,
    ) : PdfFileRepository {
        override suspend fun parsePdf(uriString: String): PdfDocument {
            val uri = uriString.toUri()
            return dataSource.parsePdf(uri)
        }

        override suspend fun getPdfDetail(uriString: String): PdfInfo {
            val uri = uriString.toUri()
            return dataSource.getPdfDetail(uri)
        }

        override suspend fun savePdfFile(uriString: String): String {
            val uri = uriString.toUri()
            return dataSource.savePdfFile(uri)
        }

        override suspend fun deletePdfFile(internalPath: String) {
            dataSource.deletePdfFile(internalPath)
        }
    }
