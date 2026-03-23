package com.noteflow.app.features.notes.data.repository

import com.noteflow.app.features.notes.data.local.NoteDao
import com.noteflow.app.features.notes.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<NoteEntity>> =
        noteDao.getAllNotes()

    suspend fun getNoteById(id: Long): NoteEntity? =
        noteDao.getNoteById(id)

    suspend fun insertNote(note: NoteEntity): Long =
        noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) =
        noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) =
        noteDao.deleteNote(note)

    fun getNotesByIds(ids: List<Long>): Flow<List<NoteEntity>> =
        noteDao.getNotesByIds(ids)

    fun searchNotes(query: String): Flow<List<NoteEntity>> =
        noteDao.searchNotes(query)
}
