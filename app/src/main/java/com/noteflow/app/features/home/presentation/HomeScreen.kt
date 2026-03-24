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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import com.noteflow.app.ui.components.ObsidianToolbar as SharedObsidianToolbar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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
    onNavigateToAi: () -> Unit,
    noteViewModel: NoteViewModel = hiltViewModel(),
    timerViewModel: TimerViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val timeLeft by timerViewModel.timeLeft.collectAsState()
    val isRunning by timerViewModel.isRunning.collectAsState()
    val isWorkSession by timerViewModel.isWorkSession.collectAsState()
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf(TextFieldValue("")) }
    var showLeftDrawer by remember { mutableStateOf(false) }
    var showRightDrawer by remember { mutableStateOf(false) }
    var isWriting by remember { mutableStateOf(false) }
    var timerFullScreen by remember { mutableStateOf(false) }
    var tasksFullScreen by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "صباح الخير"
        in 12..17 -> "مساء النور"
        else -> "مساء الخير"
    }

    val cardsAlpha by animateFloatAsState(targetValue = if (isWriting) 0f else 1f, animationSpec = tween(350), label = "cards")
    val writeAlpha by animateFloatAsState(targetValue = if (timerFullScreen || tasksFullScreen) 0f else 1f, animationSpec = tween(350), label = "write")

    LaunchedEffect(noteTitle, noteContent.text) {
        if (noteTitle.isNotBlank()) {
            kotlinx.coroutines.delay(2000)
            noteViewModel.triggerAutoSave(noteTitle, noteContent.text, 0)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(modifier = Modifier.fillMaxSize().background(BgColor).imePadding()) {
        when {
            timerFullScreen -> TimerFullScreen(timeLeft, isRunning, isWorkSession,
                onToggle = { if (isRunning) timerViewModel.pause() else timerViewModel.start() },
                onClose = { timerFullScreen = false })
            tasksFullScreen -> TasksFullScreen(tasks, onToggle = { taskViewModel.toggleComplete(it) }, onClose = { tasksFullScreen = false })
            else -> {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    HomeTopBar(greeting, { showRightDrawer = true }, { showLeftDrawer = true }, onNavigateToStats)
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.graphicsLayer(alpha = writeAlpha)) {
                            HomeQuickWrite(
                                noteTitle = noteTitle,
                                noteContent = noteContent,
                                onTitleChange = { noteTitle = it },
                                onContentChange = { noteContent = it },
                                onFocusChange = { isWriting = it }
                            )
                        }
                        if (!isWriting) {
                            Box(modifier = Modifier.graphicsLayer(alpha = cardsAlpha)) {
                                HomeCardsRow(tasks, timeLeft, isRunning, isWorkSession,
                                    { taskViewModel.toggleComplete(it) },
                                    { if (isRunning) timerViewModel.pause() else timerViewModel.start() },
                                    { onNavigateToTimer() }, { tasksFullScreen = true })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(140.dp))
                }

                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    if (isWriting) {
                        val imeVisible = WindowInsets.isImeVisible
                        AnimatedVisibility(
                            visible = imeVisible,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            SharedObsidianToolbar(
                                value = noteContent,
                                onValueChange = { noteContent = it },
                                modifier = Modifier.navigationBarsPadding()
                            )
                        }
                        HomeWritingMiniBar(tasks, timeLeft, isRunning,
                            onStop = { isWriting = false; focusManager.clearFocus() })
                    } else {
                        HomeBottomNav(
                            onWrite = { isWriting = true },
                            onShowAddSheet = { showAddSheet = true },
                            onNavigateToTasks = { tasksFullScreen = true },
                            onNavigateToSearch = onNavigateToSearch,
                            onNavigateToAi = onNavigateToAi,
                            onNavigateToSettings = onNavigateToSettings
                        )
                    }
                }
            }
        }

        if (showAddSheet) {
            AddBottomSheet(
                onDismiss = { showAddSheet = false },
                onNewNote = { showAddSheet = false; onAddNote() },
                onNewTask = { showAddSheet = false; tasksFullScreen = true },
                onStartTimer = { showAddSheet = false; timerFullScreen = true },
                onNewNote2 = { showAddSheet = false; onNavigateToNotes() }
            )
        }

        if (showLeftDrawer) HomeLeftDrawer({ showLeftDrawer = false }, onNavigateToNotes, onNavigateToTasks, onNavigateToTimer, onNavigateToStats)
        if (showRightDrawer) HomeRightDrawer({ showRightDrawer = false }, onNavigateToSettings)
    }
    } // end CompositionLocalProvider
}

