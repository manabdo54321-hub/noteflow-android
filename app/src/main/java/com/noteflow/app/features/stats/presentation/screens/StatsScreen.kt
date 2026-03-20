package com.noteflow.app.features.stats.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.presentation.NoteViewModel
import com.noteflow.app.features.tasks.presentation.TaskViewModel
import com.noteflow.app.features.timer.presentation.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    noteViewModel: NoteViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val notes by noteViewModel.notes.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()
    val totalSessions by timerViewModel.totalWorkSessions.collectAsState()

    val completedToday = tasks.count { task ->
        task.isCompleted && isToday(task.createdAt)
    }
    val totalCompleted = tasks.count { it.isCompleted }
    val totalHours = (totalSessions * 25) / 60f

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("الإحصائيات") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "نظرة عامة",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Edit,
                        value = "${notes.size}",
                        label = "ملاحظة",
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        value = "${tasks.size}",
                        label = "مهمة",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Timer,
                        value = "${totalSessions}",
                        label = "جلسة",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            item {
                Divider()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "التفاصيل",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                DetailRow(label = "مهام خلصت النهارده", value = "$completedToday")
            }
            item {
                DetailRow(label = "إجمالي المهام المكتملة", value = "$totalCompleted / ${tasks.size}")
            }
            item {
                DetailRow(label = "ساعات تركيز إجمالية", value = "${"%.1f".format(totalHours)} ساعة")
            }
            item {
                DetailRow(
                    label = "ملاحظات مترابطة",
                    value = "${notes.count { it.content.contains("[[") }}"
                )
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun isToday(timestamp: Long): Boolean {
    val now = System.currentTimeMillis()
    val startOfDay = now - (now % 86400000)
    return timestamp >= startOfDay
}
