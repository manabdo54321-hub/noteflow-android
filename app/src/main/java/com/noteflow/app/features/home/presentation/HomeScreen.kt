package com.noteflow.app.features.home.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.presentation.NoteViewModel
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
private fun SimpleDivider(color: Color, verticalPadding: Int) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = verticalPadding.dp).height(1.dp).background(color))
}

@Composable
fun HomeScreen(
    onNoteClick: (Long) -> Unit,
    onAddNote: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    noteViewModel: NoteViewModel = hiltViewModel(),
    timerViewModel: TimerViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val timeLeft by timerViewModel.timeLeft.collectAsState()
    val isRunning by timerViewModel.isRunning.collectAsState()
    val isWorkSession by timerViewModel.isWorkSession.collectAsState()
    var quickNote by remember { mutableStateOf("") }
    var showLeftDrawer by remember { mutableStateOf(false) }
    var showRightDrawer by remember { mutableStateOf(false) }
    var isWriting by remember { mutableStateOf(false) }
    var isTaskFocused by remember { mutableStateOf(false) }

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "صباح الخير"
        in 12..17 -> "مساء النور"
        else -> "مساء الخير"
    }

    // Zen Mode animations
    val cardsAlpha by animateFloatAsState(
        targetValue = when {
            isWriting -> 0.15f
            isTaskFocused -> 1f
            else -> 1f
        },
        animationSpec = tween(400), label = "cardsAlpha"
    )
    val writeAreaWeight by animateFloatAsState(
        targetValue = when {
            isWriting -> 2f
            isTaskFocused -> 0.3f
            else -> 1f
        },
        animationSpec = tween(400), label = "writeWeight"
    )

    LaunchedEffect(quickNote) {
        if (quickNote.isNotBlank()) {
            kotlinx.coroutines.delay(2000)
            noteViewModel.triggerAutoSave(quickNote, "", 0)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            HomeTopBar(greeting, { showRightDrawer = true }, { showLeftDrawer = true }, onNavigateToStats)
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeQuickWrite(
                    quickNote = quickNote,
                    isWriting = isWriting,
                    writeAreaWeight = writeAreaWeight,
                    onQuickNoteChange = { quickNote = it },
                    onFocusChange = { isWriting = it; if (it) isTaskFocused = false }
                )
                Box(modifier = Modifier.graphicsLayer(alpha = cardsAlpha)) {
                    HomeCardsRow(
                        tasks = tasks,
                        timeLeft = timeLeft,
                        isRunning = isRunning,
                        isWorkSession = isWorkSession,
                        isTaskFocused = isTaskFocused,
                        onToggleTask = { taskViewModel.toggleComplete(it) },
                        onTimerToggle = { if (isRunning) timerViewModel.pause() else timerViewModel.start() },
                        onTaskFocusChange = { isTaskFocused = it; if (it) isWriting = false }
                    )
                }
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
        HomeBottomNav(Modifier.align(Alignment.BottomCenter), onAddNote, onNavigateToTasks, onNavigateToSearch, onNavigateToSettings)
        if (showLeftDrawer) {
            HomeLeftDrawer({ showLeftDrawer = false }, onNavigateToNotes, onNavigateToTasks, onNavigateToTimer, onNavigateToStats)
        }
        if (showRightDrawer) {
            HomeRightDrawer({ showRightDrawer = false }, onNavigateToSettings)
        }
    }
}

