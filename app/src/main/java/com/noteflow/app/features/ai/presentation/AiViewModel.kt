package com.noteflow.app.features.ai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.noteflow.app.features.ai.data.AiAction
import com.noteflow.app.features.ai.data.AiActionExecutor
import com.noteflow.app.features.ai.data.AiMessage
import com.noteflow.app.features.ai.data.AiRepository
import com.noteflow.app.features.ai.data.local.AiChatDao
import com.noteflow.app.features.ai.data.local.AiChatEntity
import com.noteflow.app.features.notes.data.repository.NoteRepository
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import com.noteflow.app.features.timer.presentation.TimerViewModel
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
    private val taskRepository: TaskRepository,
    private val aiChatDao: AiChatDao,
    private val aiActionExecutor: AiActionExecutor
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()
    private val gson = Gson()

    private val _notes = noteRepository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _tasks = taskRepository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { loadHistory() }

    fun setTimerViewModel(timerViewModel: TimerViewModel) {
        aiActionExecutor.timerViewModel = timerViewModel
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val chats = aiChatDao.getAllChats()
            val messages = chats.map { AiMessage(it.role, it.text) }
            _uiState.update { it.copy(messages = messages) }
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = AiMessage("user", userText)
        val currentMessages = _uiState.value.messages + userMsg
        _uiState.update { it.copy(messages = currentMessages, isLoading = true, error = null) }

        viewModelScope.launch {
            aiChatDao.insertChat(AiChatEntity(role = "user", text = userText))
            val context = buildContext()
            val history = currentMessages.dropLast(1).takeLast(20)
            val result = aiRepository.sendMessage(history, userText, context)
            result.fold(
                onSuccess = { reply ->
                    val action = parseAction(reply)
                    val actionResult = if (action !is AiAction.None) {
                        aiActionExecutor.execute(action)
                    } else ""
                    val cleanReply = extractMessage(reply)
                    val finalReply = if (actionResult.isNotBlank()) "$cleanReply\n$actionResult" else cleanReply
                    aiChatDao.insertChat(AiChatEntity(role = "model", text = finalReply))
                    _uiState.update {
                        it.copy(
                            messages = currentMessages + AiMessage("model", finalReply),
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "حصل خطأ") }
                }
            )
        }
    }

    private fun parseAction(reply: String): AiAction {
        return try {
            val jsonRegex = Regex("""\{[^{}]*"action"[^{}]*\}""")
            val match = jsonRegex.find(reply) ?: return AiAction.None()
            val json = gson.fromJson(match.value, Map::class.java)
            when (json["action"]?.toString()) {
                "ADD_TASK" -> AiAction.AddTask(
                    title = json["title"]?.toString() ?: "",
                    priority = json["priority"]?.toString() ?: "MEDIUM"
                )
                "COMPLETE_TASK" -> AiAction.CompleteTask(json["title"]?.toString() ?: "")
                "DELETE_TASK" -> AiAction.DeleteTask(json["title"]?.toString() ?: "")
                "ADD_NOTE" -> AiAction.AddNote(
                    title = json["title"]?.toString() ?: "",
                    content = json["content"]?.toString() ?: ""
                )
                "DELETE_NOTE" -> AiAction.DeleteNote(json["title"]?.toString() ?: "")
                "START_TIMER" -> AiAction.StartTimer(
                    minutes = json["minutes"]?.toString()?.toIntOrNull() ?: 25,
                    taskTitle = json["task"]?.toString()
                )
                "STOP_TIMER" -> AiAction.StopTimer()
                "CREATE_PLAN" -> AiAction.CreatePlan(
                    title = json["title"]?.toString() ?: "",
                    items = (json["items"] as? List<*>)?.map { it.toString() } ?: emptyList()
                )
                else -> AiAction.None()
            }
        } catch (e: Exception) { AiAction.None() }
    }

    private fun extractMessage(reply: String): String {
        return reply.replace(Regex("""\{[^{}]*"action"[^{}]*\}"""), "").trim()
    }

    private fun buildContext(): String {
        val notes = _notes.value
        val tasks = _tasks.value
        val activeTasks = tasks.filter { !it.isCompleted }
        val doneTasks = tasks.filter { it.isCompleted }
        val noteTitles = notes.take(20).joinToString("، ") { it.title }
        val activeTasksList = activeTasks.take(10).joinToString("، ") { "${it.title} (${it.priority})" }

        return """
أنت مساعد ذكي داخل تطبيق NoteFlow — تطبيق ملاحظات ومهام وتركيز.
تتحدث بالعربية دائماً. أنت ودود، مختصر، وتنفذ الأوامر فوراً.

معلومات التطبيق الحالية:
- الملاحظات: ${notes.size} ملاحظة
- عناوين الملاحظات: $noteTitles
- المهام النشطة (${activeTasks.size}): $activeTasksList
- المهام المكتملة: ${doneTasks.size}
- ألوان التطبيق: بنفسجي (#CABEFF) على خلفية داكنة (#131313)

قواعد مهمة جداً:
1. لو المستخدم طلب إضافة مهمة، رد بالنص العادي + JSON في نفس الرد:
   {"action":"ADD_TASK","title":"اسم المهمة","priority":"HIGH/MEDIUM/LOW"}

2. لو طلب إكمال مهمة:
   {"action":"COMPLETE_TASK","title":"اسم المهمة"}

3. لو طلب حذف مهمة:
   {"action":"DELETE_TASK","title":"اسم المهمة"}

4. لو طلب إضافة ملاحظة:
   {"action":"ADD_NOTE","title":"العنوان","content":"المحتوى"}

5. لو طلب حذف ملاحظة:
   {"action":"DELETE_NOTE","title":"اسم الملاحظة"}

6. لو طلب بدء تايمر:
   {"action":"START_TIMER","minutes":25,"task":"اسم المهمة"}

7. لو طلب إيقاف التايمر:
   {"action":"STOP_TIMER"}

8. لو طلب خطة أو جدول:
   {"action":"CREATE_PLAN","title":"عنوان الخطة","items":["بند 1","بند 2","بند 3"]}

9. لو مش في أمر محدد — رد عادي بدون JSON.
        """.trimIndent()
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    fun clearChat() {
        viewModelScope.launch {
            aiChatDao.clearChats()
            _uiState.update { it.copy(messages = emptyList(), error = null) }
        }
    }
}
