package com.noteflow.app.features.goals.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.noteflow.app.features.goals.domain.model.Goal

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val TertiaryColor = Color(0xFF75D1FF)
private val HighPriorityColor = Color(0xFFFF6B6B)
private val GreenColor = Color(0xFF4CAF50)

@Composable
fun GoalsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Menu, contentDescription = null, tint = OnSurfaceVariant)
        Text("NoteFlow", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(AccentColor),
            contentAlignment = Alignment.Center
        ) {
            Text("أ", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GoalsTitle(activeCount: Int) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text("الأهداف", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        Text("لديك $activeCount هدف نشط.", fontSize = 14.sp, color = OnSurfaceVariant)
    }
}

@Composable
fun GoalsSummaryRow(goals: List<Goal>) {
    val total = goals.size
    val completed = goals.count { it.isCompleted }
    val avgProgress = if (goals.isNotEmpty()) goals.map { it.progress }.average().toInt() else 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GoalsSummaryCard("الكل", total.toString(), TertiaryColor, Modifier.weight(1f))
        GoalsSummaryCard("مكتمل", completed.toString(), GreenColor, Modifier.weight(1f))
        GoalsSummaryCard("التقدم", "$avgProgress%", PrimaryColor, Modifier.weight(1f))
    }
}

@Composable
private fun GoalsSummaryCard(label: String, value: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = OnSurfaceVariant)
        }
    }
}

@Composable
fun GoalsSectionLabel(label: String, color: Color) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun GoalsEmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎯", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("لا توجد أهداف بعد", color = OnSurfaceVariant, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("اضغط + لإضافة هدف جديد", color = OnSurfaceVariant.copy(alpha = 0.6f), fontSize = 13.sp)
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit,
    onProgressChange: (Int) -> Unit
) {
    var showSlider by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor)
            .then(
                if (goal.isCompleted)
                    Modifier.border(1.dp, GreenColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                else
                    Modifier.border(1.dp, AccentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            )
            .padding(16.dp)
    ) {
        GoalCardTopRow(goal, onEdit, onDelete, onToggleComplete)
        if (goal.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(goal.description, fontSize = 13.sp, color = OnSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(12.dp))
        GoalProgressRow(goal, showSlider, onProgressChange) { showSlider = !showSlider }
        if (goal.targetDate != null) {
            Spacer(modifier = Modifier.height(8.dp))
            GoalDateRow(goal.targetDate)
        }
    }
}

@Composable
private fun GoalCardTopRow(
    goal: Goal,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (goal.isCompleted)
                        Brush.linearGradient(listOf(GreenColor, TertiaryColor))
                    else
                        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                )
                .border(1.5.dp, if (goal.isCompleted) Color.Transparent else Color(0xFF47464C), CircleShape)
                .clickable { onToggleComplete() },
            contentAlignment = Alignment.Center
        ) {
            if (goal.isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = goal.title,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (goal.isCompleted) OnSurfaceVariant else Color.White,
            textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else null
        )
        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = HighPriorityColor.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun GoalProgressRow(
    goal: Goal,
    showSlider: Boolean,
    onProgressChange: (Int) -> Unit,
    onToggleSlider: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SurfaceHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(goal.progress / 100f)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(listOf(AccentColor, PrimaryColor))
                        )
                )
            }
            Text(
                "${goal.progress}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
                modifier = Modifier.clickable { onToggleSlider() }
            )
        }
        if (showSlider && !goal.isCompleted) {
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = goal.progress.toFloat(),
                onValueChange = { onProgressChange(it.toInt()) },
                valueRange = 0f..100f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryColor,
                    activeTrackColor = AccentColor,
                    inactiveTrackColor = SurfaceHigh
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GoalDateRow(targetDate: Long) {
    val formatted = remember(targetDate) {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        sdf.format(java.util.Date(targetDate))
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(Icons.Default.DateRange, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(12.dp))
        Text(formatted, fontSize = 11.sp, color = OnSurfaceVariant)
    }
}
