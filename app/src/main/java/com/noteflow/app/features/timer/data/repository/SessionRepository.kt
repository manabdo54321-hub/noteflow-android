package com.noteflow.app.features.timer.data.repository

import com.noteflow.app.features.timer.data.local.SessionDao
import com.noteflow.app.features.timer.data.local.SessionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(private val sessionDao: SessionDao) {

    fun getAllSessions(): Flow<List<SessionEntity>> =
        sessionDao.getAllSessions()

    fun getSessionsByTask(taskId: Long): Flow<List<SessionEntity>> =
        sessionDao.getSessionsByTask(taskId)

    fun getTotalWorkSessions(): Flow<Int> =
        sessionDao.getTotalWorkSessions()

    suspend fun saveSession(session: SessionEntity): Long =
        sessionDao.insertSession(session)

    suspend fun deleteSession(session: SessionEntity) =
        sessionDao.deleteSession(session)
}
