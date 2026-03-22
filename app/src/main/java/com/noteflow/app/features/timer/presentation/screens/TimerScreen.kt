package com.noteflow.app.features.timer.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
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
    "🌧" to "مطر", "🌊" to "أمواج البحر", "🌲" to "غابة هادئة",
    "☕" to "كافيه", "🌀" to "ضوضاء بيضاء", "🔥" to "نار المدفأة", "🎵" to "موسيقى هادئة"
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
    val sessionFinished by timerViewModel.sessionFinished.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()

    var selectedTaskId by remember { mutableStateOf<Long?>(null) }
    var showTaskPicker by remember { mutableStateOf(false) }
    var showStopConfirm by remember { mutableStateOf(false) }
    var showSkipConfirm by remember { mutableStateOf(false) }
    var showNoiseSheet by remember { mutableStateOf(false) }
    var showTimerModeSheet by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedNoise by remember { mutableStateOf<String?>(null) }
    var isCountingUp by remember { mutableStateOf(false) }
    var countUpSeconds by remember { mutableStateOf(0L) }
    var selectedHours by remember { mutableStateOf(0) }
    var selectedMinutes by remember { mutableStateOf(25) }

    val selectedTaskName = tasks.find { it.id == selectedTaskId }?.title
    val totalDuration = if (isCountingUp) (countUpSeconds * 1000L).coerceAtLeast(1L)
        else if (isWorkSession) timerViewModel.customDuration.collectAsState().value
        else if (completedSessions % 4 == 0 && completedSessions > 0) TimerViewModel.LONG_BREAK_DURATION
        else TimerViewModel.BREAK_DURATION
    val progress = if (isCountingUp) {
        val target = (selectedHours * 3600L + selectedMinutes * 60L).coerceAtLeast(1L)
        (countUpSeconds.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    } else timeLeft.toFloat() / totalDuration.toFloat()
    val minutes = if (isCountingUp) countUpSeconds / 60 else (timeLeft / 1000) / 60
    val seconds = if (isCountingUp) countUpSeconds % 60 else (timeLeft / 1000) % 60
    val motivationalMsg = remember(completedSessions) { motivationalMessages[completedSessions % motivationalMessages.size] }

    LaunchedEffect(isRunning, isCountingUp) {
        if (isRunning && isCountingUp) {
            while (isRunning) {
                kotlinx.coroutines.delay(1000)
                countUpSeconds++
                val target = selectedHours * 3600L + selectedMinutes * 60L
                if (target > 0 && countUpSeconds >= target) {
                    timerViewModel.pause()
                    break
                }
            }
        }
    }

    LaunchedEffect(sessionFinished) {
        if (sessionFinished) timerViewModel.acknowledgeFinished()
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TimerTopBar(onBack = onBack)
            TimerTaskSelector(selectedTaskName, isRunning) { showTaskPicker = true }
            Spacer(modifier = Modifier.height(12.dp))
            TimerCircleDisplay(minutes, seconds, progress, isWorkSession, completedSessions,
                isRunning = isRunning, isCountingUp = isCountingUp,
                onTimePickerClick = { if (!isRunning) showTimePicker = true })
            Spacer(modifier = Modifier.height(8.dp))
            TimerMotivationText(motivationalMsg, isWorkSession)
            Spacer(modifier = Modifier.height(20.dp))
            TimerMainControls(
                isRunning = isRunning, isWorkSession = isWorkSession,
                onStart = { timerViewModel.setTask(selectedTaskId); if (!isCountingUp) timerViewModel.start() else { timerViewModel.setTask(selectedTaskId); timerViewModel.start() } },
                onPause = { timerViewModel.pause() },
                onStop = { showStopConfirm = true },
                onSkip = { showSkipConfirm = true }
            )
            Spacer(modifier = Modifier.weight(1f))
            TimerBottomToolbar(selectedNoise, isCountingUp,
                { showNoiseSheet = true }, { showTimerModeSheet = true })
        }

        if (showTaskPicker) TimerTaskPickerDialog(tasks.filter { !it.isCompleted }, selectedTaskId,
            { selectedTaskId = it; showTaskPicker = false }, { showTaskPicker = false })
        if (showStopConfirm) TimerConfirmDialog("إيقاف الجلسة؟", "هل أنت متأكد؟ سيتم إلغاء التقدم",
            "إيقاف", "تابع التركيز",
            { timerViewModel.reset(); countUpSeconds = 0; showStopConfirm = false }, { showStopConfirm = false })
        if (showSkipConfirm) TimerConfirmDialog("تخطي الجلسة؟", "هل أنت متأكد من التخطي؟",
            "تخطي", "إلغاء",
            { timerViewModel.skipSession(); countUpSeconds = 0; showSkipConfirm = false }, { showSkipConfirm = false })
        if (showNoiseSheet) TimerNoiseBottomSheet(selectedNoise, { selectedNoise = it; showNoiseSheet = false }, { showNoiseSheet = false })
        if (showTimerModeSheet) TimerModeBottomSheet(isCountingUp, { isCountingUp = it; countUpSeconds = 0; showTimerModeSheet = false }, { showTimerModeSheet = false })
        if (showTimePicker) TimerTimePickerDialog(selectedHours, selectedMinutes,
            { h, m -> selectedHours = h; selectedMinutes = m; timerViewModel.setCustomDuration(h, m); showTimePicker = false },
            { showTimePicker = false })
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
            Icon(Icons.Default.CheckCircle, contentDescription = null,
                tint = if (selectedTaskName != null) PrimaryColor else OutlineVariant, modifier = Modifier.size(16.dp))
            Text(selectedTaskName ?: "يرجى تحديد مهمة...", fontSize = 13.sp,
                color = if (selectedTaskName != null) OnSurface else OnSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (!isRunning) Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = OutlineVariant, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun TimerCircleDisplay(minutes: Long, seconds: Long, progress: Float, isWorkSession: Boolean,
    completedSessions: Int, isRunning: Boolean, isCountingUp: Boolean, onTimePickerClick: () -> Unit) {
    val breatheScale by rememberInfiniteTransition(label = "breathe").animateFloat(
        initialValue = 1f, targetValue = if (!isWorkSession && isRunning) 1.03f else 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOut), RepeatMode.Reverse), label = "scale"
    )
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp), contentAlignment = Alignment.Center) {
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
                Box(modifier = Modifier.clickable { onTimePickerClick() }) {
                    Text("%02d:%02d".format(minutes, seconds), fontSize = 52.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text(if (isWorkSession) "جلسة تركيز" else if (completedSessions % 4 == 0 && completedSessions > 0) "استراحة كبيرة 🌿" else "استراحة قصيرة 🌿",
                    fontSize = 11.sp, letterSpacing = 2.sp, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(4) { index ->
                        Box(modifier = Modifier.size(if (index == completedSessions % 4) 10.dp else 7.dp).clip(CircleShape)
                            .background(when { index < completedSessions % 4 -> PrimaryColor; index == completedSessions % 4 -> AccentColor; else -> SurfaceHigh }))
                    }
                }
                Text("الجلسة ${(completedSessions % 4) + 1} من 4", fontSize = 10.sp, letterSpacing = 1.sp, color = OutlineVariant)
                if (!isRunning) Text("اضغط على الوقت لتغييره", fontSize = 10.sp, color = OutlineVariant.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun TimerMotivationText(message: String, isWorkSession: Boolean) {
    if (!isWorkSession) {
        val breathAlpha by rememberInfiniteTransition(label = "breath").animateFloat(
            initialValue = 0.4f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Reverse), label = "alpha"
        )
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("تنفس بعمق...", fontSize = 13.sp, color = TertiaryColor.copy(alpha = breathAlpha))
        }
    } else {
        Text(message, fontSize = 13.sp, color = OnSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun TimerMainControls(isRunning: Boolean, isWorkSession: Boolean,
    onStart: () -> Unit, onPause: () -> Unit, onStop: () -> Unit, onSkip: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(modifier = Modifier.size(220.dp, 56.dp).clip(RoundedCornerShape(50.dp))
            .background(if (isRunning) SurfaceColor else Color.White)
            .clickable { if (isRunning) onPause() else onStart() },
            contentAlignment = Alignment.Center) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isRunning) OnSurface else Color(0xFF131313),
                    modifier = Modifier.size(22.dp))
                Text(if (isRunning) "إيقاف مؤقت" else if (isWorkSession) "ابدأ التركيز" else "ابدأ الاستراحة",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = if (isRunning) OnSurface else Color(0xFF131313))
            }
        }
        if (isRunning) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(SurfaceHigh)
                    .clickable { onStop() }.padding(horizontal = 24.dp, vertical = 10.dp)) {
                    Text("إيقاف", fontSize = 13.sp, color = Color(0xFFFF6B6B))
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(SurfaceHigh)
                    .clickable { onSkip() }.padding(horizontal = 24.dp, vertical = 10.dp)) {
                    Text("تخطي ⏭", fontSize = 13.sp, color = OnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun TimerBottomToolbar(selectedNoise: String?, isCountingUp: Boolean, onNoiseClick: () -> Unit, onModeClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(SurfaceColor).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        TimerToolBtn(Icons.Default.MusicNote, selectedNoise ?: "ضوضاء بيضاء", selectedNoise != null, onNoiseClick)
        TimerToolBtn(Icons.Default.Timer, if (isCountingUp) "تصاعدي" else "تنازلي", isCountingUp, onModeClick)
        TimerToolBtn(Icons.Default.SelfImprovement, "الوضع الصارم", false) {}
    }
}

@Composable
private fun TimerToolBtn(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable { onClick() }.padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, tint = if (isActive) PrimaryColor else OnSurfaceVariant, modifier = Modifier.size(22.dp))
        Text(label, fontSize = 10.sp, color = if (isActive) PrimaryColor else OnSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun TimerTimePickerDialog(initialHours: Int, initialMinutes: Int, onConfirm: (Int, Int) -> Unit, onDismiss: () -> Unit) {
    var hours by remember { mutableStateOf(initialHours) }
    var minutes by remember { mutableStateOf(initialMinutes) }

    AlertDialog(onDismissRequest = onDismiss, containerColor = SurfaceColor,
        title = { Text("اختار الوقت", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("اسحب لأعلى أو لأسفل لتغيير الوقت", fontSize = 12.sp, color = OnSurfaceVariant, textAlign = TextAlign.Center)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    TimeScrollPicker(value = hours, maxValue = 23, label = "ساعة") { hours = it }
                    Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    TimeScrollPicker(value = minutes, maxValue = 59, label = "دقيقة") { minutes = it }
                }
                Text("الوقت المحدد: %02d:%02d".format(hours, minutes), fontSize = 13.sp, color = PrimaryColor)
            }
        },
        confirmButton = { TextButton(onClick = { if (hours > 0 || minutes > 0) onConfirm(hours, minutes) }) { Text("تأكيد", color = PrimaryColor, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء", color = OnSurfaceVariant) } }
    )
}

@Composable
private fun TimeScrollPicker(value: Int, maxValue: Int, label: String, onValueChange: (Int) -> Unit) {
    var dragAccumulator by remember { mutableStateOf(0f) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 10.sp, color = OutlineVariant, letterSpacing = 1.sp)
        Box(modifier = Modifier.size(72.dp, 120.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceHigh)
            .pointerInput(value) {
                detectVerticalDragGestures(
                    onDragEnd = { dragAccumulator = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        dragAccumulator += dragAmount
                        val steps = (dragAccumulator / 40).toInt()
                        if (steps != 0) {
                            val newVal = (value - steps).coerceIn(0, maxValue)
                            onValueChange(newVal)
                            dragAccumulator -= steps * 40f
                        }
                    }
                )
            }, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("%02d".format((value - 1).coerceAtLeast(0)), fontSize = 18.sp, color = OutlineVariant)
                Text("%02d".format(value), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                Text("%02d".format((value + 1).coerceAtMost(maxValue)), fontSize = 18.sp, color = OutlineVariant)
            }
        }
    }
}

@Composable
private fun TimerTaskPickerDialog(tasks: List<com.noteflow.app.features.tasks.domain.model.Task>, selectedTaskId: Long?, onSelect: (Long?) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = SurfaceColor,
        title = { Text("اختار مهمة", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                item {
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { onSelect(null) }.padding(12.dp),
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
                        Text(task.title, color = if (selectedTaskId == task.id) PrimaryColor else Color.White,
                            fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }, confirmButton = {})
}

@Composable
private fun TimerConfirmDialog(title: String, message: String, confirmText: String, dismissText: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = SurfaceColor,
        title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
        text = { Text(message, color = OnSurfaceVariant) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText, color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(dismissText, color = PrimaryColor) } }
    )
}

@Composable
private fun TimerNoiseBottomSheet(selectedNoise: String?, onSelect: (String?) -> Unit, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onDismiss() })
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .background(SurfaceColor).padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(OutlineVariant).align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(4.dp))
        Text("اختار صوت هادئ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        if (selectedNoise != null) {
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFFF6B6B).copy(alpha = 0.1f)).clickable { onSelect(null) }.padding(12.dp),
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
                Text(name, color = if (selectedNoise == name) PrimaryColor else OnSurface, fontSize = 14.sp,
                    fontWeight = if (selectedNoise == name) FontWeight.Bold else FontWeight.Normal)
                if (selectedNoise == name) { Spacer(modifier = Modifier.weight(1f)); Icon(Icons.Default.VolumeUp, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp)) }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TimerModeBottomSheet(isCountingUp: Boolean, onSelectMode: (Boolean) -> Unit, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onDismiss() })
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .background(SurfaceColor).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(OutlineVariant).align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(4.dp))
        Text("وضع المؤقت", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        TimerModeOption(Icons.Default.HourglassBottom, "عد تنازلي", "25 دقيقة ← 0", !isCountingUp) { onSelectMode(false) }
        TimerModeOption(Icons.Default.HourglassTop, "عد تصاعدي", "0 ← ∞ بلا حد", isCountingUp) { onSelectMode(true) }
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
