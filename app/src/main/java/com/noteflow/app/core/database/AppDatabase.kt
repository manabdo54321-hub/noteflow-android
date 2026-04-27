package com.noteflow.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.noteflow.app.features.notes.data.local.NoteDao
import com.noteflow.app.features.notes.data.local.NoteEntity
import com.noteflow.app.features.tasks.data.local.TaskDao
import com.noteflow.app.features.tasks.data.local.TaskEntity
import com.noteflow.app.features.timer.data.local.SessionDao
import com.noteflow.app.features.timer.data.local.SessionEntity
import com.noteflow.app.features.ai.data.local.AiChatDao
import com.noteflow.app.features.ai.data.local.AiChatEntity
import com.noteflow.app.features.tags.data.local.TagEntity
import com.noteflow.app.features.tags.data.local.NoteTagCrossRef
import com.noteflow.app.features.tags.data.local.TaskTagCrossRef
import com.noteflow.app.features.tags.data.local.GoalTagCrossRef
import com.noteflow.app.features.tags.data.local.TagDao

@Database(
    entities = [
        NoteEntity::class,
        TaskEntity::class,
        SessionEntity::class,
        AiChatEntity::class,
        TagEntity::class,
        NoteTagCrossRef::class,
        TaskTagCrossRef::class,
        GoalTagCrossRef::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun sessionDao(): SessionDao
    abstract fun aiChatDao(): AiChatDao
    abstract fun tagDao(): TagDao

    companion object {
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS tags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        normalizedName TEXT NOT NULL,
                        color TEXT NOT NULL DEFAULT '#8A70FF',
                        usageCount INTEGER NOT NULL DEFAULT 0,
                        lastUsedAt INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_tags_normalizedName 
                    ON tags(normalizedName)
                """)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS note_tag_cross_ref (
                        noteId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        PRIMARY KEY(noteId, tagId)
                    )
                """)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS task_tag_cross_ref (
                        taskId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        PRIMARY KEY(taskId, tagId)
                    )
                """)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS goal_tag_cross_ref (
                        goalId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        PRIMARY KEY(goalId, tagId)
                    )
                """)
            }
        }
    }
}
