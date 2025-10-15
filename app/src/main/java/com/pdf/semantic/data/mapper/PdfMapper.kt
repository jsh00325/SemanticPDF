package com.pdf.semantic.data.mapper

import com.pdf.semantic.data.entity.SlideEntity
import com.pdf.semantic.domain.model.Slide

fun SlideEntity.toDomain(): Slide =
    Slide(
        slideNumber = this.slideNo,
        content = this.content,
        similarity = null,
    )

fun Slide.toEntity(pdfUri: String): SlideEntity =
    SlideEntity(
        pdfUri = pdfUri,
        slideNo = this.slideNumber,
        content = this.content,
    )
