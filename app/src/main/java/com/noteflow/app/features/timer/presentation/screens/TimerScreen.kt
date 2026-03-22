package com.noteflow.app.features.timer.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.tasks.presentation.TaskViewModel
import com.noteflow.app.features.timer.presentation.TimerViewModel

private val BgColor = Color(0xFF0D0D0D)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val TertiaryColor = Color(0xFF75D1FF)
private val OnSurface = Color(0xFFE5E2E1)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val OutlineVariant = Color(0xFF47464C)

private val motivationalMessages = listOf(
    "أنت أقرب مما تعتقد ✨",
    "التركيز هو أقوى سلاح لديك",
    "كل دقيقة تركيز تبني مستقبلك",
    "الإنجاز يبدأ بخطوة واحدة",
    "أنت تصنع التاريخ الآن",
    "العظماء لا يتوقفون 🔥",
    "ركّز، أنجز، انتصر"
)

private val whiteNoiseSounds = listOf(
    "🌧" to "مطر",
    "🌊" to "أمواج البحر",
    "🌲" to "غابة هادئة",
    "☕" to "كافيه",
    "🌀" to "ضوضاء بيضاء",
    "🔥" to "نار المدفأة",
    "🎵" to "موسيقى هادئة"
)

@Composable
fun TimerScreen(
    onBack: () -> Unit = {},
    timerViewModel: TimerViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val timeLeft by timerViewModel.timeLeft.collectAsState()
    val isRunning by timerViewModel.isRunning.collectAsState()
    val isWorkSession by timerViewModel.isWorkSession.collectAsState()
    val completedSessions by timerViewModel.completedSessions.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()

    var selectedTaskId by remember { mutableStateOf<Long?>(null) }
    var showTaskPicker by remember { mutableStateOf(false) }
    var showStopConfirm by remember { mutableStateOf(false) }
    var showNoiseSheet by remember { mutableStateOf(false) }
    var showTimerModeSheet by remember { mutableStateOf(false) }
    var selectedNoise by remember { mutableStateOf<String?>(null) }
    var isCountingUp by remember { mutableStateOf(false) }
    var countUpSeconds by remember { mutableStateOf(0L) }

    val selectedTaskName = tasks.find { it.id == selectedTaskId }?.title
    val totalDuration = if (isWorkSession) TimerViewModel.WORK_DURATION else TimerViewModel.BREAK_DURATION
    val progress = if (isCountingUp) 1f else timeLeft.toFloat() / totalDuration.toFloat()
    val minutes = if (isCountingUp) countUpSeconds / 60 else (timeLeft / 1000) / 60
    val seconds = if (isCountingUp) countUpSeconds % 60 else (timeLeft / 1000) % 60

    LaunchedEffect(isRunning, isCountingUp) {
        if (isRunning && isCountingUp) {
            while (isRunning) {
                kotlinx.coroutines.delay(1000)
                countUpSeconds++
            }
        }
    }

    val motivationalMsg = remember(completedSessions) {
        motivationalMessages[completedSessions % motivationalMessages.size]
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TimerTopBar(onBack = onBack)
            TimerTaskSelector(selectedTaskName = selectedTaskName, isRunning = isRunning, onShowPicker = { showTaskPicker = true })
            Spacer(modifier = Modifier.height(16.dp))
            TimerCircle(minutes = minutes, seconds = seconds, progress = progress, isWorkSession = isWorkSession, completedSessions = completedSessions)
            Spacer(modifier = Modifier.height(8.dp))
            TimerMotivation(message = motivationalMsg, isWorkSession = isWorkSession)
            Spacer(modifier = Modifier.height(24.dp))
            TimerControls(
                isRunning = isRunning,
                isWorkSession = isWorkSession,
                onStart = { timerViewModel.setTask(selectedTaskId); if (isCountingUp) { /* handled by LaunchedEffect */ } else timerViewModel.start() },
                onPause = { timerViewModel.pause() },
                onStop = { showStopConfirm = true }
            )
            Spacer(modifier = Modifier.weight(1f))
            TimerBottomBar(
                selectedNoise = selectedNoise,
                isCountingUp = isCountingUp,
                onNoiseClick = { showNoiseSheet = true },
                onModeClick = { showTimerModeSheet = true }
            )
        }

        if (showTaskPicker) TimerTaskPicker(tasks = tasks.filter { !it.isCompleted }, selectedTaskId = selectedTaskId,
            onSelect = { selectedTaskId = it; showTaskPicker = false }, onDismiss = { showTaskPicker = false })
        if (showStopConfirm) TimerStopConfirm(onConfirm = { timerViewModel.reset(); countUpSeconds = 0; showStopConfirm = false }, onDismiss = { showStopConfirm = false })
        if (showNoiseSheet) TimerNoiseSheet(selectedNoise = selectedNoise, onSelect = { selectedNoise = it; showNoiseSheet = false }, onDismiss = { showNoiseSheet = false })
        if (showTimerModeSheet) TimerModeSheet(isCountingUp = isCountingUp,
            onSelectMode = { countUp -> isCountingUp = countUp; if (!countUp) countUpSeconds = 0; showTimerModeSheet = false },
            onDismiss = { showTimerModeSheet = false })
    }
}

