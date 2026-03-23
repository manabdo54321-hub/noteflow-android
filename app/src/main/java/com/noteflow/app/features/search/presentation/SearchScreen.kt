package com.noteflow.app.features.search.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noteflow.app.features.notes.data.local.NoteEntity
import com.noteflow.app.features.tasks.data.local.TaskEntity
import java.text.SimpleDateFormat
import java.util.*

// ── الألوان ──────────────────────────────────────
private val BgColor       = Color(0xFF131313)
private val SurfaceColor  = Color(0xFF1C1B1B)
private val SurfaceHigh   = Color(0xFF2A2A2A)
private val PrimaryColor  = Color(0xFFCABEFF)
private val AccentColor   = Color(0xFF8A70FF)
private val TertiaryColor = Color(0xFF75D1FF)
private val TextPrimary   = Color(0xFFE8E8E8)
private val TextSecondary = Color(0xFF9A9A9A)

// ── 1. الشاشة الرئيسية ───────────────────────────
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNoteClick: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query        by viewModel.query.collectAsStateWithLifecycle()
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
    val noteResults  by viewModel.noteResults.collectAsStateWithLifecycle()
    val taskResults  by viewModel.taskResults.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        SearchTopBar(
            query          = query,
            onQueryChange  = viewModel::onQueryChange,
            onClearQuery   = viewModel::clearQuery,
            onNavigateBack = onNavigateBack,
            focusRequester = focusRequester
        )

        AnimatedVisibility(visible = query.length >= 2) {
            SearchFilterChips(
                activeFilter   = activeFilter,
                noteCount      = noteResults.size,
                taskCount      = taskResults.size,
                onFilterChange = viewModel::onFilterChange
            )
        }

        val showEmpty = query.length >= 2
            && noteResults.isEmpty()
            && taskResults.isEmpty()

        if (showEmpty) {
            SearchEmptyState(query = query)
        } else {
            SearchResultsContent(
                query        = query,
                activeFilter = activeFilter,
                noteResults  = noteResults,
                taskResults  = taskResults,
                onNoteClick  = onNoteClick
            )
        }
    }
}

// ── 2. شريط البحث ────────────────────────────────
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onNavigateBack: () -> Unit,
    focusRequester: FocusRequester
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "رجوع",
                tint = TextSecondary
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .background(SurfaceColor, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "ابحث في الملاحظات والمهام...",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(PrimaryColor),
                    singleLine = true
                )
            }
            AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                IconButton(onClick = onClearQuery, modifier = Modifier.size(20.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "مسح",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ── 3. أزرار الفلترة ─────────────────────────────
@Composable
fun SearchFilterChips(
    activeFilter: String,
    noteCount: Int,
    taskCount: Int,
    onFilterChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            Triple("all",   "الكل",    noteCount + taskCount),
            Triple("notes", "ملاحظات", noteCount),
            Triple("tasks", "مهام",    taskCount)
        ).forEach { (key, label, count) ->
            val selected = activeFilter == key
            Box(
                modifier = Modifier
                    .background(
                        if (selected) AccentColor.copy(alpha = 0.2f) else SurfaceColor,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onFilterChange(key) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (count > 0) "$label ($count)" else label,
                    color = if (selected) PrimaryColor else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(SurfaceHigh)
    )
}

// ── 4. قائمة النتائج ─────────────────────────────
@Composable
fun SearchResultsContent(
    query: String,
    activeFilter: String,
    noteResults: List<NoteEntity>,
    taskResults: List<TaskEntity>,
    onNoteClick: (Long) -> Unit
) {
    val showNotes = activeFilter == "all" || activeFilter == "notes"
    val showTasks = activeFilter == "all" || activeFilter == "tasks"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        if (showNotes && noteResults.isNotEmpty()) {
            item {
                SearchSectionLabel(
                    icon  = Icons.Default.Article,
                    label = "ملاحظات",
                    color = PrimaryColor
                )
            }
            items(noteResults, key = { it.id }) { note ->
                SearchNoteItem(note = note, query = query, onNoteClick = onNoteClick)
            }
        }
        if (showTasks && taskResults.isNotEmpty()) {
            item {
                SearchSectionLabel(
                    icon  = Icons.Default.CheckCircleOutline,
                    label = "مهام",
                    color = TertiaryColor
                )
            }
            items(taskResults, key = { it.id }) { task ->
                SearchTaskItem(task = task, query = query)
            }
        }
    }
}

@Composable
private fun SearchSectionLabel(icon: ImageVector, label: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color.copy(alpha = 0.7f),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = color.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        )
    }
}

// ── 5. بطاقة ملاحظة ──────────────────────────────
@Composable
fun SearchNoteItem(
    note: NoteEntity,
    query: String,
    onNoteClick: (Long) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("d MMM", Locale("ar")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNoteClick(note.id) }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = highlightQuery(note.title.ifEmpty { "بدون عنوان" }, query),
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = dateFormat.format(Date(note.updatedAt)),
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        if (note.content.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = highlightQuery(note.content.take(120), query),
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(0.5.dp)
            .background(SurfaceHigh.copy(alpha = 0.6f))
    )
}

// ── 6. بطاقة مهمة ────────────────────────────────
@Composable
fun SearchTaskItem(task: TaskEntity, query: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (task.isCompleted)
                Icons.Default.CheckCircle
            else
                Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (task.isCompleted)
                TertiaryColor.copy(alpha = 0.6f)
            else
                TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = highlightQuery(task.title, query),
            color = if (task.isCompleted) TextSecondary else TextPrimary,
            fontSize = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(0.5.dp)
            .background(SurfaceHigh.copy(alpha = 0.6f))
    )
}

// ── 7. حالة لا يوجد نتائج ────────────────────────
@Composable
fun SearchEmptyState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = TextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(52.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "لا نتائج لـ \"$query\"",
            color = TextSecondary,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "جرّب كلمة مختلفة",
            color = TextSecondary.copy(alpha = 0.5f),
            fontSize = 12.sp
        )
    }
}

// ── دالة تظليل كلمة البحث ────────────────────────
@Composable
private fun highlightQuery(text: String, query: String): AnnotatedString {
    if (query.length < 2) return buildAnnotatedString { append(text) }
    return buildAnnotatedString {
        val lower = text.lowercase()
        val lowerQuery = query.lowercase()
        var start = 0
        while (start < text.length) {
            val idx = lower.indexOf(lowerQuery, start)
            if (idx == -1) {
                append(text.substring(start))
                break
            }
            append(text.substring(start, idx))
            withStyle(
                SpanStyle(
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    background = PrimaryColor.copy(alpha = 0.12f)
                )
            ) {
                append(text.substring(idx, idx + query.length))
            }
            start = idx + query.length
        }
    }
}
