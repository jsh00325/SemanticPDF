package com.pdf.semantic.data.repositoryImpl

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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

            val paragraphRegex = "\n{2,}".toRegex()
            val paragraphPlaceholder = "__PARAGRAPH_BREAK__"

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val document = PDDocument.load(inputStream)
                val pdfStripper = PDFTextStripper()

                for (page in 1..document.numberOfPages) {
                    pdfStripper.startPage = page
                    pdfStripper.endPage = page

                    val rawText = pdfStripper.getText(document)
                    val textStep1 = rawText.replace("-\n", "")
                    val textStep2 = textStep1.replace(paragraphRegex, paragraphPlaceholder)
                    val textStep3 = textStep2.replace("\n", " ")
                    val finalContent = textStep3.replace(paragraphPlaceholder, "\n")

                    slides.add(
                        Slide(
                            slideNumber = page,
                            content = finalContent.trim(),
                            similarity = null,
                        ),
                    )
                }
                document.close()
            } ?: throw IllegalStateException("Uri로부터 InputStream을 열 수 없습니다: $uri")
            val title = getFileName(uri)
            PdfDocument(uri = uri, title = title, slides = slides)
        }

    override suspend fun savePdfFile(uri: Uri): String {
        TODO("Not yet implemented")
    }

    override suspend fun deletePdfFile(internalPath: String) {
        TODO("Not yet implemented")
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) {
                            result = cursor.getString(nameIndex)
                        }
                    }
                }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "Unknown Title"
    }
}
