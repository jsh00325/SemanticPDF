package com.pdf.semantic.domain.repository

import android.net.Uri

interface PdfRepository {
    /*
   URI의 PDF 파일 파싱, DB에 저장
    */
    suspend fun parsePdf(uri: Uri): List<String>
}
