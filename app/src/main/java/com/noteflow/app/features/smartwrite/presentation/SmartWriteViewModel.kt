package com.noteflow.app.features.smartwrite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.notes.domain.usecase.SaveNoteUseCase
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import com.noteflow.app.features.tasks.domain.model.Task
import com.noteflow.app.features.tasks.domain.model.TaskPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

enum class SmartContentType(val label: String, val emoji: String) {
    NOTE("ملاحظة", "📝"),
    TASK("مهام", "✅"),
    IDEA("فكرة", "💡"),
    GOAL("هدف", "🎯"),
    FEELING("شعور", "💭"),
    MIXED("متنوع", "✨")
}

data class SmartWriteResult(
    val originalText: String = "",
    val refinedText: String = "",
    val title: String = "",
    val contentType: SmartContentType = SmartContentType.NOTE,
    val tags: List<String> = emptyList(),
    val extractedTasks: List<String> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val shouldLinkTimer: Boolean = false,
    val estimatedMinutes: Int = 0
)

sealed class SmartWriteState {
    object Idle : SmartWriteState()
    object Analyzing : SmartWriteState()
    data class Result(val result: SmartWriteResult) : SmartWriteState()
    data class Saved(val message: String) : SmartWriteState()
    data class Error(val message: String) : SmartWriteState()
}

@HiltViewModel
class SmartWriteViewModel @Inject constructor(
    private val saveNoteUseCase: SaveNoteUseCase,
    private val taskRepository: TaskRepository
) : ViewModel() {

    companion object {
        const val GROQ_API_KEY = "YOUR_GROQ_API_KEY_HERE"
        const val GROQ_MODEL = "llama3-8b-8192"
        const val GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"
    }

    private val _state = MutableStateFlow<SmartWriteState>(SmartWriteState.Idle)
    val state: StateFlow<SmartWriteState> = _state

    fun analyze(title: String, content: String) {
        if (content.isBlank() && title.isBlank()) return
        val fullText = if (title.isNotBlank()) "$title\n$content" else content
        viewModelScope.launch {
            _state.value = SmartWriteState.Analyzing
            try {
                val result = callGroq(fullText)
                _state.value = SmartWriteState.Result(result)
            } catch (e: Exception) {
                _state.value = SmartWriteState.Result(buildFallback(title, content))
            }
        }
    }

    private suspend fun callGroq(text: String): SmartWriteResult = withContext(Dispatchers.IO) {
        val systemPrompt = "انت مساعد ذكي لتنظيم الافكار. حلل النص وارجع JSON فقط بدون اي نص خارجه: {\"title\":\"عنوان\",\"type\":\"NOTE|TASK|IDEA|GOAL|FEELING|MIXED\",\"refined\":\"النص المحسن\",\"tags\":[\"وسم\"],\"tasks\":[\"مهمة\"],\"suggestions\":[\"اقتراح\"],\"link_timer\":false,\"minutes\":0}"
        val body = JSONObject().apply {
            put("model", GROQ_MODEL)
            put("max_tokens", 700)
            put("messages", JSONArray().apply {
                put(JSONObject().apply { put("role", "system"); put("content", systemPrompt) })
                put(JSONObject().apply { put("role", "user");   put("content", text) })
            })
        }
        val conn = (URL(GROQ_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer $GROQ_API_KEY")
            doOutput = true
            connectTimeout = 15000
            readTimeout = 20000
        }
        conn.outputStream.use { it.write(body.toString().toByteArray()) }
        val response = conn.inputStream.bufferedReader().readText()
        parseResponse(response, text)
    }

    private fun parseResponse(response: String, original: String): SmartWriteResult {
        return try {
            val content = JSONObject(response)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content").trim()
            val json = JSONObject(content)
            val type = try { SmartContentType.valueOf(json.optString("type", "NOTE")) }
                       catch (e: Exception) { SmartContentType.NOTE }
            SmartWriteResult(
                originalText = original,
                refinedText = json.optString("refined", original),
                title = json.optString("title", "ملاحظة جديدة"),
                contentType = type,
                tags = json.optJSONArray("tags").toList(),
                extractedTasks = json.optJSONArray("tasks").toList(),
                suggestions = json.optJSONArray("suggestions").toList(),
                shouldLinkTimer = json.optBoolean("link_timer", false),
                estimatedMinutes = json.optInt("minutes", 0)
            )
        } catch (e: Exception) {
            buildFallback("", original)
        }
    }

    private fun JSONArray?.toList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).map { getString(it) }
    }

    private fun buildFallback(title: String, content: String): SmartWriteResult {
        val t = title.ifBlank { content.take(50) }
        val hasTasks = content.contains(Regex("يجب|لازم|ضروري|احتاج|عايز اعمل"))
        return SmartWriteResult(
            originalText = content,
            refinedText = content,
            title = t,
            contentType = if (hasTasks) SmartContentType.TASK else SmartContentType.NOTE,
            tags = listOf("عام"),
            extractedTasks = emptyList(),
            suggestions = listOf("اضف تفاصيل اكثر لتحليل افضل")
        )
    }

    fun saveAsNote(result: SmartWriteResult) {
        viewModelScope.launch {
            saveNoteUseCase(Note(
                id = 0,
                title = result.title,
                content = result.refinedText
            ))
            _state.value = SmartWriteState.Saved("تم الحفظ كملاحظة")
        }
    }

    fun saveTasks(result: SmartWriteResult) {
        viewModelScope.launch {
            result.extractedTasks.forEach { t ->
                taskRepository.saveTask(Task(
                    id = 0,
                    title = t,
                    isCompleted = false,
                    priority = TaskPriority.MEDIUM
                ))
            }
            _state.value = SmartWriteState.Saved("تمت اضافة ${result.extractedTasks.size} مهمة")
        }
    }

    fun reset() { _state.value = SmartWriteState.Idle }
}
