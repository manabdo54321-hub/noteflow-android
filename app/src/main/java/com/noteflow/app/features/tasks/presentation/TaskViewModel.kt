package com.noteflow.app.features.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.tasks.data.local.TaskEntity
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveTask(title: String, noteId: Long? = null, id: Long = 0) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val task = TaskEntity(
                id = id,
                title = title.trim(),
                noteId = noteId,
                isCompleted = false,
                priority = "MEDIUM",
                dueDate = null,
                pomodoroCount = 0,
                createdAt = now
            )
            if (id == 0L) repository.insertTask(task)
            else repository.updateTask(task)
        }
    }

    fun toggleComplete(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun incrementPomodoro(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(pomodoroCount = task.pomodoroCount + 1))
        }
    }
}
