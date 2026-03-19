package com.noteflow.app.features.notes.domain.usecase

import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.notes.data.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}
