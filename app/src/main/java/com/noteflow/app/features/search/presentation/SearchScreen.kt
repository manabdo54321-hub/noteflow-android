package com.noteflow.app.features.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.tasks.domain.model.Task

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val TertiaryColor = Color(0xFF75D1FF)
private val OnSurface = Color(0xFFE5E2E1)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val OutlineVariant = Color(0xFF47464C)

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onNoteClick: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    var selectedFilter by remember { mutableStateOf("الكل") }
    val filters = listOf("الكل", "ملاحظات", "مهام")

    val showNotes = selectedFilter == "الكل" || selectedFilter == "ملاحظات"
    val showTasks = selectedFilter == "الكل" || selectedFilter == "مهام"

    val visibleNotes = if (showNotes) results.notes else emptyList()
    val visibleTasks = if (showTasks) results.tasks else emptyList()
    val totalResults = visibleNotes.size + visibleTasks.size

    Column(
        modifier = Modifier.fillMaxSize().background(BgColor)
    ) {
        SearchTopBar(
            query = query,
            onQueryChange = viewModel::onQueryChange,
            onBack = onBack
        )
        SearchFilterRow(
            filters = filters,
            selected = selectedFilter,
            onSelect = { selectedFilter = it }
        )
        SearchResultsList(
            query = query,
            totalResults = totalResults,
            showNotes = showNotes,
            showTasks = showTasks,
            notes = visibleNotes,
            tasks = visibleTasks,
            onNoteClick = onNoteClick
        )
    }
}

@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceColor)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = OnSurfaceVariant)
        }
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
                onValueChange = onQueryChange,
                textStyle = TextStyle(color = OnSurface, fontSize = 16.sp),
                cursorBrush = SolidColor(PrimaryColor),
                singleLine = true,
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text("ابحث...", color = OnSurfaceVariant, fontSize = 16.sp)
                    }
                    inner()
                }
            )
            if (query.isNotEmpty()) {
                Icon(Icons.Default.Close, contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(18.dp).clickable { onQueryChange("") })
            }
        }
    }
}

@Composable
private fun SearchFilterRow(
    filters: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) PrimaryColor.copy(alpha = 0.2f) else SurfaceColor
                    )
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(filter, fontSize = 13.sp,
                    color = if (isSelected) PrimaryColor else OnSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    query: String,
    totalResults: Int,
    showNotes: Boolean,
    showTasks: Boolean,
    notes: List<Note>,
    tasks: List<Task>,
    onNoteClick: (Long) -> Unit
) {
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null,
                            tint = OnSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("ابدأ الكتابة للبحث", color = OnSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
        } else if (totalResults == 0) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, contentDescription = null,
                            tint = OnSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("مفيش نتائج لـ \"$query\"",
                            color = OnSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
        } else {
            if (showNotes && notes.isNotEmpty()) {
                item { SearchSectionHeader("ملاحظات (${notes.size})", PrimaryColor, Icons.Default.Notes) }
                items(notes) { note -> NoteResultCard(note, query, onNoteClick) }
            }
            if (showTasks && tasks.isNotEmpty()) {
                item { SearchSectionHeader("مهام (${tasks.size})", TertiaryColor, Icons.Default.CheckCircle) }
                items(tasks) { task -> TaskResultCard(task) }
            }
        }
    }
}

@Composable
private fun SearchSectionHeader(title: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(title, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun NoteResultCard(note: Note, query: String, onNoteClick: (Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor)
            .clickable { onNoteClick(note.id) }
            .padding(16.dp)
    ) {
        Text(note.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
        if (note.content.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            val preview = if (note.content.contains(query, ignoreCase = true)) {
                val idx = note.content.indexOf(query, ignoreCase = true)
                val start = maxOf(0, idx - 30)
                val end = minOf(note.content.length, idx + query.length + 60)
                "...${note.content.substring(start, end)}..."
            } else note.content.take(80)
            Text(preview, fontSize = 13.sp, color = OnSurfaceVariant, lineHeight = 20.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Notes, contentDescription = null,
                tint = PrimaryColor.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
            Text("ملاحظة", fontSize = 11.sp, color = PrimaryColor.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun TaskResultCard(task: Task) {
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
            modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Box(modifier = Modifier.fillMaxSize().background(PrimaryColor),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Check, contentDescription = null,
                        tint = Color(0xFF1C0062), modifier = Modifier.size(13.dp))
                }
            } else {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = OutlineVariant,
                        style = Stroke(width = 1.dp.toPx()),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                }
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 15.sp,
                color = if (task.isCompleted) OnSurface.copy(alpha = 0.4f) else Color.White,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
            )
        }
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
