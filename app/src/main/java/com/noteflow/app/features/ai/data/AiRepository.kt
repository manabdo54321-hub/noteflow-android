package com.noteflow.app.features.ai.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class AiMessage(val role: String, val text: String)

data class GeminiRequest(val contents: List<GeminiContent>)
data class GeminiContent(val role: String, val parts: List<GeminiPart>)
data class GeminiPart(val text: String)
data class GeminiResponse(val candidates: List<GeminiCandidate>?)
data class GeminiCandidate(val content: GeminiContent?)

@Singleton
class AiRepository @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val apiKey = "AIzaSyBpIp1aeGXK6wY9ZCrRSOzN_pS7OR6ywOo"
    private val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    suspend fun sendMessage(
        history: List<AiMessage>,
        userMessage: String,
        systemContext: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contents = mutableListOf<GeminiContent>()

            if (systemContext.isNotBlank()) {
                contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(systemContext))))
                contents.add(GeminiContent(role = "model", parts = listOf(GeminiPart("فهمت! أنا جاهز أساعدك."))))
            }

            history.forEach { msg ->
                contents.add(GeminiContent(
                    role = if (msg.role == "user") "user" else "model",
                    parts = listOf(GeminiPart(msg.text))
                ))
            }

            contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(userMessage))))

            val body = gson.toJson(GeminiRequest(contents))
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder().url(url).post(body).build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("خطأ ${response.code}: $responseBody"))
            }

            val geminiResponse = gson.fromJson(responseBody, GeminiResponse::class.java)
            val text = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "مفيش رد من الـ AI"

            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
