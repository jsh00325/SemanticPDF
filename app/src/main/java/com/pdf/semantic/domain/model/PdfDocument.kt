package com.pdf.semantic.domain.model

import android.net.Uri

data class PdfDocument(
    val uri: Uri,
    val slides: List<Slide>
)