@Composable
private fun AddBottomSheet(
    onDismiss: () -> Unit,
    onNewNote: () -> Unit,
    onNewTask: () -> Unit,
    onStartTimer: () -> Unit,
    onNewNote2: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onDismiss() })
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFF1C1B1B)).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(OutlineVariant))
        Spacer(modifier = Modifier.height(4.dp))
        AddSheetItem(Icons.Default.EditNote, "ملاحظة جديدة", "ابدأ كتابة فكرة جديدة", PrimaryColor) { onNewNote() }
        AddSheetItem(Icons.Default.CheckCircle, "مهمة جديدة", "أضف مهمة لقائمتك", AccentColor) { onNewTask() }
        AddSheetItem(Icons.Default.Timer, "ابدأ جلسة تركيز", "بومودورو 25 دقيقة", TertiaryColor) { onStartTimer() }
        AddSheetItem(Icons.Default.Notes, "كل الملاحظات", "تصفح ملاحظاتك", OnSurfaceVariant) { onNewNote2() }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AddSheetItem(icon: ImageVector, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(SurfaceHigh).clickable { onClick() }.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            Text(subtitle, fontSize = 12.sp, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun ObsidianToolbar(noteContent: TextFieldValue, onContentChange: (TextFieldValue) -> Unit) {
    val symbols = listOf("# ", "## ", "### ", "**text**", "*text*", "- ", "> ", "[[]]", "`code`", "---\n", "- [ ] ", "@")
    val labels = listOf("H1", "H2", "H3", "B", "I", "•", "❝", "[[", "<>", "—", "☐", "@")

    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        symbols.forEachIndexed { index, symbol ->
            Box(
                modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(SurfaceHigh)
                    .clickable {
                        val cursor = noteContent.selection.end
                        val text = noteContent.text
                        val insertion = when (symbol) {
                            "**text**" -> "****"
                            "*text*" -> "**"
                            "`code`" -> "``"
                            else -> symbol
                        }
                        val newText = text.substring(0, cursor) + insertion + text.substring(cursor)
                        val newCursor = when (symbol) {
                            "**text**" -> cursor + 2
                            "*text*" -> cursor + 1
                            "`code`" -> cursor + 1
                            "[[]]" -> cursor + 2
                            else -> cursor + insertion.length
                        }
                        onContentChange(TextFieldValue(newText, TextRange(newCursor)))
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(labels[index], fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HomeWritingMiniBar(tasks: List<Task>, timeLeft: Long, isRunning: Boolean, onStop: () -> Unit) {
    val activeTasks = tasks.filter { !it.isCompleted }
    val timerMinutes = (timeLeft / 1000) / 60
    val timerSeconds = (timeLeft / 1000) % 60
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(50.dp)).background(SurfaceColor.copy(alpha = 0.97f))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.clickable { onStop() }, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(14.dp))
            Text("إنهاء", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.width(1.dp).height(16.dp).background(OutlineVariant))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = AccentColor.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
            Text("${activeTasks.size}", fontSize = 12.sp, color = OnSurfaceVariant)
        }
        Box(modifier = Modifier.width(1.dp).height(16.dp).background(OutlineVariant))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (isRunning) Icons.Default.Pause else Icons.Default.Timer, contentDescription = null, tint = TertiaryColor.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
            Text("%02d:%02d".format(timerMinutes, timerSeconds), fontSize = 12.sp, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun HomeBottomNav(onWrite: () -> Unit, onShowAddSheet: () -> Unit, onNavigateToTasks: () -> Unit, onNavigateToSearch: () -> Unit, onNavigateToAi: () -> Unit, onNavigateToSettings: () -> Unit) {
    var fabPressed by remember { mutableStateOf(false) }
    val fabScale by animateFloatAsState(targetValue = if (fabPressed) 0.85f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "fab")
    LaunchedEffect(fabPressed) { if (fabPressed) { kotlinx.coroutines.delay(150); fabPressed = false } }
    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), contentAlignment = Alignment.Center) {
        Row(modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(Color(0xFF201F1F).copy(alpha = 0.95f)).padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).clickable { onWrite() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.EditNote, contentDescription = null, tint = OnSurface.copy(alpha = 0.6f), modifier = Modifier.size(26.dp))
            }
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).clickable { onNavigateToTasks() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = OnSurface.copy(alpha = 0.5f), modifier = Modifier.size(26.dp))
            }
            Box(modifier = Modifier.size(60.dp).scale(fabScale).clip(CircleShape)
                .background(Brush.linearGradient(listOf(PrimaryColor, AccentColor)))
                .clickable { fabPressed = true; onShowAddSheet() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF131313), modifier = Modifier.size(28.dp))
            }
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).clickable { onNavigateToAi() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentColor.copy(alpha = 0.8f), modifier = Modifier.size(26.dp))
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
private fun HomeQuickWrite(noteTitle: String, noteContent: TextFieldValue, onTitleChange: (String) -> Unit, onContentChange: (TextFieldValue) -> Unit, onFocusChange: (Boolean) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceLowest).padding(20.dp)) {
        BasicTextField(value = noteTitle, onValueChange = onTitleChange,
            textStyle = TextStyle(color = OnSurface, fontSize = 26.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp, textAlign = TextAlign.Right),
            cursorBrush = SolidColor(PrimaryColor),
            modifier = Modifier.fillMaxWidth().onFocusChanged { onFocusChange(it.isFocused) }, singleLine = true,
            decorationBox = { inner ->
                if (noteTitle.isEmpty()) Text("✦ العنوان", color = PrimaryColor.copy(alpha = 0.25f), fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                inner()
            })
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, OutlineVariant.copy(alpha = 0.4f), Color.Transparent))))
        Spacer(modifier = Modifier.height(12.dp))
        BasicTextField(value = noteContent, onValueChange = onContentChange,
            textStyle = TextStyle(color = OnSurfaceVariant, fontSize = 15.sp, lineHeight = 26.sp, textAlign = TextAlign.Right),
            cursorBrush = SolidColor(PrimaryColor),
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 120.dp).onFocusChanged { onFocusChange(it.isFocused) },
            decorationBox = { inner ->
                if (noteContent.text.isEmpty()) Text("اكتب أفكارك هنا...", color = OnSurfaceVariant.copy(alpha = 0.25f), fontSize = 15.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                inner()
            })
        if (noteTitle.isNotBlank() || noteContent.text.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("يحفظ تلقائياً...", fontSize = 10.sp, letterSpacing = 1.sp, color = OutlineVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Left)
        }
    }
}

