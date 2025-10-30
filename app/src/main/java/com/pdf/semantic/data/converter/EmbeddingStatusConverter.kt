package com.pdf.semantic.data.converter

import com.pdf.semantic.domain.model.EmbeddingStatus
import io.objectbox.converter.PropertyConverter

class EmbeddingStatusConverter : PropertyConverter<EmbeddingStatus, Int> {
    override fun convertToEntityProperty(databaseValue: Int): EmbeddingStatus =
        EmbeddingStatus.entries.firstOrNull { it.id == databaseValue }
            ?: throw IllegalArgumentException("Invalid database value: $databaseValue")

    override fun convertToDatabaseValue(entityProperty: EmbeddingStatus): Int = entityProperty.id
}
