package com.noteflow.app.features.timer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE taskId = :taskId ORDER BY startTime DESC")
    fun getSessionsByTask(taskId: Long): Flow<List<SessionEntity>>

    @Query("SELECT COUNT(*) FROM sessions WHERE isWorkSession = 1")
    fun getTotalWorkSessions(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Delete
    suspend fun deleteSession(session: SessionEntity)
}
