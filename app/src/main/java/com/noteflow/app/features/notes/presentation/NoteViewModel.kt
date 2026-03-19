package com.noteflow.app.features.notes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.notes.domain.usecase.GetNotesUseCase
import com.noteflow.app.features.notes.domain.usecase.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            getNotesUseCase().collect { _notes.value = it }
        }
    }

    fun saveNote(title: String, content: String, id: Long = 0) {
        viewModelScope.launch {
            val note = Note(id = id, title = title, content = content)
            saveNoteUseCase(note).onFailure { _error.value = it.message }
        }
    }

    fun clearError() { _error.value = null }
}
