package com.noteflow.app.features.timer.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.tasks.presentation.TaskViewModel
import com.noteflow.app.features.timer.presentation.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    timerViewModel: TimerViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val timeLeft by timerViewModel.timeLeft.collectAsState()
    val isRunning by timerViewModel.isRunning.collectAsState()
    val isWorkSession by timerViewModel.isWorkSession.collectAsState()
    val completedSessions by timerViewModel.completedSessions.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()

    val totalDuration = if (isWorkSession)
        TimerViewModel.WORK_DURATION else TimerViewModel.BREAK_DURATION
    val progress = timeLeft.toFloat() / totalDuration.toFloat()

    val minutes = (timeLeft / 1000) / 60
    val seconds = (timeLeft / 1000) % 60

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    var selectedTaskId by remember { mutableStateOf<Long?>(null) }
    var showTaskPicker by remember { mutableStateOf(false) }

    val selectedTaskName = tasks.find { it.id == selectedTaskId }?.title

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("التايمر") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isWorkSession) "وقت التركيز 🎯" else "وقت الراحة ☕",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // اختيار المهمة
            OutlinedButton(
                onClick = { showTaskPicker = true },
                enabled = !isRunning
            ) {
                Icon(Icons.Default.Link, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedTaskName ?: "اختار مهمة (اختياري)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(240.dp)) {
                    val strokeWidth = 16.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    val arcSize = Size(diameter, diameter)

                    drawArc(
                        color = surfaceVariant,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🍅 × $completedSessions",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = {
                    timerViewModel.reset()
                }) {
                    Text("إعادة")
                }
                Button(
                    onClick = {
                        if (isRunning) {
                            timerViewModel.pause()
                        } else {
                            timerViewModel.setTask(selectedTaskId)
                            timerViewModel.start()
                        }
                    },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(if (isRunning) "إيقاف" else "ابدأ")
                }
            }
        }
    }

    if (showTaskPicker) {
        AlertDialog(
            onDismissRequest = { showTaskPicker = false },
            title = { Text("اختار مهمة") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    item {
                        TextButton(
                            onClick = { selectedTaskId = null; showTaskPicker = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("بلاش مهمة") }
                    }
                    items(tasks.filter { !it.isCompleted }) { task ->
                        TextButton(
                            onClick = { selectedTaskId = task.id; showTaskPicker = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                task.title,
                                color = if (selectedTaskId == task.id)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
