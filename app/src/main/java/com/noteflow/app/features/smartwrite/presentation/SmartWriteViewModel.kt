package com.noteflow.app.features.smartwrite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.notes.domain.usecase.SaveNoteUseCase
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import com.noteflow.app.features.tasks.domain.model.Task
import com.noteflow.app.features.tasks.domain.model.TaskPriority
import com.noteflow.app.features.ai.data.AiRepository
import com.noteflow.app.features.ai.data.AiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
    private val taskRepository: TaskRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

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

    private suspend fun callGroq(text: String): SmartWriteResult {
        val systemPrompt = "انت مساعد ذكي لتنظيم الافكار. حلل النص وارجع JSON فقط بدون اي نص خارجه: {\"title\":\"عنوان\",\"type\":\"NOTE|TASK|IDEA|GOAL|FEELING|MIXED\",\"refined\":\"النص المحسن\",\"tags\":[\"وسم\"],\"tasks\":[\"مهمة\"],\"suggestions\":[\"اقتراح\"],\"link_timer\":false,\"minutes\":0}"
        val result = aiRepository.sendMessage(
            history = emptyList(),
            userMessage = text,
            systemContext = systemPrompt
        )
        return if (result.isSuccess) {
            parseResponse(result.getOrDefault(""), text)
        } else {
            buildFallback("", text)
        }
    }

    private fun parseResponse(response: String, original: String): SmartWriteResult {
        return try {
            val json = org.json.JSONObject(response.trim())
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

    private fun org.json.JSONArray?.toList(): List<String> {
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
