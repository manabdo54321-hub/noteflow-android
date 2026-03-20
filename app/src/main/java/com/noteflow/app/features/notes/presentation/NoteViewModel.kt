package com.noteflow.app.features.notes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.notes.data.repository.NoteRepository
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.notes.domain.usecase.GetNotesUseCase
import com.noteflow.app.features.notes.domain.usecase.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class NoteViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val repository: NoteRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _backlinks = MutableStateFlow<List<Note>>(emptyList())
    val backlinks: StateFlow<List<Note>> = _backlinks.asStateFlow()

    // Auto-save state
    private val _autoSaveContent = MutableStateFlow<Triple<String, String, Long>?>(null)

    init {
        loadNotes()
        setupAutoSave()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            getNotesUseCase().collect { _notes.value = it }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupAutoSave() {
        viewModelScope.launch {
            _autoSaveContent
                .drop(1)
                .debounce(2000)
                .collect { triple ->
                    triple?.let { (title, content, id) ->
                        if (title.isNotBlank()) {
                            val note = Note(id = id, title = title, content = content)
                            saveNoteUseCase(note)
                        }
                    }
                }
        }
    }

    fun triggerAutoSave(title: String, content: String, id: Long) {
        _autoSaveContent.value = Triple(title, content, id)
    }

    fun loadBacklinks(noteTitle: String, noteId: Long) {
        viewModelScope.launch {
            repository.getBacklinks(noteTitle, noteId).collect {
                _backlinks.value = it
            }
        }
    }

    fun saveNote(title: String, content: String, id: Long = 0) {
        viewModelScope.launch {
            val note = Note(id = id, title = title, content = content)
            saveNoteUseCase(note).onFailure { _error.value = it.message }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun clearError() { _error.value = null }
}
