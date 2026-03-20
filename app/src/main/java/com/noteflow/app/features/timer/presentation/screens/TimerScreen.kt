package com.noteflow.app.features.timer.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.noteflow.app.features.tasks.presentation.TaskViewModel
import com.noteflow.app.features.timer.presentation.TimerViewModel

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)

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

    var selectedTaskId by remember { mutableStateOf<Long?>(null) }
    var showTaskPicker by remember { mutableStateOf(false) }
    var distractionFree by remember { mutableStateOf(false) }

    val selectedTaskName = tasks.find { it.id == selectedTaskId }?.title
    val totalSessions = 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SurfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Remove, contentDescription = null,
                    tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
            }
            Text("NoteFlow", fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = Color.White)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SurfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = null,
                    tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // الدايرة
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(260.dp)) {
                val strokeWidth = 8.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(diameter, diameter)

                // الخلفية
                drawArc(
                    color = SurfaceHigh,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                // التقدم
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryColor, AccentColor)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isWorkSession) "جلسة تركيز" else "وقت راحة",
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // أزرار التحكم
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // رجوع
            IconButton(onClick = { timerViewModel.reset() }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = null,
                    tint = OnSurfaceVariant, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Play/Pause
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(PrimaryColor, AccentColor))
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {
                    if (isRunning) timerViewModel.pause()
                    else {
                        timerViewModel.setTask(selectedTaskId)
                        timerViewModel.start()
                    }
                }) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF1C0062),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // تقديم
            IconButton(onClick = { }) {
                Icon(Icons.Default.SkipNext, contentDescription = null,
                    tint = OnSurfaceVariant, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // مؤشر الجلسات
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSessions) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (index == completedSessions % totalSessions) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index < completedSessions % totalSessions) PrimaryColor
                            else if (index == completedSessions % totalSessions) AccentColor
                            else SurfaceHigh
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "الجلسة ${(completedSessions % totalSessions) + 1} من $totalSessions",
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            color = OnSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Distraction Free Mode
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceColor)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Vibration, contentDescription = null,
                    tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                Text("وضع التركيز الكامل", color = Color.White, fontSize = 14.sp)
            }
            Switch(
                checked = distractionFree,
                onCheckedChange = { distractionFree = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF1C0062),
                    checkedTrackColor = PrimaryColor
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // المهمة الحالية
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "المهمة الحالية",
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null,
                        tint = PrimaryColor, modifier = Modifier.size(20.dp))
                    Text(
                        text = selectedTaskName ?: "اختار مهمة...",
                        color = if (selectedTaskName != null) Color.White else OnSurfaceVariant,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = { showTaskPicker = true },
                    enabled = !isRunning
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null,
                        tint = OnSurfaceVariant)
                }
            }
        }
    }

    // Task Picker Dialog
    if (showTaskPicker) {
        AlertDialog(
            onDismissRequest = { showTaskPicker = false },
            title = { Text("اختار مهمة") },
            containerColor = SurfaceColor,
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    item {
                        TextButton(
                            onClick = { selectedTaskId = null; showTaskPicker = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("بلاش مهمة", color = OnSurfaceVariant) }
                    }
                    items(tasks.filter { !it.isCompleted }) { task ->
                        TextButton(
                            onClick = { selectedTaskId = task.id; showTaskPicker = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                task.title,
                                color = if (selectedTaskId == task.id) PrimaryColor
                                else Color.White
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
