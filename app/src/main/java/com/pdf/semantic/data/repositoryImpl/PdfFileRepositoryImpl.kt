package com.pdf.semantic.data.repositoryImpl

import android.content.Context
import android.net.Uri
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.model.Slide
import com.pdf.semantic.domain.repository.PdfFileRepository
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfFileRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : PdfFileRepository {
        init {
            PDFBoxResourceLoader.init(context)
        }

        override suspend fun parsePdf(uri: Uri): PdfDocument =
            withContext(Dispatchers.IO) {
                val slides = mutableListOf<Slide>()

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val document = PDDocument.load(inputStream)
                    val pdfStripper = PDFTextStripper()

                    for (page in 1..document.numberOfPages) {
                        pdfStripper.startPage = page
                        pdfStripper.endPage = page
                        val text = pdfStripper.getText(document)

                        slides.add(
                            Slide(
                                slideNumber = page,
                                content = text.trim(),
                                similarity = null,
                            ),
                        )
                    }
                    document.close()
                } ?: throw IllegalStateException("Uri로부터 InputStream을 열 수 없습니다: $uri")

                PdfDocument(uri = uri, slides = slides)
            }

    override suspend fun savePdfFile(uri: Uri): String {
        TODO("Not yet implemented")
    }

    override suspend fun deletePdfFile(internalPath: String) {
        TODO("Not yet implemented")
    }
}
