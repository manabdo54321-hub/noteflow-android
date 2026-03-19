package com.noteflow.app.features.timer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long?,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val isWorkSession: Boolean
)
