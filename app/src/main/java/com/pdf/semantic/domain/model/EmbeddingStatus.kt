package com.pdf.semantic.domain.model

enum class EmbeddingStatus(
    val id: Int,
) {
    PENDING(0),
    IN_PROGRESS(1),
    COMPLETE(2),
    FAIL(3),
}
