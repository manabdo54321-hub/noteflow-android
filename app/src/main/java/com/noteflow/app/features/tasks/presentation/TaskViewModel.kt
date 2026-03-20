package com.noteflow.app.features.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import com.noteflow.app.features.tasks.domain.model.Task
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

    val tasks: StateFlow<List<Task>> = repository.getActiveTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveTask(title: String, noteId: Long? = null) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.saveTask(Task(title = title.trim(), noteId = noteId))
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            repository.setCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun incrementPomodoro(taskId: Long) {
        viewModelScope.launch {
            repository.incrementPomodoro(taskId)
        }
    }
}
