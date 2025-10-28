package com.pdf.semantic.data.datasource

import io.objectbox.BoxStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfDocumentDbDatasource
    @Inject
    constructor(
        private val boxStore: BoxStore,
    )
