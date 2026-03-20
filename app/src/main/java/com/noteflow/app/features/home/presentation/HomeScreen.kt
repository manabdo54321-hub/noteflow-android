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

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val TertiaryColor = Color(0xFF75D1FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val OutlineVariant = Color(0xFF47464C)

@OptIn(ExperimentalMaterial3Api::class)
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

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "صباح الخير"
        in 12..17 -> "مساء النور"
        else -> "مساء الخير"
    }

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
                Icon(Icons.Default.Menu, contentDescription = null,
                    tint = OnSurfaceVariant)
                Text("NoteFlow", fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = Color.White)
                Icon(Icons.Default.Search, contentDescription = null,
                    tint = OnSurfaceVariant)
            }
        }

        // التحية
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
                    text = "لديك ${activeTasks.size} مهام للإنجاز اليوم.",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant
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
                    Text(
                        text = "الإتقان اليومي",
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        color = PrimaryColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(completionRate * 100).toInt()}% مكتمل",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Progress Bar
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
                            Text("${completedSessions * 25} د",
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
                    // ملاحظة جديدة
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceColor)
                            .clickable { onAddNote() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null,
                            tint = PrimaryColor, modifier = Modifier.size(20.dp))
                        Text("ملاحظة جديدة", color = Color.White, fontSize = 15.sp)
                    }
                    // مهمة جديدة
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceColor)
                            .clickable { onNavigateToTasks() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null,
                            tint = TertiaryColor, modifier = Modifier.size(20.dp))
                        Text("مهمة جديدة", color = Color.White, fontSize = 15.sp)
                    }
                    // جلسة تركيز
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onNoteClick(note.id) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(PrimaryColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(note.title, fontWeight = FontWeight.Bold,
                                color = Color.White, fontSize = 15.sp)
                            if (note.content.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    note.content.take(80) +
                                        if (note.content.length > 80) "..." else "",
                                    fontSize = 13.sp,
                                    color = OnSurfaceVariant
                                )
                            }
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
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.size(18.dp),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp, OutlineVariant)
                            ) {}
                        }
                    }
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) OnSurfaceVariant else Color.White,
                        fontSize = 15.sp,
                        textDecoration = if (task.isCompleted)
                            TextDecoration.LineThrough else null
                    )
                }
            }
        }
    }
}
