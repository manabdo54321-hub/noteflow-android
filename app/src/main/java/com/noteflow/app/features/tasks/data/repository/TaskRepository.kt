package com.noteflow.app.features.tasks.data.repository

import com.noteflow.app.features.tasks.data.local.TaskDao
import com.noteflow.app.features.tasks.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<TaskEntity>> =
        taskDao.getAllTasks()

    fun getActiveTasks(): Flow<List<TaskEntity>> =
        taskDao.getActiveTasks()

    suspend fun getTaskById(id: Long): TaskEntity? =
        taskDao.getTaskById(id)

    suspend fun insertTask(task: TaskEntity): Long =
        taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) =
        taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) =
        taskDao.deleteTask(task)

    fun searchTasks(query: String): Flow<List<TaskEntity>> =
        taskDao.searchTasks(query)
}
