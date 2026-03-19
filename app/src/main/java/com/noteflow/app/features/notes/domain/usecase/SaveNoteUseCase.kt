package com.noteflow.app.features.notes.domain.usecase

import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.notes.data.repository.NoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Result<Long> {
        if (note.title.isBlank()) {
            return Result.failure(Exception("العنوان مش ممكن يكون فاضي"))
        }
        return Result.success(repository.saveNote(note))
    }
}
