package com.noteflow.app.features.search.domain.usecase

import com.noteflow.app.features.notes.data.repository.NoteRepository
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import com.noteflow.app.features.tasks.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class SearchResults(
    val notes: List<Note> = emptyList(),
    val tasks: List<Task> = emptyList()
)

class SearchUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) {
    fun search(query: String): Flow<SearchResults> {
        if (query.isBlank()) {
            return combine(
                noteRepository.getAllNotes(),
                taskRepository.getAllTasks()
            ) { notes, tasks -> SearchResults(notes, tasks) }
        }
        return combine(
            noteRepository.searchNotes(query),
            taskRepository.searchTasks(query)
        ) { notes, tasks -> SearchResults(notes, tasks) }
    }
}
