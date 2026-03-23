package com.noteflow.app.features.ai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.ai.data.AiMessage
import com.noteflow.app.features.ai.data.AiRepository
import com.noteflow.app.features.notes.data.repository.NoteRepository
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiUiState(
    val messages: List<AiMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AiViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    private val _notes = noteRepository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _tasks = taskRepository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = AiMessage("user", userText)
        val currentMessages = _uiState.value.messages + userMsg
        _uiState.update { it.copy(messages = currentMessages, isLoading = true, error = null) }

        viewModelScope.launch {
            val context = buildContext()
            val history = currentMessages.dropLast(1)
            val result = aiRepository.sendMessage(history, userText, context)
            result.fold(
                onSuccess = { reply ->
                    _uiState.update {
                        it.copy(
                            messages = currentMessages + AiMessage("model", reply),
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "حصل خطأ")
                    }
                }
            )
        }
    }

    private fun buildContext(): String {
        val notes = _notes.value
        val tasks = _tasks.value
        val activeTasks = tasks.filter { !it.isCompleted }
        val doneTasks = tasks.filter { it.isCompleted }
        val noteTitles = notes.take(20).joinToString("، ") { it.title }

        return """
أنت مساعد ذكي داخل تطبيق NoteFlow للملاحظات والمهام.
تتحدث بالعربية دائماً ما لم يطلب المستخدم غير ذلك.
أنت ودود، مختصر، ومفيد.

معلومات المستخدم الحالية:
- عدد الملاحظات: ${notes.size}
- عناوين أحدث الملاحظات: $noteTitles
- المهام النشطة (${activeTasks.size}): ${activeTasks.take(10).joinToString("، ") { it.title }}
- المهام المكتملة: ${doneTasks.size}

يمكنك مساعدة المستخدم في:
- تلخيص ملاحظاته أو تحليلها
- اقتراح أفكار جديدة
- تنظيم مهامه وترتيب أولوياتها
- الإجابة على أي سؤال عام
        """.trimIndent()
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    fun clearChat() = _uiState.update { it.copy(messages = emptyList(), error = null) }
}
