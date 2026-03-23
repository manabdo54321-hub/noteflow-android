package com.noteflow.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.noteflow.app.features.notes.data.local.NoteDao
import com.noteflow.app.features.notes.data.local.NoteEntity
import com.noteflow.app.features.tasks.data.local.TaskDao
import com.noteflow.app.features.tasks.data.local.TaskEntity
import com.noteflow.app.features.timer.data.local.SessionDao
import com.noteflow.app.features.timer.data.local.SessionEntity
import com.noteflow.app.features.ai.data.local.AiChatDao
import com.noteflow.app.features.ai.data.local.AiChatEntity

@Database(
    entities = [NoteEntity::class, TaskEntity::class, SessionEntity::class, AiChatEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun sessionDao(): SessionDao
    abstract fun aiChatDao(): AiChatDao
}
