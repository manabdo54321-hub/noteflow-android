package com.noteflow.app.features.tasks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noteflow.app.features.tasks.domain.model.Task
import com.noteflow.app.features.tasks.domain.model.TaskPriority

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val noteId: Long?,
    val isCompleted: Boolean,
    val priority: String,
    val dueDate: Long?,
    val pomodoroCount: Int,
    val createdAt: Long
) {
    fun toDomain() = Task(
        id = id,
        title = title,
        noteId = noteId,
        isCompleted = isCompleted,
        priority = TaskPriority.valueOf(priority),
        dueDate = dueDate,
        pomodoroCount = pomodoroCount,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(task: Task) = TaskEntity(
            id = task.id,
            title = task.title,
            noteId = task.noteId,
            isCompleted = task.isCompleted,
            priority = task.priority.name,
            dueDate = task.dueDate,
            pomodoroCount = task.pomodoroCount,
            createdAt = task.createdAt
        )
    }
}
