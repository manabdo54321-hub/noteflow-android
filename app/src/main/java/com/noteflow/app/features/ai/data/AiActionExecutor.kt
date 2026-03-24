package com.noteflow.app.features.ai.data

import com.noteflow.app.features.notes.data.repository.NoteRepository
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import com.noteflow.app.features.tasks.domain.model.Task
import com.noteflow.app.features.tasks.domain.model.TaskPriority
import com.noteflow.app.features.timer.presentation.TimerViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiActionExecutor @Inject constructor(
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository
) {
    var timerViewModel: TimerViewModel? = null

    suspend fun execute(action: AiAction): String {
        return when (action) {
            is AiAction.AddTask -> {
                val priority = when (action.priority.uppercase()) {
                    "HIGH" -> TaskPriority.HIGH
                    "LOW" -> TaskPriority.LOW
                    else -> TaskPriority.MEDIUM
                }
                taskRepository.saveTask(Task(title = action.title, priority = priority))
                "✅ تمت إضافة المهمة: ${action.title}"
            }
            is AiAction.CompleteTask -> {
                val tasks = taskRepository.getAllTasks().first()
                val task = tasks.find { it.title.contains(action.title, ignoreCase = true) }
                if (task != null) {
                    taskRepository.setCompleted(task.id, true)
                    "✅ تم إكمال المهمة: ${task.title}"
                } else "❌ مش لاقي المهمة دي"
            }
            is AiAction.DeleteTask -> {
                val tasks = taskRepository.getAllTasks().first()
                val task = tasks.find { it.title.contains(action.title, ignoreCase = true) }
                if (task != null) {
                    taskRepository.deleteTask(task)
                    "🗑️ تم حذف المهمة: ${task.title}"
                } else "❌ مش لاقي المهمة دي"
            }
            is AiAction.AddNote -> {
                noteRepository.saveNote(Note(title = action.title, content = action.content))
                "📝 تمت إضافة الملاحظة: ${action.title}"
            }
            is AiAction.DeleteNote -> {
                val notes = noteRepository.getAllNotes().first()
                val note = notes.find { it.title.contains(action.title, ignoreCase = true) }
                if (note != null) {
                    noteRepository.deleteNote(note)
                    "🗑️ تم حذف الملاحظة: ${note.title}"
                } else "❌ مش لاقي الملاحظة دي"
            }
            is AiAction.StartTimer -> {
                timerViewModel?.setCustomDuration(0, action.minutes)
                timerViewModel?.start()
                "⏱️ بدأ التايمر ${action.minutes} دقيقة"
            }
            is AiAction.StopTimer -> {
                timerViewModel?.pause()
                "⏹️ تم إيقاف التايمر"
            }
            is AiAction.CreatePlan -> {
                val content = action.items.mapIndexed { i, item -> "${i+1}. $item" }.joinToString("\n")
                noteRepository.saveNote(Note(title = action.title, content = content))
                "📋 تم إنشاء الخطة: ${action.title}"
            }
            else -> ""
        }
    }
}
