package com.noteflow.app.features.goals.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val BgColor = Color(0xFF131313)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val TertiaryColor = Color(0xFF75D1FF)

@Composable
fun GoalsScreen(
    viewModel: GoalViewModel = hiltViewModel()
) {
    val goals by viewModel.allGoals.collectAsState()
    val showDialog by viewModel.showAddDialog.collectAsState()
    val editingGoal by viewModel.editingGoal.collectAsState()

    val activeGoals = goals.filter { !it.isCompleted }
    val completedGoals = goals.filter { it.isCompleted }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item { GoalsHeader() }
            item { GoalsTitle(activeGoals.size) }
            item { GoalsSummaryRow(goals) }

            if (activeGoals.isNotEmpty()) {
                item { GoalsSectionLabel("قيد التنفيذ", TertiaryColor) }
                items(activeGoals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onEdit = { viewModel.openEditDialog(goal) },
                        onDelete = { viewModel.deleteGoal(goal) },
                        onToggleComplete = { viewModel.toggleComplete(goal) },
                        onProgressChange = { viewModel.updateProgress(goal, it) }
                    )
                }
            }

            if (completedGoals.isNotEmpty()) {
                item { GoalsSectionLabel("مكتملة", Color(0xFF4CAF50)) }
                items(completedGoals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onEdit = { viewModel.openEditDialog(goal) },
                        onDelete = { viewModel.deleteGoal(goal) },
                        onToggleComplete = { viewModel.toggleComplete(goal) },
                        onProgressChange = { viewModel.updateProgress(goal, it) }
                    )
                }
            }

            if (goals.isEmpty()) {
                item { GoalsEmptyState() }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(PrimaryColor, AccentColor)))
                .clickableNoRipple { viewModel.openAddDialog() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = Color(0xFF1C0062),
                modifier = Modifier.size(28.dp)
            )
        }
    }

    if (showDialog) {
        GoalAddEditDialog(
            editingGoal = editingGoal,
            onConfirm = { title, desc, date -> viewModel.saveGoal(title, desc, date) },
            onDismiss = { viewModel.closeDialog() }
        )
    }
}
