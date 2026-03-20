package com.noteflow.app.features.home.presentation

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.presentation.NoteViewModel
import com.noteflow.app.features.stats.presentation.StatsViewModel
import com.noteflow.app.features.timer.presentation.TimerViewModel
import java.util.Calendar
import java.util.concurrent.TimeUnit

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val TertiaryColor = Color(0xFF75D1FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)

@Composable
fun HomeScreen(
    onNoteClick: (Long) -> Unit,
    onAddNote: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToTasks: () -> Unit,
    noteViewModel: NoteViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel(),
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val notes by noteViewModel.notes.collectAsState()
    val allTasks by statsViewModel.allTasks.collectAsState()
    val completedSessions by timerViewModel.completedSessions.collectAsState()

    val activeTasks = allTasks.filter { !it.isCompleted }
    val completedTasks = allTasks.filter { it.isCompleted }
    val completionRate = if (allTasks.isEmpty()) 0f
        else completedTasks.size.toFloat() / allTasks.size.toFloat()

    val totalFocusMinutes = completedSessions * 25
    val goalMinutes = 5 * 60 // 5 ساعات
    val remainingMinutes = maxOf(0, goalMinutes - totalFocusMinutes)
    val remainingHours = remainingMinutes / 60
    val remainingMins = remainingMinutes % 60

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "صباح الخير"
        in 12..17 -> "مساء النور"
        else -> "مساء الخير"
    }

    val focusStreak = completedSessions / 4 // كل 4 جلسات = يوم

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // TopAppBar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = OnSurfaceVariant)
                Text("NoteFlow", fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = Color.White)
                Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant)
            }
        }

        // التحية + Streak
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "$greeting 👋",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 40.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "لديك ${activeTasks.size} مهام للإنجاز اليوم. " +
                        "streak التركيز الحالي $focusStreak أيام.",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }

        // Daily Mastery Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("الإتقان اليومي", fontSize = 11.sp,
                        letterSpacing = 2.sp, color = PrimaryColor,
                        fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(completionRate * 100).toInt()}% مكتمل",
                        fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(SurfaceHigh)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(completionRate)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(PrimaryColor, AccentColor))
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        Column {
                            Text("${completedTasks.size} / ${allTasks.size}",
                                fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                color = Color.White)
                            Text("المهام", fontSize = 11.sp, color = OnSurfaceVariant)
                        }
                        Column {
                            val hours = totalFocusMinutes / 60
                            val mins = totalFocusMinutes % 60
                            Text("${hours}س ${mins}د",
                                fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                color = Color.White)
                            Text("وقت التركيز", fontSize = 11.sp, color = OnSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Quick Actions
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("إجراءات سريعة", fontSize = 11.sp,
                    letterSpacing = 2.sp, color = OnSurfaceVariant,
                    fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickActionRow(
                        icon = Icons.Default.Edit,
                        iconColor = PrimaryColor,
                        label = "ملاحظة جديدة",
                        bgColor = SurfaceColor,
                        onClick = onAddNote
                    )
                    QuickActionRow(
                        icon = Icons.Default.CheckCircle,
                        iconColor = TertiaryColor,
                        label = "مهمة جديدة",
                        bgColor = SurfaceColor,
                        onClick = onNavigateToTasks
                    )
                    // Focus Session - مميزة
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(AccentColor.copy(alpha = 0.3f),
                                        PrimaryColor.copy(alpha = 0.2f))
                                )
                            )
                            .clickable { onNavigateToTimer() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null,
                            tint = PrimaryColor, modifier = Modifier.size(20.dp))
                        Text("جلسة تركيز", color = PrimaryColor,
                            fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recent Notes
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("آخر الملاحظات", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Text("عرض الكل", fontSize = 13.sp, color = PrimaryColor,
                    modifier = Modifier.clickable { })
            }
        }

        items(notes.take(3)) { note ->
            val timeAgo = getTimeAgo(note.createdAt)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor)
                    .clickable { onNoteClick(note.id) }
                    .padding(16.dp)
            ) {
                Text(timeAgo, fontSize = 11.sp,
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(PrimaryColor)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(note.title, fontWeight = FontWeight.Bold,
                            color = Color.White, fontSize = 15.sp)
                        if (note.content.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                note.content.take(80) +
                                    if (note.content.length > 80) "..." else "",
                                fontSize = 13.sp, color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Today's Focus Tasks
        if (activeTasks.isNotEmpty()) {
            item {
                Text("مهام اليوم", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            items(activeTasks.take(3)) { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (task.isCompleted)
                                    Brush.linearGradient(listOf(PrimaryColor, AccentColor))
                                else Brush.linearGradient(
                                    listOf(Color.Transparent, Color.Transparent))
                            )
                            .then(
                                if (!task.isCompleted) Modifier.padding(1.dp) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null,
                                tint = Color(0xFF1C0062), modifier = Modifier.size(12.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                                    .padding(1.dp)
                            ) {
                                Surface(modifier = Modifier.fillMaxSize(),
                                    shape = CircleShape,
                                    color = Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp, Color(0xFF47464C))) {}
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            color = if (task.isCompleted) OnSurfaceVariant else Color.White,
                            fontSize = 15.sp,
                            textDecoration = if (task.isCompleted)
                                TextDecoration.LineThrough else null
                        )
                        Text(
                            text = when (task.priority) {
                                com.noteflow.app.features.tasks.domain.model.TaskPriority.HIGH ->
                                    "أولوية عالية"
                                com.noteflow.app.features.tasks.domain.model.TaskPriority.MEDIUM ->
                                    "صيانة"
                                else -> "عادي"
                            },
                            fontSize = 11.sp,
                            color = when (task.priority) {
                                com.noteflow.app.features.tasks.domain.model.TaskPriority.HIGH ->
                                    Color(0xFFFF6B6B)
                                else -> OnSurfaceVariant
                            }
                        )
                    }
                }
            }
        }

        // Daily Goal Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                TertiaryColor.copy(alpha = 0.15f),
                                AccentColor.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🚀", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الهدف اليومي: 5 ساعات عمل عميق",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TertiaryColor
                    )
                    Text(
                        text = if (remainingMinutes > 0)
                            "${remainingHours}س ${remainingMins}د متبقية للوصول لذروة أدائك."
                        else
                            "🎉 أنجزت هدفك اليومي!",
                        fontSize = 12.sp,
                        color = TertiaryColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    label: String,
    bgColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null,
            tint = iconColor, modifier = Modifier.size(20.dp))
        Text(label, color = Color.White, fontSize = 15.sp)
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "الآن"
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
            "منذ $mins دقيقة"
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "منذ $hours ساعة"
        }
        diff < TimeUnit.DAYS.toMillis(2) -> "أمس"
        else -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "منذ $days أيام"
        }
    }
}