@Composable
private fun HomeTopBar(greeting: String, onShowRightDrawer: () -> Unit, onShowLeftDrawer: () -> Unit, onNavigateToStats: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF1C1B1B)).statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.clickable { onShowRightDrawer() }, horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Brush.linearGradient(listOf(PrimaryColor, AccentColor))), contentAlignment = Alignment.Center) {
                Text("م", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C0062))
            }
            Column {
                Text("WELCOME BACK", fontSize = 10.sp, letterSpacing = 2.sp, color = OnSurface.copy(alpha = 0.5f))
                Text(greeting, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateToStats) {
                Icon(Icons.Default.ShowChart, contentDescription = null, tint = OnSurface.copy(alpha = 0.6f), modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = onShowLeftDrawer) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = OnSurface.copy(alpha = 0.6f), modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun HomeQuickWrite(
    quickNote: String,
    isWriting: Boolean,
    writeAreaWeight: Float,
    onQuickNoteChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit
) {
    val minHeight by animateDpAsState(
        targetValue = if (isWriting) 320.dp else 220.dp,
        animationSpec = tween(400), label = "writeHeight"
    )
    Box(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = minHeight)
            .clip(RoundedCornerShape(16.dp)).background(SurfaceLowest).padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (!isWriting && quickNote.isEmpty()) {
                Text("✦", fontSize = 18.sp, color = PrimaryColor.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                Spacer(modifier = Modifier.height(8.dp))
            }
            BasicTextField(
                value = quickNote,
                onValueChange = onQuickNoteChange,
                textStyle = TextStyle(
                    color = OnSurface,
                    fontSize = if (isWriting) 18.sp else 16.sp,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.Right
                ),
                cursorBrush = SolidColor(PrimaryColor),
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = if (isWriting) 260.dp else 160.dp),
                decorationBox = { inner ->
                    if (quickNote.isEmpty()) {
                        Text(
                            if (isWriting) "اكتب أفكارك هنا..." else "ابدأ الكتابة...",
                            color = PrimaryColor.copy(alpha = 0.2f),
                            fontSize = if (isWriting) 18.sp else 16.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right
                        )
                    }
                    inner()
                }
            )
        }
        Row(modifier = Modifier.align(Alignment.TopStart), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!isWriting) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                    .background(PrimaryColor.copy(alpha = 0.1f))
                    .clickable { onFocusChange(true) },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(14.dp))
                }
            } else {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                    .background(PrimaryColor.copy(alpha = 0.15f))
                    .clickable { onFocusChange(false) },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(14.dp))
                }
            }
        }
        if (quickNote.isNotBlank()) {
            Text("يحفظ تلقائياً...", fontSize = 10.sp, letterSpacing = 1.sp, color = OutlineVariant,
                modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}

@Composable
private fun HomeCardsRow(
    tasks: List<Task>, timeLeft: Long, isRunning: Boolean, isWorkSession: Boolean,
    isTaskFocused: Boolean, onToggleTask: (Task) -> Unit,
    onTimerToggle: () -> Unit, onTaskFocusChange: (Boolean) -> Unit
) {
    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }
    val totalTasks = tasks.size
    val completionRate = if (totalTasks == 0) 0f else completedTasks.size.toFloat() / totalTasks.toFloat()
    val totalDuration = if (isWorkSession) TimerViewModel.WORK_DURATION else TimerViewModel.BREAK_DURATION
    val timerProgress = timeLeft.toFloat() / totalDuration.toFloat()
    val timerMinutes = (timeLeft / 1000) / 60
    val timerSeconds = (timeLeft / 1000) % 60

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val tasksWeight by animateFloatAsState(targetValue = if (isTaskFocused) 1.8f else 1f, animationSpec = tween(400), label = "tasksWeight")
        val timerWeight by animateFloatAsState(targetValue = if (isTaskFocused) 0.6f else 1f, animationSpec = tween(400), label = "timerWeight")
        HomeTasksCard(Modifier.weight(tasksWeight), activeTasks, completedTasks, totalTasks, completionRate, isTaskFocused, onToggleTask, onTaskFocusChange)
        HomeTimerCard(Modifier.weight(timerWeight), timerMinutes, timerSeconds, timerProgress, isRunning, onTimerToggle)
    }
}

