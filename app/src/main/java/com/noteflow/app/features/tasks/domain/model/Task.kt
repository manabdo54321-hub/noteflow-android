package com.noteflow.app.features.tasks.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val noteId: Long? = null,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val pomodoroCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskPriority { LOW, MEDIUM, HIGH }
