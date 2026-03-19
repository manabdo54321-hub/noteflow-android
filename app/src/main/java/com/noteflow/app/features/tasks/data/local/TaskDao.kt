package com.noteflow.app.features.tasks.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE noteId = :noteId ORDER BY createdAt DESC")
    fun getTasksByNote(noteId: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :done WHERE id = :id")
    suspend fun setCompleted(id: Long, done: Boolean)

    @Query("UPDATE tasks SET pomodoroCount = pomodoroCount + 1 WHERE id = :id")
    suspend fun incrementPomodoro(id: Long)
}
