package com.pdf.semantic.data.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.VectorDistanceType
import io.objectbox.relation.ToOne

@Entity
data class PageEmbeddingEntity(
    @Id
    var id: Long = 0,
    var pageNumber: Int = 0,
    @HnswIndex(dimensions = 768, distanceType = VectorDistanceType.COSINE)
    var embeddingVector: FloatArray = FloatArray(768),
) {
    lateinit var pdfDocument: ToOne<PdfDocumentEntity>
}
