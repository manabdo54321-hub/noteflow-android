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

data class GroqRequest(val model: String, val messages: List<GroqMessage>)
data class GroqMessage(val role: String, val content: String)
data class GroqResponse(val choices: List<GroqChoice>?)
data class GroqChoice(val message: GroqMessage?)

@Singleton
class AiRepository @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val apiKey = com.noteflow.app.BuildConfig.GROQ_API_KEY
    private val url = "https://api.groq.com/openai/v1/chat/completions"

    suspend fun sendMessage(
        history: List<AiMessage>,
        userMessage: String,
        systemContext: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val messages = mutableListOf<GroqMessage>()

            if (systemContext.isNotBlank()) {
                messages.add(GroqMessage(role = "system", content = systemContext))
            }

            history.forEach { msg ->
                messages.add(GroqMessage(
                    role = if (msg.role == "user") "user" else "assistant",
                    content = msg.text
                ))
            }

            messages.add(GroqMessage(role = "user", content = userMessage))

            val body = gson.toJson(GroqRequest(
                model = "llama-3.3-70b-versatile",
                messages = messages
            )).toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("خطأ ${response.code}: $responseBody"))
            }

            val groqResponse = gson.fromJson(responseBody, GroqResponse::class.java)
            val text = groqResponse.choices?.firstOrNull()?.message?.content
                ?: "مفيش رد من الـ AI"

            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
