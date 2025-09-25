package com.pdf.semantic.data.repositoryImpl

import android.content.Context
import android.net.Uri
import com.pdf.semantic.domain.repository.PdfRepository
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

class PdfRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PdfRepository {

    init {
        PDFBoxResourceLoader.init(context)
    }

    override suspend fun parsePdf(uri: Uri): List<String> {
        val parsedText = mutableListOf<String>()
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val document = PDDocument.load(inputStream)
                val stripper = PDFTextStripper()
                for (i in 1..document.numberOfPages) {
                    stripper.startPage = i
                    stripper.endPage = i
                    val text = stripper.getText(document)
                    parsedText.add(text)
                }
                document.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 예외 처리
        }
        return parsedText
    }
}
