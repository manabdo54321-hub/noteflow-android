package com.noteflow.app.features.goals.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.goals.domain.model.Goal
import com.noteflow.app.features.goals.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val repository: GoalRepository
) : ViewModel() {

    val allGoals: StateFlow<List<Goal>> = repository
        .getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog

    private val _editingGoal = MutableStateFlow<Goal?>(null)
    val editingGoal: StateFlow<Goal?> = _editingGoal

    fun openAddDialog() { _showAddDialog.value = true }

    fun openEditDialog(goal: Goal) {
        _editingGoal.value = goal
        _showAddDialog.value = true
    }

    fun closeDialog() {
        _showAddDialog.value = false
        _editingGoal.value = null
    }

    fun saveGoal(title: String, description: String, targetDate: Long?) {
        viewModelScope.launch {
            val existing = _editingGoal.value
            if (existing != null) {
                repository.updateGoal(
                    existing.copy(
                        title = title,
                        description = description,
                        targetDate = targetDate
                    )
                )
            } else {
                repository.insertGoal(
                    Goal(
                        title = title,
                        description = description,
                        targetDate = targetDate
                    )
                )
            }
            closeDialog()
        }
    }

    fun updateProgress(goal: Goal, progress: Int) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(progress = progress.coerceIn(0, 100)))
        }
    }

    fun toggleComplete(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(isCompleted = !goal.isCompleted))
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }
}