@Composable
private fun HomeCardsRow(tasks: List<Task>, timeLeft: Long, isRunning: Boolean, isWorkSession: Boolean, onToggleTask: (Task) -> Unit, onTimerToggle: () -> Unit, onTimerFullScreen: () -> Unit, onTasksFullScreen: () -> Unit) {
    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }
    val totalTasks = tasks.size
    val completionRate = if (totalTasks == 0) 0f else completedTasks.size.toFloat() / totalTasks.toFloat()
    val totalDuration = if (isWorkSession) TimerViewModel.WORK_DURATION else TimerViewModel.BREAK_DURATION
    val timerProgress = timeLeft.toFloat() / totalDuration.toFloat()
    val timerMinutes = (timeLeft / 1000) / 60
    val timerSeconds = (timeLeft / 1000) % 60
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        HomeTasksCard(Modifier.weight(1f), activeTasks, completedTasks, totalTasks, completionRate, onToggleTask, onTasksFullScreen)
        HomeTimerCard(Modifier.weight(1f), timerMinutes, timerSeconds, timerProgress, isRunning, onTimerToggle, onTimerFullScreen)
    }
}

@Composable
private fun HomeTasksCard(modifier: Modifier, activeTasks: List<Task>, completedTasks: List<Task>, totalTasks: Int, completionRate: Float, onToggleTask: (Task) -> Unit, onFullScreen: () -> Unit) {
    Column(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(SurfaceColor).clickable { onFullScreen() }.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("اليوم", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            Text("${completedTasks.size}/$totalTasks", fontSize = 10.sp, color = PrimaryColor)
        }
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).clip(RoundedCornerShape(1.dp)).background(SurfaceHigh)) {
            Box(modifier = Modifier.fillMaxWidth(completionRate).height(2.dp).background(Brush.horizontalGradient(listOf(PrimaryColor, AccentColor))))
        }
        val displayTasks = (activeTasks.take(2) + completedTasks.take(1)).take(3)
        if (displayTasks.isEmpty()) Text("لا توجد مهام ✨", color = OnSurfaceVariant, fontSize = 12.sp)
        else displayTasks.forEach { task -> AnimatedTaskRow(task) { onToggleTask(task) } }
    }
}