@Composable
private fun TimerTopBar(onBack: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(SurfaceHigh).clickable { onBack() }, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(22.dp))
        }
        Text("NoteFlow", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Composable
private fun TimerTaskSelector(selectedTaskName: String?, isRunning: Boolean, onShowPicker: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp), contentAlignment = Alignment.Center) {
        Row(modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(SurfaceColor)
            .clickable(enabled = !isRunning) { onShowPicker() }.padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (selectedTaskName != null) PrimaryColor else OutlineVariant, modifier = Modifier.size(16.dp))
            Text(
                text = selectedTaskName ?: "يرجى تحديد مهمة...",
                fontSize = 13.sp, color = if (selectedTaskName != null) OnSurface else OnSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            if (!isRunning) Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = OutlineVariant, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun TimerCircle(minutes: Long, seconds: Long, progress: Float, isWorkSession: Boolean, completedSessions: Int) {
    val breatheScale by rememberInfiniteTransition(label = "breathe").animateFloat(
        initialValue = 1f, targetValue = if (!isWorkSession) 1.04f else 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOut), RepeatMode.Reverse), label = "scale"
    )
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(260.dp).scale(breatheScale), contentAlignment = Alignment.Center) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 10.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)
                drawArc(color = SurfaceHigh, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                    topLeft = topLeft, size = arcSize, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                drawArc(brush = Brush.linearGradient(listOf(if (isWorkSession) PrimaryColor else TertiaryColor, AccentColor)),
                    startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
                    topLeft = topLeft, size = arcSize, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("%02d:%02d".format(minutes, seconds), fontSize = 54.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(if (isWorkSession) "جلسة تركيز" else "وقت الراحة 🌿", fontSize = 12.sp, letterSpacing = 2.sp, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(4) { index ->
                        Box(modifier = Modifier.size(if (index == completedSessions % 4) 10.dp else 7.dp).clip(CircleShape)
                            .background(when {
                                index < completedSessions % 4 -> PrimaryColor
                                index == completedSessions % 4 -> AccentColor
                                else -> SurfaceHigh
                            }))
                    }
                }
                Text("الجلسة ${(completedSessions % 4) + 1} من 4", fontSize = 10.sp, letterSpacing = 1.sp, color = OutlineVariant)
            }
        }
    }
}

@Composable
private fun TimerMotivation(message: String, isWorkSession: Boolean) {
    if (!isWorkSession) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("تنفس بعمق...", fontSize = 13.sp, color = TertiaryColor, letterSpacing = 1.sp)
            val breathText by rememberInfiniteTransition(label = "breath").animateFloat(
                initialValue = 0f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Reverse), label = "breathAlpha"
            )
            Text(if (breathText < 0.5f) "شهيق..." else "زفير...", fontSize = 12.sp, color = OnSurfaceVariant.copy(alpha = 0.6f + breathText * 0.4f))
        }
    } else {
        Text(text = message, fontSize = 13.sp, color = OnSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun TimerControls(isRunning: Boolean, isWorkSession: Boolean, onStart: () -> Unit, onPause: () -> Unit, onStop: () -> Unit) {
    val fabScale by animateFloatAsState(targetValue = if (isRunning) 0.95f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "fab")
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.size(200.dp, 56.dp).scale(fabScale).clip(RoundedCornerShape(50.dp))
            .background(if (isRunning) SurfaceColor else Color.White)
            .clickable { if (isRunning) onPause() else onStart() },
            contentAlignment = Alignment.Center) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isRunning) OnSurface else Color(0xFF131313),
                    modifier = Modifier.size(22.dp))
                Text(if (isRunning) "إيقاف مؤقت" else "ابدأ التركيز",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = if (isRunning) OnSurface else Color(0xFF131313))
            }
        }
        if (isRunning) {
            Box(modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(SurfaceHigh).clickable { onStop() }.padding(horizontal = 24.dp, vertical = 10.dp)) {
                Text("إيقاف", fontSize = 13.sp, color = Color(0xFFFF6B6B))
            }
        }
    }
}

