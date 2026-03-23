package com.noteflow.app.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.notes.data.local.NoteEntity
import com.noteflow.app.features.notes.data.repository.NoteRepository
import com.noteflow.app.features.tasks.data.local.TaskEntity
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _activeFilter = MutableStateFlow("all")
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    val noteResults: StateFlow<List<NoteEntity>> = _query
        .debounce(300)
        .filter { it.length >= 2 }
        .flatMapLatest { q ->
            if (q.isEmpty()) flowOf(emptyList())
            else noteRepository.searchNotes(q)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val taskResults: StateFlow<List<TaskEntity>> = _query
        .debounce(300)
        .filter { it.length >= 2 }
        .flatMapLatest { q ->
            if (q.isEmpty()) flowOf(emptyList())
            else taskRepository.searchTasks(q)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onFilterChange(filter: String) {
        _activeFilter.value = filter
    }

    fun clearQuery() {
        _query.value = ""
    }
}
