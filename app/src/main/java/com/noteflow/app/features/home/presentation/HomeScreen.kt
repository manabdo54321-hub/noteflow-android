package com.noteflow.app.features.home.presentation

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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

    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }
    val totalTasks = tasks.size
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

    LaunchedEffect(quickNote) {
        if (quickNote.isNotBlank()) {
            kotlinx.coroutines.delay(2000)
            noteViewModel.triggerAutoSave(quickNote, "", 0)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {

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
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { showRightDrawer = true },
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(PrimaryColor, AccentColor))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("م", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = Color(0xFF1C0062))
                    }
                    Column {
                        Text("WELCOME BACK", fontSize = 10.sp,
                            letterSpacing = 2.sp, color = OnSurface.copy(alpha = 0.5f))
                        Text(greeting, fontSize = 20.sp,
                            fontWeight = FontWeight.Bold, color = PrimaryColor)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onNavigateToStats() }) {
                        Icon(Icons.Default.ShowChart, contentDescription = null,
                            tint = OnSurface.copy(alpha = 0.6f), modifier = Modifier.size(22.dp))
                    }
                    IconButton(onClick = { showLeftDrawer = true }) {
                        Icon(Icons.Default.Menu, contentDescription = null,
                            tint = OnSurface.copy(alpha = 0.6f), modifier = Modifier.size(22.dp))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quick Write
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLowest)
                        .clickable { }
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("ابدأ الكتابة...", fontSize = 28.sp,
                            fontWeight = FontWeight.Bold, color = OnSurface)
                        Spacer(modifier = Modifier.height(12.dp))
                        BasicTextField(
                            value = quickNote,
                            onValueChange = { quickNote = it },
                            textStyle = TextStyle(
                                color = OnSurfaceVariant,
                                fontSize = 16.sp,
                                lineHeight = 26.sp
                            ),
                            cursorBrush = SolidColor(PrimaryColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 120.dp),
                            decorationBox = { inner ->
                                if (quickNote.isEmpty()) {
                                    Text("فكّر في مشروعك القادم...",
                                        color = OnSurfaceVariant.copy(alpha = 0.4f),
                                        fontSize = 16.sp)
                                }
                                inner()
                            }
                        )
                    }
                    if (quickNote.isNotBlank()) {
                        Text("يحفظ تلقائياً...", fontSize = 10.sp,
                            letterSpacing = 1.sp, color = OutlineVariant,
                            modifier = Modifier.align(Alignment.TopEnd))
                    }
                }

                // Tasks + Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tasks
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceColor)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("اليوم", fontSize = 16.sp,
                                fontWeight = FontWeight.Bold, color = OnSurface)
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${completedTasks.size} من $totalTasks",
                                    fontSize = 10.sp, color = PrimaryColor, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(40.dp).height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(SurfaceHigh)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(completionRate).height(3.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Brush.horizontalGradient(
                                                listOf(PrimaryColor, AccentColor)))
                                    )
                                }
                            }
                        }

                        val displayTasks = (activeTasks.take(2) + completedTasks.take(2)).take(4)
                        if (displayTasks.isEmpty()) {
                            Text("لا توجد مهام ✨", color = OnSurfaceVariant, fontSize = 13.sp)
                        } else {
                            displayTasks.forEach { task ->
                                TaskRow(task = task,
                                    onToggle = { taskViewModel.toggleComplete(task) })
                            }
                        }
                    }

                    // Timer
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceColor)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("تايمر التركيز", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("جلسة: %02d:%02d".format(timerMinutes, timerSeconds),
                            fontSize = 11.sp, color = OutlineVariant)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF002331))
                                    .clickable {
                                        if (isRunning) timerViewModel.pause()
                                        else timerViewModel.start()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (isRunning) Icons.Default.Pause
                                    else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = TertiaryColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    if (isRunning) "إيقاف" else "ابدأ",
                                    fontSize = 11.sp, color = TertiaryColor, letterSpacing = 1.sp
                                )
                            }

                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.foundation.Canvas(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    val strokeWidth = 4.dp.toPx()
                                    val radius = size.minDimension / 2 - strokeWidth
                                    drawArc(
                                        color = SurfaceHigh,
                                        startAngle = -90f, sweepAngle = 360f,
                                        useCenter = false,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = strokeWidth,
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round),
                                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth, strokeWidth),
                                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                                    )
                                    drawArc(
                                        color = TertiaryColor,
                                        startAngle = -90f,
                                        sweepAngle = 360f * timerProgress,
                                        useCenter = false,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = strokeWidth,
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round),
                                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth, strokeWidth),
                                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                                    )
                                }
                                Text("%02d:%02d".format(timerMinutes, timerSeconds),
                                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }

        // Bottom Navigation
        Box(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFF201F1F).copy(alpha = 0.95f))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(PrimaryColor, AccentColor)))
                        .clickable { onAddNote() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null,
                        tint = Color(0xFF131313), modifier = Modifier.size(26.dp))
                }
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape)
                        .clickable { onNavigateToTasks() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null,
                        tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(26.dp))
                }
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape)
                        .clickable { onNavigateToSearch() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(26.dp))
                }
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape)
                        .clickable { onNavigateToSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null,
                        tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(26.dp))
                }
            }
        }

        // Left Drawer
        if (showLeftDrawer) {
            Box(modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { showLeftDrawer = false })
            Column(
                modifier = Modifier
                    .fillMaxHeight().width(280.dp)
                    .align(Alignment.CenterStart)
                    .background(Color(0xFF1C1B1B))
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("الأدوات", fontSize = 11.sp, letterSpacing = 2.sp,
                    color = PrimaryColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                DrawerItem(Icons.Default.Home, "الرئيسية") { showLeftDrawer = false }
                DrawerItem(Icons.Default.Notes, "الملاحظات") { showLeftDrawer = false; onNavigateToNotes() }
                DrawerItem(Icons.Default.CheckCircle, "المهام") { showLeftDrawer = false; onNavigateToTasks() }
                DrawerItem(Icons.Default.Timer, "التايمر") { showLeftDrawer = false; onNavigateToTimer() }
                DrawerItem(Icons.Default.BarChart, "الإحصائيات") { showLeftDrawer = false; onNavigateToStats() }
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                Text("إضافات", fontSize = 11.sp, letterSpacing = 2.sp,
                    color = PrimaryColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                DrawerItem(Icons.Default.AccountTree, "خريطة الروابط") { showLeftDrawer = false }
                DrawerItem(Icons.Default.Tag, "الوسوم") { showLeftDrawer = false }
                DrawerItem(Icons.Default.Archive, "الأرشيف") { showLeftDrawer = false }
                DrawerItem(Icons.Default.FileDownload, "تصدير") { showLeftDrawer = false }
            }
        }

        // Right Drawer
        if (showRightDrawer) {
            Box(modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { showRightDrawer = false })
            Column(
                modifier = Modifier
                    .fillMaxHeight().width(280.dp)
                    .align(Alignment.CenterEnd)
                    .background(Color(0xFF1C1B1B))
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PrimaryColor, AccentColor))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("م", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C0062))
                    }
                    Column {
                        Text("مستخدم NoteFlow", fontWeight = FontWeight.Bold,
                            color = OnSurface, fontSize = 15.sp)
                        Text("noteflow@app.io", fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                }
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                Text("الإعدادات", fontSize = 11.sp, letterSpacing = 2.sp,
                    color = PrimaryColor, fontWeight = FontWeight.Bold)
                DrawerItem(Icons.Default.Settings, "إعدادات التطبيق") { showRightDrawer = false; onNavigateToSettings() }
                DrawerItem(Icons.Default.Palette, "المظهر والألوان") { showRightDrawer = false }
                DrawerItem(Icons.Default.Notifications, "الإشعارات") { showRightDrawer = false }
                DrawerItem(Icons.Default.Security, "الأمان") { showRightDrawer = false }
                DrawerItem(Icons.Default.Sync, "المزامنة") { showRightDrawer = false }
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                DrawerItem(Icons.Default.Logout, "تسجيل الخروج",
                    tint = Color(0xFFFF6B6B)) { showRightDrawer = false }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    tint: Color = OnSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Text(label, color = if (tint == OnSurfaceVariant) OnSurface else tint, fontSize = 15.sp)
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
                .background(if (task.isCompleted) PrimaryColor else Color.Transparent)
                .border(1.dp,
                    if (task.isCompleted) Color.Transparent else OutlineVariant,
                    RoundedCornerShape(4.dp))
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null,
                    tint = Color(0xFF1C0062), modifier = Modifier.size(13.dp))
            }
        }
        Text(
            text = task.title,
            fontSize = 14.sp,
            color = if (task.isCompleted) OnSurface.copy(alpha = 0.4f) else OnSurfaceVariant,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
    }
}
