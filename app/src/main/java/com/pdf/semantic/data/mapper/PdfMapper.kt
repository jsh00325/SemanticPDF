package com.pdf.semantic.data.mapper

import com.pdf.semantic.data.datasource.local.SlideEntity
import com.pdf.semantic.domain.model.Slide


fun SlideEntity.toDomain(): Slide {
    return Slide(
        slideNumber = this.slideNo,
        content = this.content
    )
}

fun Slide.toEntity(pdfUri: String): SlideEntity {
    return SlideEntity(
        pdfUri = pdfUri,
        slideNo = this.slideNumber,
        content = this.content
    )
}
