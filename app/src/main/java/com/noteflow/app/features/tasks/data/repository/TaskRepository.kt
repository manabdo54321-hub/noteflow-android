package com.noteflow.app.features.tasks.data.repository

import com.noteflow.app.features.tasks.data.local.TaskDao
import com.noteflow.app.features.tasks.data.local.TaskEntity
import com.noteflow.app.features.tasks.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { list -> list.map { it.toDomain() } }

    fun getActiveTasks(): Flow<List<Task>> =
        taskDao.getActiveTasks().map { list -> list.map { it.toDomain() } }

    fun getTasksByNote(noteId: Long): Flow<List<Task>> =
        taskDao.getTasksByNote(noteId).map { list -> list.map { it.toDomain() } }

    suspend fun saveTask(task: Task): Long =
        taskDao.insertTask(TaskEntity.fromDomain(task))

    suspend fun updateTask(task: Task) =
        taskDao.updateTask(TaskEntity.fromDomain(task))

    suspend fun deleteTask(task: Task) =
        taskDao.deleteTask(TaskEntity.fromDomain(task))

    suspend fun setCompleted(id: Long, done: Boolean) =
        taskDao.setCompleted(id, done)

    suspend fun incrementPomodoro(id: Long) =
        taskDao.incrementPomodoro(id)
}
