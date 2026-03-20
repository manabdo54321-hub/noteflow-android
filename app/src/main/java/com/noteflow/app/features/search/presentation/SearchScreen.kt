package com.noteflow.app.features.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.presentation.NoteViewModel
import com.noteflow.app.features.stats.presentation.StatsViewModel

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val OnSurface = Color(0xFFE5E2E1)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val OutlineVariant = Color(0xFF47464C)
private val TertiaryColor = Color(0xFF75D1FF)

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onNoteClick: (Long) -> Unit,
    noteViewModel: NoteViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel()
) {
    val notes by noteViewModel.notes.collectAsState()
    val allTasks by statsViewModel.allTasks.collectAsState()

    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }
    val filters = listOf("الكل", "ملاحظات", "مهام")

    val filteredNotes = if (query.isBlank()) emptyList()
        else notes.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.content.contains(query, ignoreCase = true)
        }

    val filteredTasks = if (query.isBlank()) emptyList()
        else allTasks.filter {
            it.title.contains(query, ignoreCase = true)
        }

    val showNotes = selectedFilter == "الكل" || selectedFilter == "ملاحظات"
    val showTasks = selectedFilter == "الكل" || selectedFilter == "مهام"

    val totalResults = (if (showNotes) filteredNotes.size else 0) +
            (if (showTasks) filteredTasks.size else 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1C1B1B))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null,
                    tint = OnSurfaceVariant)
            }

            // Search Field
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceHigh)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null,
                    tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(color = OnSurface, fontSize = 16.sp),
                    cursorBrush = SolidColor(PrimaryColor),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text("ابحث في كل حاجة...",
                                color = OnSurfaceVariant, fontSize = 16.sp)
                        }
                        inner()
                    }
                )
                if (query.isNotEmpty()) {
                    Icon(Icons.Default.Close, contentDescription = null,
                        tint = OnSurfaceVariant, modifier = Modifier.size(18.dp)
                            .clickable { query = "" })
                }
            }
        }

        // Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) PrimaryColor.copy(alpha = 0.2f)
                            else SurfaceColor
                        )
                        .then(
                            if (isSelected) Modifier
                            else Modifier
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(filter, fontSize = 13.sp,
                        color = if (isSelected) PrimaryColor else OnSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        // Results
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (query.isBlank()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.Search, contentDescription = null,
                                tint = OutlineVariant, modifier = Modifier.size(48.dp))
                            Text("ابحث في ملاحظاتك ومهامك",
                                color = OutlineVariant, fontSize = 14.sp)
                        }
                    }
                }
            } else if (totalResults == 0) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("🔍", fontSize = 48.sp)
                            Text("لا توجد نتائج لـ \"$query\"",
                                color = OutlineVariant, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                // عدد النتائج
                item {
                    Text("$totalResults نتيجة",
                        fontSize = 12.sp, color = OutlineVariant,
                        modifier = Modifier.padding(vertical = 4.dp))
                }

                // الملاحظات
                if (showNotes && filteredNotes.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Notes, contentDescription = null,
                                tint = PrimaryColor, modifier = Modifier.size(16.dp))
                            Text("ملاحظات (${filteredNotes.size})",
                                fontSize = 12.sp, color = PrimaryColor,
                                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                    items(filteredNotes) { note ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceColor)
                                .clickable { onNoteClick(note.id) }
                                .padding(16.dp)
                        ) {
                            // Highlight العنوان
                            Text(
                                text = note.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            if (note.content.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                // بيظهر الجزء اللي فيه الكلمة
                                val contentPreview = if (note.content.contains(query, ignoreCase = true)) {
                                    val idx = note.content.indexOf(query, ignoreCase = true)
                                    val start = maxOf(0, idx - 30)
                                    val end = minOf(note.content.length, idx + query.length + 60)
                                    "...${note.content.substring(start, end)}..."
                                } else {
                                    note.content.take(80)
                                }
                                Text(contentPreview, fontSize = 13.sp,
                                    color = OnSurfaceVariant, lineHeight = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Notes, contentDescription = null,
                                    tint = PrimaryColor.copy(alpha = 0.6f),
                                    modifier = Modifier.size(12.dp))
                                Text("ملاحظة", fontSize = 11.sp,
                                    color = PrimaryColor.copy(alpha = 0.6f))
                            }
                        }
                    }
                }

                // المهام
                if (showTasks && filteredTasks.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                tint = TertiaryColor, modifier = Modifier.size(16.dp))
                            Text("مهام (${filteredTasks.size})",
                                fontSize = 12.sp, color = TertiaryColor,
                                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                    items(filteredTasks) { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceColor)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (task.isCompleted) PrimaryColor
                                        else Color.Transparent
                                    )
                                    .then(
                                        if (!task.isCompleted)
                                            Modifier.background(Color.Transparent)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (task.isCompleted) {
                                    Icon(Icons.Default.Check, contentDescription = null,
                                        tint = Color(0xFF1C0062),
                                        modifier = Modifier.size(13.dp))
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.Transparent)
                                            .then(
                                                Modifier.background(
                                                    Color.Transparent
                                                )
                                            )
                                    ) {
                                        androidx.compose.foundation.Canvas(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            drawRoundRect(
                                                color = OutlineVariant,
                                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                                    width = 1.dp.toPx()
                                                ),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                                    4.dp.toPx()
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontSize = 15.sp,
                                    color = if (task.isCompleted)
                                        OnSurface.copy(alpha = 0.4f) else Color.White,
                                    textDecoration = if (task.isCompleted)
                                        TextDecoration.LineThrough else null
                                )
                            }
                            // Priority badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TertiaryColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("مهمة", fontSize = 10.sp, color = TertiaryColor)
                            }
                        }
                    }
                }
            }
        }
    }
}