@Composable
private fun HomeTasksCard(
    modifier: Modifier, activeTasks: List<Task>, completedTasks: List<Task>,
    totalTasks: Int, completionRate: Float, isTaskFocused: Boolean,
    onToggleTask: (Task) -> Unit, onTaskFocusChange: (Boolean) -> Unit
) {
    Column(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(SurfaceColor).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("اليوم", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("${completedTasks.size} من $totalTasks", fontSize = 10.sp, color = PrimaryColor, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(40.dp).height(3.dp).clip(RoundedCornerShape(2.dp)).background(SurfaceHigh)) {
                        Box(modifier = Modifier.fillMaxWidth(completionRate).height(3.dp).clip(RoundedCornerShape(2.dp)).background(Brush.horizontalGradient(listOf(PrimaryColor, AccentColor))))
                    }
                }
                Box(modifier = Modifier.size(24.dp).clip(CircleShape)
                    .background(if (isTaskFocused) PrimaryColor.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onTaskFocusChange(!isTaskFocused) },
                    contentAlignment = Alignment.Center) {
                    Icon(if (isTaskFocused) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                        contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(14.dp))
                }
            }
        }
        val displayTasks = (activeTasks.take(if (isTaskFocused) 4 else 2) + completedTasks.take(if (isTaskFocused) 3 else 2)).take(if (isTaskFocused) 6 else 4)
        if (displayTasks.isEmpty()) {
            Text("لا توجد مهام ✨", color = OnSurfaceVariant, fontSize = 13.sp)
        } else {
            displayTasks.forEach { task -> AnimatedTaskRow(task = task, onToggle = { onToggleTask(task) }) }
        }
    }
}

@Composable
private fun AnimatedTaskRow(task: Task, onToggle: () -> Unit) {
    val strikeWidth by animateFloatAsState(
        targetValue = if (task.isCompleted) 1f else 0f,
        animationSpec = tween(300), label = "strike"
    )
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp))
            .background(if (task.isCompleted) PrimaryColor else Color.Transparent)
            .border(1.dp, if (task.isCompleted) Color.Transparent else OutlineVariant, RoundedCornerShape(4.dp))
            .clickable { onToggle() }, contentAlignment = Alignment.Center) {
            if (task.isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1C0062), modifier = Modifier.size(13.dp))
            }
        }
        Text(
            text = task.title, fontSize = 14.sp,
            color = if (task.isCompleted) OnSurface.copy(alpha = 0.4f) else OnSurfaceVariant,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f), maxLines = 1
        )
    }
}

@Composable
private fun HomeTimerCard(modifier: Modifier, timerMinutes: Long, timerSeconds: Long, timerProgress: Float, isRunning: Boolean, onTimerToggle: () -> Unit) {
    Column(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(SurfaceColor).padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("تايمر", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
        Text("%02d:%02d".format(timerMinutes, timerSeconds), fontSize = 11.sp, color = OutlineVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF002331)).clickable { onTimerToggle() }.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = TertiaryColor, modifier = Modifier.size(14.dp))
            }
            Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 4.dp.toPx()
                    val radius = size.minDimension / 2 - strokeWidth
                    drawArc(color = SurfaceHigh, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth, strokeWidth), size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2))
                    drawArc(color = TertiaryColor, startAngle = -90f, sweepAngle = 360f * timerProgress, useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth, strokeWidth), size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2))
                }
                Text("%02d:%02d".format(timerMinutes, timerSeconds), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            }
        }
    }
}

@Composable
private fun HomeBottomNav(modifier: Modifier, onAddNote: () -> Unit, onNavigateToTasks: () -> Unit, onNavigateToSearch: () -> Unit, onNavigateToSettings: () -> Unit) {
    var fabPressed by remember { mutableStateOf(false) }
    val fabScale by animateFloatAsState(
        targetValue = if (fabPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "fab"
    )
    Box(modifier = modifier.padding(bottom = 32.dp)) {
        Row(modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(Color(0xFF201F1F).copy(alpha = 0.95f)).padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(52.dp).scale(fabScale).clip(CircleShape)
                .background(Brush.linearGradient(listOf(PrimaryColor, AccentColor)))
                .clickable { fabPressed = true; onAddNote() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFF131313), modifier = Modifier.size(26.dp))
            }
            LaunchedEffect(fabPressed) { if (fabPressed) { kotlinx.coroutines.delay(150); fabPressed = false } }
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).clickable { onNavigateToTasks() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(26.dp))
            }
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).clickable { onNavigateToSearch() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Search, contentDescription = null, tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(26.dp))
            }
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).clickable { onNavigateToSettings() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(26.dp))
            }
        }
    }
}

