package com.noteflow.app.features.home.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.notes.presentation.NoteViewModel
import com.noteflow.app.features.stats.presentation.StatsViewModel
import com.noteflow.app.features.tasks.domain.model.Task
import com.noteflow.app.features.tasks.presentation.TaskViewModel
import com.noteflow.app.features.timer.presentation.TimerViewModel
import java.util.Calendar

private val BgColor = Color(0xFF131313)
private val SurfaceLowest = Color(0xFF0E0E0E)
private val SurfaceColor = Color(0xFF201F1F)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val TertiaryColor = Color(0xFF75D1FF)
private val OnSurface = Color(0xFFE5E2E1)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val OutlineVariant = Color(0xFF47464C)

@Composable
fun HomeScreen(
    onNoteClick: (Long) -> Unit,
    onAddNote: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToTasks: () -> Unit,
    noteViewModel: NoteViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel(),
    timerViewModel: TimerViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val notes by noteViewModel.notes.collectAsState()
    val allTasks by statsViewModel.allTasks.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()
    val timeLeft by timerViewModel.timeLeft.collectAsState()
    val isRunning by timerViewModel.isRunning.collectAsState()
    val isWorkSession by timerViewModel.isWorkSession.collectAsState()
    val completedSessions by timerViewModel.completedSessions.collectAsState()

    var quickNote by remember { mutableStateOf("") }
    var isWriting by remember { mutableStateOf(false) }

    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }
    val totalTasks = activeTasks.size + completedTasks.size
    val completionRate = if (totalTasks == 0) 0f
        else completedTasks.size.toFloat() / totalTasks.toFloat()

    val totalDuration = if (isWorkSession)
        TimerViewModel.WORK_DURATION else TimerViewModel.BREAK_DURATION
    val timerProgress = timeLeft.toFloat() / totalDuration.toFloat()
    val timerMinutes = (timeLeft / 1000) / 60
    val timerSeconds = (timeLeft / 1000) % 60

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "صباح الخير"
        in 12..17 -> "مساء النور"
        else -> "مساء الخير"
    }

    // Auto-save quick note
    LaunchedEffect(quickNote) {
        if (quickNote.isNotBlank()) {
            kotlinx.coroutines.delay(2000)
            noteViewModel.triggerAutoSave(quickNote, "", 0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // TopAppBar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1B1B))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // الصورة الشخصية + الاسم
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(PrimaryColor, AccentColor))
                            )
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("م", fontSize = 14.sp,
                            fontWeight = FontWeight.Bold, color = Color(0xFF1C0062))
                    }
                    Column {
                        Text("مرحباً بعودتك",
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            color = OnSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = greeting,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }
                }

                // الأزرار العلوية
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Insights, contentDescription = null,
                            tint = OnSurface.copy(alpha = 0.6f))
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.CenterFocusWeak, contentDescription = null,
                            tint = OnSurface.copy(alpha = 0.6f))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quick Write Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLowest)
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (!isWriting && quickNote.isEmpty()) {
                            Text(
                                text = "ابدأ الكتابة...",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                modifier = Modifier.clickable { isWriting = true }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "أفكارك تنتظر...",
                                fontSize = 16.sp,
                                color = OnSurfaceVariant,
                                modifier = Modifier.clickable { isWriting = true }
                            )
                        } else {
                            Text(
                                text = "ابدأ الكتابة...",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            BasicTextField(
                                value = quickNote,
                                onValueChange = { quickNote = it; isWriting = true },
                                textStyle = TextStyle(
                                    color = OnSurfaceVariant,
                                    fontSize = 16.sp,
                                    lineHeight = 26.sp
                                ),
                                cursorBrush = SolidColor(PrimaryColor),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { inner ->
                                    if (quickNote.isEmpty()) {
                                        Text("أفكارك تنتظر...",
                                            color = OnSurfaceVariant, fontSize = 16.sp)
                                    }
                                    inner()
                                }
                            )
                        }
                    }

                    // Auto-saving indicator
                    if (quickNote.isNotBlank()) {
                        Text(
                            text = "يحفظ تلقائياً...",
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = OnSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }

                // Today's Tasks + Focus Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Today's Tasks
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceColor)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("اليوم", fontSize = 16.sp,
                                fontWeight = FontWeight.Bold, color = OnSurface)
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${completedTasks.size} من ${totalTasks} مكتملة",
                                    fontSize = 10.sp,
                                    color = PrimaryColor,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(SurfaceHigh)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(completionRate)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(PrimaryColor, AccentColor)
                                                )
                                            )
                                    )
                                }
                            }
                        }

                        // Tasks List
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            val displayTasks = (activeTasks.take(2) + completedTasks.take(2))
                                .take(4)
                            displayTasks.forEach { task ->
                                TaskRow(
                                    task = task,
                                    onToggle = { taskViewModel.toggleComplete(task) }
                                )
                            }
                            if (displayTasks.isEmpty()) {
                                Text("لا توجد مهام اليوم ✨",
                                    color = OnSurfaceVariant, fontSize = 13.sp)
                            }
                        }
                    }

                    // Focus Timer Widget
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceColor)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("تايمر التركيز", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = OnSurface)
                        Text(
                            text = "جلسة بومودورو: ${"%02d:%02d".format(timerMinutes, timerSeconds)}",
                            fontSize = 11.sp, color = OutlineVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Timer Circle
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            // Background glow
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .blur(40.dp)
                                    .background(TertiaryColor.copy(alpha = 0.1f), CircleShape)
                            )

                            androidx.compose.foundation.Canvas(
                                modifier = Modifier.size(96.dp)
                            ) {
                                val strokeWidth = 4.dp.toPx()
                                val radius = size.minDimension / 2 - strokeWidth

                                drawArc(
                                    color = SurfaceHigh,
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = strokeWidth,
                                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                                    ),
                                    topLeft = androidx.compose.ui.geometry.Offset(
                                        strokeWidth, strokeWidth),
                                    size = androidx.compose.ui.geometry.Size(
                                        radius * 2, radius * 2)
                                )
                                drawArc(
                                    color = TertiaryColor,
                                    startAngle = -90f,
                                    sweepAngle = 360f * timerProgress,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = strokeWidth,
                                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                                    ),
                                    topLeft = androidx.compose.ui.geometry.Offset(
                                        strokeWidth, strokeWidth),
                                    size = androidx.compose.ui.geometry.Size(
                                        radius * 2, radius * 2)
                                )
                            }

                            Text(
                                text = "%02d:%02d".format(timerMinutes, timerSeconds),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        }

                        // Start Button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF002331))
                                .clickable {
                                    if (isRunning) timerViewModel.pause()
                                    else timerViewModel.start()
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = TertiaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                if (isRunning) "إيقاف" else "ابدأ",
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = TertiaryColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Bottom Navigation — Floating
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFF201F1F).copy(alpha = 0.9f))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // القلم البنفسجي — Active
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(PrimaryColor, AccentColor))
                        )
                        .clickable { onAddNote() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null,
                        tint = Color(0xFF131313), modifier = Modifier.size(24.dp))
                }

                // البرق
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onNavigateToTasks() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null,
                        tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
                }

                // العدسة
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
                }

                // الترس
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null,
                        tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: Task, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (task.isCompleted) PrimaryColor else Color.Transparent
                )
                .border(
                    1.dp,
                    if (task.isCompleted) Color.Transparent else OutlineVariant,
                    RoundedCornerShape(4.dp)
                )
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null,
                    tint = Color(0xFF1C0062), modifier = Modifier.size(14.dp))
            }
        }
        Text(
            text = task.title,
            fontSize = 14.sp,
            color = if (task.isCompleted) OnSurface.copy(alpha = 0.4f) else OnSurfaceVariant,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f)
        )
    }
}
