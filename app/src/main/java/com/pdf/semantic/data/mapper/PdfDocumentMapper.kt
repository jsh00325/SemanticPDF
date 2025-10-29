package com.pdf.semantic.data.mapper

import com.pdf.semantic.data.entity.PdfDocumentEntity
import com.pdf.semantic.domain.model.PdfItem

object PdfDocumentMapper {
    fun PdfDocumentEntity.toModel(): PdfItem =
        PdfItem(
            id = id,
            title = title,
            createdTime = createdAt,
            status = embeddingStatus,
            totalPages = totalPages,
            progressedPages = processedPages,
        )

    fun List<PdfDocumentEntity>.toModels(): List<PdfItem> = this.map { it.toModel() }
}
