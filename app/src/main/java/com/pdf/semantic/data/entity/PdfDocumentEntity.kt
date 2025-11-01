package com.pdf.semantic.data.entity

import com.pdf.semantic.data.converter.EmbeddingStatusConverter
import com.pdf.semantic.domain.model.EmbeddingStatus
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import java.util.Date

@Entity
data class PdfDocumentEntity(
    @Id
    var id: Long = 0,
    var title: String = "",
    var internalFilePath: String = "",
    var totalPages: Int = 0,
    var processedPages: Int = 0,
    var thumbnail: String = "",
    var createdAt: Date = Date(),
    @Convert(converter = EmbeddingStatusConverter::class, dbType = Int::class)
    var embeddingStatus: EmbeddingStatus = EmbeddingStatus.PENDING,
) {
    @Backlink(to = "pdfDocument")
    lateinit var pageEmbeddings: ToMany<PageEmbeddingEntity>
}
