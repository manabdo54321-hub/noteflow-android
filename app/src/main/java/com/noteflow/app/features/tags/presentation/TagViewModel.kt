package com.noteflow.app.features.tags.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.tags.domain.model.Tag
import com.noteflow.app.features.tags.domain.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())
    val allTags: StateFlow<List<Tag>> = _allTags

    private val _suggestions = MutableStateFlow<List<Tag>>(emptyList())
    val suggestions: StateFlow<List<Tag>> = _suggestions

    private val _selectedTagId = MutableStateFlow<Long?>(null)
    val selectedTagId: StateFlow<Long?> = _selectedTagId

    private val _taskIdsByTag = MutableStateFlow<List<Long>>(emptyList())
    val taskIdsByTag: StateFlow<List<Long>> = _taskIdsByTag

    init {
        viewModelScope.launch {
            tagRepository.getAllTags().collectLatest {
                _allTags.value = it
            }
        }
        viewModelScope.launch {
            _selectedTagId.collectLatest { tagId ->
                if (tagId == null) {
                    _taskIdsByTag.value = emptyList()
                } else {
                    tagRepository.getTaskIdsByTag(tagId).collectLatest {
                        _taskIdsByTag.value = it
                    }
                }
            }
        }
    }

    fun onSuggestionQuery(prefix: String) {
        viewModelScope.launch {
            if (prefix.isEmpty()) {
                tagRepository.getMostUsedTags(10).collectLatest {
                    _suggestions.value = it
                }
            } else {
                tagRepository.getSuggestedTags(prefix).collectLatest {
                    _suggestions.value = it
                }
            }
        }
    }

    fun selectTag(tagId: Long?) {
        _selectedTagId.value = tagId
    }

    fun renameTag(tagId: Long, newName: String) {
        viewModelScope.launch {
            tagRepository.renameTag(tagId, newName)
        }
    }

    fun mergeTags(sourceId: Long, targetId: Long) {
        viewModelScope.launch {
            tagRepository.mergeTags(sourceId, targetId)
        }
    }

    fun deleteTag(tagId: Long) {
        viewModelScope.launch {
            tagRepository.deleteTag(tagId)
        }
    }

    fun updateTagColor(tagId: Long, color: String) {
        viewModelScope.launch {
            tagRepository.updateTagColor(tagId, color)
        }
    }
}
