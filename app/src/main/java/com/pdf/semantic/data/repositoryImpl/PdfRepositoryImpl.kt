package com.pdf.semantic.data.repositoryImpl

import android.content.Context
import android.net.Uri
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.model.Slide
import com.pdf.semantic.domain.repository.PdfRepository
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PdfRepositoryImpl(
    private val context: Context
) : PdfRepository {

    init {
        // 앱 시작 시 한 번만 호출하는 것이 좋지만, Repository 생성 시 초기화도 가능합니다.
        PDFBoxResourceLoader.init(context)
    }

    override suspend fun parsePdf(uri: Uri): PdfDocument {
        return withContext(Dispatchers.IO) {
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
                            content = text.trim()
                        )
                    )
                }
                document.close()
            } ?: throw IllegalStateException("Uri로부터 InputStream을 열 수 없습니다: $uri")

            PdfDocument(uri = uri, slides = slides)
        }
    }
}
