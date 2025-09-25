package com.pdf.semantic.data.datasource.local

import android.net.Uri
import androidx.room.Entity

@Entity(tableName = "slides", primaryKeys = ["pdfUri", "slideNo"])
data class SlideEntity(
    val pdfUri: Uri,
    val slideNo: Int,
    val content: String
)
