package com.pdf.semantic.domain.model

data class Slide(
    val slideNumber: Int,
    val content: String,
    val similarity: Float?,
)
