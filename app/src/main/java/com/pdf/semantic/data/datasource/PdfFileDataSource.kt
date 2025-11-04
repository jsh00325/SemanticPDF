package com.pdf.semantic.data.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.LruCache
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
            var result: String? = null
            if (uri.scheme == "content") {
                context.contentResolver
                    .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
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

        private suspend fun saveThumbnailImage(bitmap: Bitmap): String =
            withContext(Dispatchers.IO) {
                val internalDir = context.filesDir
                val uniqueFileName = "${UUID.randomUUID()}.png"
                val imageFile = File(internalDir, uniqueFileName)

                FileOutputStream(imageFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                }

                imageFile.absolutePath
            }

        suspend fun parsePdf(uriString: String): PdfDocument =
            withContext(Dispatchers.IO) {
                val uri = uriString.toUri()
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

        suspend fun getPdfDetail(uriString: String): PdfInfo =
            withContext(Dispatchers.IO) {
                val uri = uriString.toUri()
                val title = getFileName(uri)

                context.contentResolver.openFileDescriptor(uri, "r")?.use { fileDescriptor ->
                    val pdfRenderer = PdfRenderer(fileDescriptor)

                    val totalPages = pdfRenderer.pageCount
                    val thumbnailPage = pdfRenderer.openPage(0)
                    val thumbnailBitmap = createBitmap(thumbnailPage.width, thumbnailPage.height)
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

        suspend fun renderPage(
            internalPath: String,
            pageNumber: Int,
        ): Bitmap =
            withContext(Dispatchers.IO) {
                val file = File(internalPath)
                if (!file.exists()) {
                    throw IllegalStateException("파일을 찾을 수 없습니다: $internalPath")
                }

                val fileDescriptor =
                    ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

                fileDescriptor.use { fd ->
                    val pdfRenderer = PdfRenderer(fd)

                    val page = pdfRenderer.openPage(pageNumber)

                    val bitmap = createBitmap(page.width, page.height)

                    val canvas = _root_ide_package_.coil3.Canvas(bitmap)
                    canvas.drawColor(Color.WHITE)
                    canvas.drawBitmap(bitmap, 0f, 0f, null)

                    page.render(
                        bitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                    )

                    page.close()
                    pdfRenderer.close()

                    return@withContext bitmap
                }
            }

        suspend fun preloadAllPages(
            pdfId: Long,
            internalPath: String,
            totalPages: Int,
        ) {
            withContext(Dispatchers.IO) {
                for (pageIndex in 0 until totalPages) {
                    getPageBitmap(
                        pdfId = pdfId,
                        internalPath = internalPath,
                        pageNumber = pageIndex,
                    )
                }
            }
        }

        private val bitmapCache: LruCache<String, Bitmap>

        init {
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
            val cacheSize = maxMemory / 8

            bitmapCache =
                object : LruCache<String, Bitmap>(cacheSize) {
                    override fun sizeOf(
                        key: String,
                        bitmap: Bitmap,
                    ): Int = bitmap.byteCount / 1024
                }
        }

        suspend fun getPageBitmap(
            pdfId: Long,
            internalPath: String,
            pageNumber: Int,
        ): Bitmap {
            val cacheKey = "${pdfId}_$pageNumber"

            val cachedBitmap = bitmapCache.get(cacheKey)
            if (cachedBitmap != null) {
                return cachedBitmap
            }

            val renderedBitmap = renderPage(internalPath, pageNumber)

            bitmapCache.put(cacheKey, renderedBitmap)
            return renderedBitmap
        }
    }
