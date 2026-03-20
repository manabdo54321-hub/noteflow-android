package com.noteflow.app.core.di

import android.content.Context
import androidx.room.Room
import com.noteflow.app.core.database.AppDatabase
import com.noteflow.app.features.notes.data.local.NoteDao
import com.noteflow.app.features.notes.data.repository.NoteRepository
import com.noteflow.app.features.tasks.data.local.TaskDao
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import com.noteflow.app.features.timer.data.local.SessionDao
import com.noteflow.app.features.timer.data.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "noteflow_database"
    ).fallbackToDestructiveMigration()
     .build()

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao =
        database.noteDao()

    @Provides
    @Singleton
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository =
        NoteRepository(noteDao)

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao =
        database.taskDao()

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository =
        TaskRepository(taskDao)

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase): SessionDao =
        database.sessionDao()

    @Provides
    @Singleton
    fun provideSessionRepository(sessionDao: SessionDao): SessionRepository =
        SessionRepository(sessionDao)
}