@Composable
private fun HomeLeftDrawer(onClose: () -> Unit, onNavigateToNotes: () -> Unit, onNavigateToTasks: () -> Unit, onNavigateToTimer: () -> Unit, onNavigateToStats: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).clickable { onClose() })
    Column(modifier = Modifier.fillMaxHeight().width(280.dp).background(Color(0xFF1C1B1B)).statusBarsPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("الأدوات", fontSize = 11.sp, letterSpacing = 2.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        DrawerItem(Icons.Default.Home, "الرئيسية") { onClose() }
        DrawerItem(Icons.Default.Notes, "الملاحظات") { onClose(); onNavigateToNotes() }
        DrawerItem(Icons.Default.CheckCircle, "المهام") { onClose(); onNavigateToTasks() }
        DrawerItem(Icons.Default.Timer, "التايمر") { onClose(); onNavigateToTimer() }
        DrawerItem(Icons.Default.BarChart, "الإحصائيات") { onClose(); onNavigateToStats() }
        SimpleDivider(color = OutlineVariant.copy(alpha = 0.3f), verticalPadding = 8)
        Text("إضافات", fontSize = 11.sp, letterSpacing = 2.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        DrawerItem(Icons.Default.AccountTree, "خريطة الروابط") { onClose() }
        DrawerItem(Icons.Default.Tag, "الوسوم") { onClose() }
        DrawerItem(Icons.Default.Archive, "الأرشيف") { onClose() }
        DrawerItem(Icons.Default.FileDownload, "تصدير") { onClose() }
    }
}

@Composable
private fun HomeRightDrawer(onClose: () -> Unit, onNavigateToSettings: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).clickable { onClose() })
    Column(modifier = Modifier.fillMaxHeight().width(280.dp).background(Color(0xFF1C1B1B)).statusBarsPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Brush.linearGradient(listOf(PrimaryColor, AccentColor))), contentAlignment = Alignment.Center) {
                Text("م", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C0062))
            }
            Column {
                Text("مستخدم NoteFlow", fontWeight = FontWeight.Bold, color = OnSurface, fontSize = 15.sp)
                Text("noteflow@app.io", fontSize = 12.sp, color = OnSurfaceVariant)
            }
        }
        SimpleDivider(color = OutlineVariant.copy(alpha = 0.3f), verticalPadding = 4)
        Text("الإعدادات", fontSize = 11.sp, letterSpacing = 2.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
        DrawerItem(Icons.Default.Settings, "إعدادات التطبيق") { onClose(); onNavigateToSettings() }
        DrawerItem(Icons.Default.Palette, "المظهر والألوان") { onClose() }
        DrawerItem(Icons.Default.Notifications, "الإشعارات") { onClose() }
        DrawerItem(Icons.Default.Security, "الأمان") { onClose() }
        DrawerItem(Icons.Default.Sync, "المزامنة") { onClose() }
        SimpleDivider(color = OutlineVariant.copy(alpha = 0.3f), verticalPadding = 4)
        DrawerItem(Icons.Default.Logout, "تسجيل الخروج", tint = Color(0xFFFF6B6B)) { onClose() }
    }
}

@Composable
private fun DrawerItem(icon: ImageVector, label: String, tint: Color = OnSurfaceVariant, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable { onClick() }.padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Text(label, color = if (tint == OnSurfaceVariant) OnSurface else tint, fontSize = 15.sp)
    }
}
