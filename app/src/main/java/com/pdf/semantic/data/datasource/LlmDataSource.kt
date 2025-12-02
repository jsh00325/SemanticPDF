package com.pdf.semantic.data.datasource

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmDataSource
    @Inject
    constructor() {
        private val model =
            Firebase
                .ai(backend = GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash-lite")

        suspend fun expandQueryForRetrieval(query: String): String =
            withContext(Dispatchers.IO) {
                try {
                    val prompt =
                        """
                        Answer the following query:
                        $query
                        Give the rationale before answering.
                        Answer within 100 tokens.
                        """.trimIndent()

                    val response = model.generateContent(prompt)
                    val responseText = response.text

                    if (responseText.isNullOrBlank()) {
                        throw NullPointerException("응답이 올바르게 생성되지 않았습니다.")
                    } else {
                        val expandedQuery = "$query ".repeat(5) + responseText
                        expandedQuery
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "쿼리를 확장하는데 실패하였습니다.", e)
                    query
                }
            }

        companion object {
            private const val TAG = "LlmDataSource"
        }
    }
