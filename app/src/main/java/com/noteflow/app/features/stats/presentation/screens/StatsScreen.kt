package com.noteflow.app.features.stats.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.presentation.NoteViewModel
import com.noteflow.app.features.stats.presentation.StatsViewModel
import com.noteflow.app.features.timer.presentation.TimerViewModel

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val TertiaryColor = Color(0xFF75D1FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val GreenColor = Color(0xFF4CAF50)

@Composable
fun StatsScreen(
    noteViewModel: NoteViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel(),
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val notes by noteViewModel.notes.collectAsState()
    val allTasks by statsViewModel.allTasks.collectAsState()
    val completedSessions by timerViewModel.completedSessions.collectAsState()
    val context = LocalContext.current
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    val completedTasks = allTasks.filter { it.isCompleted }
    val activeTasks = allTasks.filter { !it.isCompleted }
    val completionRate = if (allTasks.isEmpty()) 0f else completedTasks.size.toFloat() / allTasks.size.toFloat()
    val totalHours = (completedSessions * 25) / 60f
    val linkedNotes = notes.count { it.content.contains("[[") }

    LazyColumn(modifier = Modifier.fillMaxSize().background(BgColor), contentPadding = PaddingValues(bottom = 32.dp)) {
        item { StatsHeader() }
        item { StatsTitle() }
        item { StatsCompletionCard(completionRate, completedTasks.size) }
        item { StatsFocusCard(totalHours) }
        item { StatsNotesCard(notes.size, linkedNotes) }
        item { StatsFocusDistributionCard() }
        item { StatsOverviewCard(completionRate, activeTasks.size, completedTasks.size) }
        item { StatsVersionFooter(versionName) }
    }
}

@Composable
private fun StatsHeader() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp).statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Menu, contentDescription = null, tint = OnSurfaceVariant)
        Text("NoteFlow", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant)
    }
}

@Composable
private fun StatsTitle() {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text("إحصائيات", fontSize = 11.sp, letterSpacing = 2.sp, color = OnSurfaceVariant)
        Text("الأداء والإنتاجية", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SurfaceColor).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("آخر 7 أيام", fontSize = 13.sp, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun StatsCompletionCard(completionRate: Float, completedCount: Int) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor)) {
        Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("معدل الإنجاز", fontSize = 13.sp, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${"%.1f".format(completionRate * 100)}%", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column(horizontalAlignment = Alignment.End) {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(GreenColor.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("+$completedCount مكتملة", fontSize = 11.sp, color = GreenColor)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenColor, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun StatsFocusCard(totalHours: Float) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(AccentColor.copy(alpha = 0.6f), PrimaryColor.copy(alpha = 0.4f)))).padding(20.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("عالي الكثافة", fontSize = 11.sp, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("إجمالي ساعات التركيز", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                Text("${"%.1f".format(totalHours)}س", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun StatsNotesCard(notesCount: Int, linkedCount: Int) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor)) {
        Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("إنتاج الملاحظات", fontSize = 13.sp, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$notesCount ملاحظة", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column(horizontalAlignment = Alignment.End) {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(TertiaryColor.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("$linkedCount مترابطة", fontSize = 11.sp, color = TertiaryColor)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Icon(Icons.Default.Edit, contentDescription = null, tint = TertiaryColor, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun StatsFocusDistributionCard() {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("توزيع التركيز", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("ساعات العمل اليومية", fontSize = 12.sp, color = OnSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            val days = listOf("إث", "ثل", "أر", "خم", "جم", "سب", "أح")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                days.forEachIndexed { index, day ->
                    val height = (30 + (index * 15) % 80).dp
                    val isToday = index == 3
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.width(28.dp).height(height).clip(RoundedCornerShape(6.dp))
                            .background(if (isToday) Brush.verticalGradient(listOf(PrimaryColor, AccentColor)) else Brush.verticalGradient(listOf(SurfaceHigh, SurfaceHigh))))
                        Text(day, fontSize = 10.sp, color = if (isToday) PrimaryColor else OnSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsOverviewCard(completionRate: Float, activeCount: Int, completedCount: Int) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("نظرة عامة", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = completionRate, modifier = Modifier.fillMaxSize(),
                        color = PrimaryColor, trackColor = SurfaceHigh, strokeWidth = 8.dp)
                    Text("${(completionRate * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$activeCount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("نشطة", fontSize = 12.sp, color = OnSurfaceVariant)
                }
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFF47464C)))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$completedCount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("مكتملة", fontSize = 12.sp, color = OnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StatsVersionFooter(versionName: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceColor), contentAlignment = Alignment.Center) {
            Text("✦", fontSize = 20.sp, color = PrimaryColor)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("NOTEFLOW V$versionName", fontSize = 11.sp, letterSpacing = 2.sp, color = OnSurfaceVariant)
        Text("صُنع بدقة تحريرية", fontSize = 11.sp, color = OnSurfaceVariant.copy(alpha = 0.5f))
    }
}