@Composable
private fun HomeTimerCard(modifier: Modifier, timerMinutes: Long, timerSeconds: Long, timerProgress: Float, isRunning: Boolean, onTimerToggle: () -> Unit, onFullScreen: () -> Unit) {
    Column(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(SurfaceColor).clickable { onFullScreen() }.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("تايمر", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 5.dp.toPx(); val radius = size.minDimension / 2 - strokeWidth
                    drawArc(color = SurfaceHigh, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth, strokeWidth), size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2))
                    drawArc(color = TertiaryColor, startAngle = -90f, sweepAngle = 360f * timerProgress, useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth, strokeWidth), size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("%02d:%02d".format(timerMinutes, timerSeconds), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(if (isRunning) TertiaryColor.copy(alpha = 0.2f) else Color.Transparent).clickable { onTimerToggle() }, contentAlignment = Alignment.Center) {
                        Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = TertiaryColor, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedTaskRow(task: Task, onToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.size(18.dp).clip(RoundedCornerShape(4.dp))
            .background(if (task.isCompleted) PrimaryColor else Color.Transparent)
            .border(1.dp, if (task.isCompleted) Color.Transparent else OutlineVariant, RoundedCornerShape(4.dp))
            .clickable { onToggle() }, contentAlignment = Alignment.Center) {
            if (task.isCompleted) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1C0062), modifier = Modifier.size(11.dp))
        }
        Text(task.title, fontSize = 13.sp, color = if (task.isCompleted) OnSurface.copy(alpha = 0.4f) else OnSurfaceVariant,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null, modifier = Modifier.weight(1f), maxLines = 1)
    }
}

@Composable
private fun TimerFullScreen(timeLeft: Long, isRunning: Boolean, isWorkSession: Boolean, onToggle: () -> Unit, onClose: () -> Unit) {
    val timerMinutes = (timeLeft / 1000) / 60
    val timerSeconds = (timeLeft / 1000) % 60
    val totalDuration = if (isWorkSession) TimerViewModel.WORK_DURATION else TimerViewModel.BREAK_DURATION
    val timerProgress = timeLeft.toFloat() / totalDuration.toFloat()
    Box(modifier = Modifier.fillMaxSize().background(BgColor), contentAlignment = Alignment.Center) {
        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.TopStart).padding(16.dp).statusBarsPadding()) {
            Icon(Icons.Default.Close, contentDescription = null, tint = OnSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(32.dp)) {
            Text(if (isWorkSession) "وقت التركيز" else "وقت الراحة", fontSize = 13.sp, letterSpacing = 2.sp, color = OnSurfaceVariant)
            Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx(); val radius = size.minDimension / 2 - strokeWidth
                    drawArc(color = SurfaceHigh, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth, strokeWidth), size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2))
                    drawArc(color = TertiaryColor, startAngle = -90f, sweepAngle = 360f * timerProgress, useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth, strokeWidth), size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2))
                }
                Text("%02d:%02d".format(timerMinutes, timerSeconds), fontSize = 52.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            }
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(if (isRunning) SurfaceHigh else SurfaceColor).clickable { onToggle() }, contentAlignment = Alignment.Center) {
                Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = TertiaryColor, modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Composable
private fun TasksFullScreen(tasks: List<Task>, onToggle: (Task) -> Unit, onClose: () -> Unit) {
    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }
    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.statusBarsPadding())
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("المهام", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = null, tint = OnSurfaceVariant) }
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (activeTasks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { Text("🎉 كل المهام مكتملة!", color = OnSurfaceVariant, fontSize = 16.sp) }
                } else { activeTasks.forEach { task -> FullScreenTaskRow(task) { onToggle(task) } } }
                if (completedTasks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("مكتملة", fontSize = 11.sp, letterSpacing = 2.sp, color = OutlineVariant)
                    completedTasks.take(5).forEach { task -> FullScreenTaskRow(task) { onToggle(task) } }
                }
            }
        }
    }
}

@Composable
private fun FullScreenTaskRow(task: Task, onToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceColor).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(24.dp).clip(CircleShape)
            .background(if (task.isCompleted) PrimaryColor else Color.Transparent)
            .border(1.5.dp, if (task.isCompleted) Color.Transparent else OutlineVariant, CircleShape)
            .clickable { onToggle() }, contentAlignment = Alignment.Center) {
            if (task.isCompleted) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1C0062), modifier = Modifier.size(14.dp))
        }
        Text(task.title, fontSize = 15.sp, color = if (task.isCompleted) OnSurface.copy(alpha = 0.4f) else OnSurface,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null, modifier = Modifier.weight(1f))
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
            IconButton(onClick = onNavigateToStats) { Icon(Icons.Default.ShowChart, contentDescription = null, tint = OnSurface.copy(alpha = 0.6f), modifier = Modifier.size(22.dp)) }
            IconButton(onClick = onShowLeftDrawer) { Icon(Icons.Default.Menu, contentDescription = null, tint = OnSurface.copy(alpha = 0.6f), modifier = Modifier.size(22.dp)) }
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
