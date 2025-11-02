package com.pdf.semantic.data.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.pdf.semantic.domain.model.PdfDocument
import com.pdf.semantic.domain.model.PdfInfo
import com.pdf.semantic.domain.model.Slide
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Singleton
class PdfFileDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private fun getFileName(uri: Uri): String {
            if (uri.scheme == "content") {
                context.contentResolver
                    .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                    ?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (nameIndex >= 0) {
                                return cursor.getString(nameIndex).removeSuffix(".pdf")
                            }
                        }
                    }
            }

            return uri.path
                ?.substringAfterLast('/')
                ?.removeSuffix(".pdf")
                ?: "Unknown Title"
        }

        private suspend fun saveThumbnailImage(bitmap: Bitmap): String =
            withContext(Dispatchers.IO) {
                val internalDir = context.filesDir
                val uniqueFileName = "${UUID.randomUUID()}.jpeg"
                val imageFile = File(internalDir, uniqueFileName)

                FileOutputStream(imageFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.flush()
                }

                imageFile.absolutePath
            }

        suspend fun parsePdfByInternalPath(internalPath: String): List<Slide> =
            withContext(Dispatchers.IO) {
                val slides = mutableListOf<Slide>()

                val file = File(internalPath)
                val document = PDDocument.load(file)
                val pdfStripper = PDFTextStripper()

                for (page in 1..document.numberOfPages) {
                    pdfStripper.startPage = page
                    pdfStripper.endPage = page

                    val rawText = pdfStripper.getText(document)
                    val textStep1 = rawText.replace("-\n", "")
                    val textStep2 = textStep1.replace(PARAGRAPH_REGEX, PARAGRAPH_PLACEHOLDER)
                    val textStep3 = textStep2.replace("\n", " ")
                    val finalContent = textStep3.replace(PARAGRAPH_PLACEHOLDER, "\n")

                    slides.add(
                        Slide(
                            slideNumber = page,
                            content = finalContent.trim(),
                            similarity = null,
                        ),
                    )
                }
                document.close()

                slides
            }

        suspend fun getPdfDetail(uriString: String): PdfInfo =
            withContext(Dispatchers.IO) {
                val uri = uriString.toUri()
                val title = getFileName(uri)

                context.contentResolver.openFileDescriptor(uri, "r")?.use { fileDescriptor ->
                    val pdfRenderer = PdfRenderer(fileDescriptor)

                    val totalPages = pdfRenderer.pageCount
                    val thumbnailPage = pdfRenderer.openPage(0)
                    val thumbnailBitmap =
                        createBitmap(
                            thumbnailPage.width,
                            thumbnailPage.height,
                            Bitmap.Config.ARGB_8888,
                        )

                    val canvas = android.graphics.Canvas(thumbnailBitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    canvas.drawBitmap(thumbnailBitmap, 0f, 0f, null)

                    thumbnailPage.render(
                        thumbnailBitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                    )

                    thumbnailPage.close()
                    pdfRenderer.close()

                    val thumbnailFilePath = saveThumbnailImage(thumbnailBitmap)

                    PdfInfo(
                        title = title,
                        totalPages = totalPages,
                        thumbnailFilePath = thumbnailFilePath,
                    )
                } ?: throw IllegalStateException("Uri로부터 FileDescriptor를 열 수 없습니다: $uri")
            }

        suspend fun savePdfFile(uriString: String): String =
            withContext(Dispatchers.IO) {
                val uri = uriString.toUri()

                val internalDir = context.filesDir
                val uniqueFileName = "${UUID.randomUUID()}.pdf"
                val destinationFile = File(internalDir, uniqueFileName)

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                } ?: throw IllegalStateException("Uri로부터 InputStream을 열 수 없습니다: $uri")

                destinationFile.absolutePath
            }

        suspend fun deletePdfFile(internalPath: String) =
            withContext(Dispatchers.IO) {
                val file = File(internalPath)
                if (file.exists()) {
                    file.delete()
                }
            }

        companion object {
            private val PARAGRAPH_REGEX = "\n{2,}".toRegex()
            private const val PARAGRAPH_PLACEHOLDER = "__PARAGRAPH_BREAK__"
        }
    }
