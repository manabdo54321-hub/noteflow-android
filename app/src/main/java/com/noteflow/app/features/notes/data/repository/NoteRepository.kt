package com.noteflow.app.features.notes.data.repository

import com.noteflow.app.features.notes.data.local.NoteDao
import com.noteflow.app.features.notes.data.local.NoteEntity
import com.noteflow.app.features.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }

    fun searchNotes(query: String): Flow<List<Note>> =
        noteDao.searchNotes(query).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun getNoteById(id: Long): Note? =
        noteDao.getNoteById(id)?.toDomain()

    suspend fun saveNote(note: Note): Long =
        noteDao.insertNote(NoteEntity.fromDomain(note))

    suspend fun deleteNote(note: Note) =
        noteDao.deleteNote(NoteEntity.fromDomain(note))

    fun getBacklinks(noteTitle: String, noteId: Long): Flow<List<Note>> =
        noteDao.getBacklinks(noteTitle, noteId).map { entities ->
            entities.map { it.toDomain() }
        }
}
