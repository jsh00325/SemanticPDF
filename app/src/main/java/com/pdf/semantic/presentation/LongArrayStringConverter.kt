package com.pdf.semantic.presentation

object LongArrayStringConverter {
    private const val DELIMITER = ","

    fun List<Long>.toSerializedString(): String = joinToString(DELIMITER)

    fun String?.toLongList(): List<Long> =
        this
            ?.split(DELIMITER)
            ?.mapNotNull { it.trim().toLongOrNull() }
            ?: emptyList()
}
