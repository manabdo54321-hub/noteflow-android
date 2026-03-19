package com.noteflow.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.noteflow.app.features.notes.data.local.NoteDao
import com.noteflow.app.features.notes.data.local.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
