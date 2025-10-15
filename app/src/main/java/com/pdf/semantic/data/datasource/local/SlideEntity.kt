package com.pdf.semantic.data.datasource.local

import androidx.room.Entity

@Entity(tableName = "slides", primaryKeys = ["pdfUri", "slideNo"])
data class SlideEntity(
    val pdfUri: String,
    val slideNo: Int,
    val content: String,
)