@Composable
private fun TimerBottomBar(selectedNoise: String?, isCountingUp: Boolean, onNoiseClick: () -> Unit, onModeClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(SurfaceColor).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        TimerBottomBtn(icon = Icons.Default.MusicNote, label = selectedNoise ?: "ضوضاء بيضاء",
            isActive = selectedNoise != null, onClick = onNoiseClick)
        TimerBottomBtn(icon = Icons.Default.Timer, label = if (isCountingUp) "تصاعدي" else "تنازلي",
            isActive = isCountingUp, onClick = onModeClick)
        TimerBottomBtn(icon = Icons.Default.SelfImprovement, label = "الوضع الصارم", isActive = false, onClick = {})
    }
}

@Composable
private fun TimerBottomBtn(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable { onClick() }.padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, tint = if (isActive) PrimaryColor else OnSurfaceVariant, modifier = Modifier.size(22.dp))
        Text(label, fontSize = 10.sp, color = if (isActive) PrimaryColor else OnSurfaceVariant, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun TimerTaskPicker(tasks: List<com.noteflow.app.features.tasks.domain.model.Task>, selectedTaskId: Long?, onSelect: (Long?) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = SurfaceColor,
        title = { Text("اختار مهمة", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                item {
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                        .clickable { onSelect(null) }.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotInterested, contentDescription = null, tint = OutlineVariant, modifier = Modifier.size(18.dp))
                        Text("بلاش مهمة", color = OnSurfaceVariant, fontSize = 14.sp)
                    }
                }
                items(tasks) { task ->
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                        .background(if (selectedTaskId == task.id) PrimaryColor.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable { onSelect(task.id) }.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null,
                            tint = if (selectedTaskId == task.id) PrimaryColor else OutlineVariant, modifier = Modifier.size(18.dp))
                        Text(task.title, color = if (selectedTaskId == task.id) PrimaryColor else Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }, confirmButton = {})
}

@Composable
private fun TimerStopConfirm(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = SurfaceColor,
        title = { Text("إيقاف الجلسة؟", color = Color.White, fontWeight = FontWeight.Bold) },
        text = { Text("هل أنت متأكد؟ سيتم إلغاء التقدم الحالي", color = OnSurfaceVariant) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("إيقاف", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("تابع التركيز", color = PrimaryColor) } }
    )
}

@Composable
private fun TimerNoiseSheet(selectedNoise: String?, onSelect: (String?) -> Unit, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onDismiss() })
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .background(SurfaceColor).padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(OutlineVariant).align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(4.dp))
        Text("اختار صوت هادئ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        if (selectedNoise != null) {
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFFF6B6B).copy(alpha = 0.1f))
                .clickable { onSelect(null) }.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🔕", fontSize = 20.sp)
                Text("إيقاف الصوت", color = Color(0xFFFF6B6B), fontSize = 14.sp)
            }
        }
        whiteNoiseSounds.forEach { (emoji, name) ->
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(if (selectedNoise == name) PrimaryColor.copy(alpha = 0.15f) else SurfaceHigh)
                .border(if (selectedNoise == name) 1.dp else 0.dp, PrimaryColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .clickable { onSelect(name) }.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 22.sp)
                Text(name, color = if (selectedNoise == name) PrimaryColor else OnSurface, fontSize = 14.sp, fontWeight = if (selectedNoise == name) FontWeight.Bold else FontWeight.Normal)
                if (selectedNoise == name) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TimerModeSheet(isCountingUp: Boolean, onSelectMode: (Boolean) -> Unit, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onDismiss() })
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .background(SurfaceColor).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(OutlineVariant).align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(4.dp))
        Text("وضع المؤقت", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        TimerModeOption(icon = Icons.Default.HourglassBottom, title = "عد تنازلي", subtitle = "25 دقيقة ← 0", isSelected = !isCountingUp) { onSelectMode(false) }
        TimerModeOption(icon = Icons.Default.HourglassTop, title = "عد تصاعدي", subtitle = "0 ← ∞ بلا حد", isSelected = isCountingUp) { onSelectMode(true) }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TimerModeOption(icon: ImageVector, title: String, subtitle: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
        .background(if (isSelected) PrimaryColor.copy(alpha = 0.12f) else SurfaceHigh)
        .border(if (isSelected) 1.dp else 0.dp, PrimaryColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
        .clickable { onClick() }.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(if (isSelected) PrimaryColor.copy(alpha = 0.2f) else OutlineVariant.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = if (isSelected) PrimaryColor else OnSurfaceVariant, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isSelected) PrimaryColor else OnSurface)
            Text(subtitle, fontSize = 12.sp, color = OnSurfaceVariant)
        }
        if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
    }
}
