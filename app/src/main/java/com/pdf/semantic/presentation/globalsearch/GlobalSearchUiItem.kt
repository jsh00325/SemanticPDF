package com.pdf.semantic.presentation.globalsearch

import android.graphics.Bitmap

data class GlobalSearchUiItem(
    val pdfId: Long,
    val pdfTitle: String,
    val totalPages: Int,
    val slideNumber: Int,
    val similarityScore: Double,
    val slidePreviewImage: Bitmap?,
)
