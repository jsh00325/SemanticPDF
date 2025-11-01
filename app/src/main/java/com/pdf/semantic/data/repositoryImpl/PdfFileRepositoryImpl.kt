package com.pdf.semantic.data.repositoryImpl

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
        override suspend fun parsePdf(uriString: String): PdfDocument =
            dataSource.parsePdf(uriString)

        override suspend fun getPdfDetail(uriString: String): PdfInfo =
            dataSource.getPdfDetail(uriString)

        override suspend fun savePdfFile(uriString: String): String =
            dataSource.savePdfFile(uriString)

        override suspend fun deletePdfFile(internalPath: String) {
            dataSource.deletePdfFile(internalPath)
        }
    }
