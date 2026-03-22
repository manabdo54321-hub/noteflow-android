package com.noteflow.app.features.tasks.presentation.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.notes.presentation.NoteViewModel
import com.noteflow.app.features.tasks.domain.model.Task
import com.noteflow.app.features.tasks.domain.model.TaskPriority
import com.noteflow.app.features.tasks.presentation.TaskViewModel

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val HighPriorityColor = Color(0xFFFF6B6B)
private val MediumPriorityColor = Color(0xFFFFBA00)
private val TertiaryColor = Color(0xFF75D1FF)

@Composable
fun TaskListScreen(
    onNavigateToNote: (Long) -> Unit = {},
    taskViewModel: TaskViewModel = hiltViewModel(),
    noteViewModel: NoteViewModel = hiltViewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val notes by noteViewModel.notes.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var newTitle by remember { mutableStateOf("") }
    var selectedNoteId by remember { mutableStateOf<Long?>(null) }
    var showNotePicker by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }
    val highPriority = activeTasks.filter { it.priority == TaskPriority.HIGH }
    val routineTasks = activeTasks.filter { it.priority != TaskPriority.HIGH }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 100.dp)) {
            item { TaskListHeader() }
            item { TaskListTitle(activeTasks.size) }
            item { TaskListTabs(selectedTab) { selectedTab = it } }
            when (selectedTab) {
                0 -> {
                    if (highPriority.isNotEmpty()) {
                        item { TaskSectionLabel("أولوية عالية", HighPriorityColor) }
                        items(highPriority, key = { it.id }) { task ->
                            TaskCard(task = task,
                                linkedNoteName = notes.find { it.id == task.noteId }?.title,
                                badge = "وقت محدود", badgeColor = HighPriorityColor, isHighPriority = true,
                                onToggle = { taskViewModel.toggleComplete(task) },
                                onDelete = { taskToDelete = task },
                                onEdit = { editingTask = task; newTitle = task.title; selectedNoteId = task.noteId; showDialog = true },
                                onNoteClick = { task.noteId?.let { onNavigateToNote(it) } })
                        }
                    }
                    if (routineTasks.isNotEmpty()) {
                        item { TaskSectionLabel("روتين", MediumPriorityColor) }
                        items(routineTasks, key = { it.id }) { task ->
                            val badge = when (task.priority) { TaskPriority.MEDIUM -> "عمل"; TaskPriority.LOW -> "شخصي"; else -> null }
                            val badgeColor = when (task.priority) { TaskPriority.MEDIUM -> TertiaryColor; else -> OnSurfaceVariant }
                            TaskCard(task = task,
                                linkedNoteName = notes.find { it.id == task.noteId }?.title,
                                badge = badge, badgeColor = badgeColor, isHighPriority = false,
                                onToggle = { taskViewModel.toggleComplete(task) },
                                onDelete = { taskToDelete = task },
                                onEdit = { editingTask = task; newTitle = task.title; selectedNoteId = task.noteId; showDialog = true },
                                onNoteClick = { task.noteId?.let { onNavigateToNote(it) } })
                        }
                    }
                    if (activeTasks.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🎉", fontSize = 48.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("كل المهام مكتملة!", color = OnSurfaceVariant)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                            Text("لا توجد مهام قادمة", color = OnSurfaceVariant)
                        }
                    }
                }
                2 -> {
                    if (completedTasks.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                                Text("لا توجد مهام مكتملة", color = OnSurfaceVariant)
                            }
                        }
                    } else {
                        items(completedTasks, key = { it.id }) { task ->
                            TaskCard(task = task,
                                linkedNoteName = notes.find { it.id == task.noteId }?.title,
                                badge = null, badgeColor = Color.Transparent, isHighPriority = false,
                                onToggle = { taskViewModel.toggleComplete(task) },
                                onDelete = { taskToDelete = task },
                                onEdit = {},
                                onNoteClick = { task.noteId?.let { onNavigateToNote(it) } })
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp)
            .size(56.dp).clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(PrimaryColor, AccentColor)))
            .clickable { editingTask = null; newTitle = ""; selectedNoteId = null; showDialog = true },
            contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF1C0062), modifier = Modifier.size(28.dp))
        }
    }

    if (showDialog) {
        TaskAddEditDialog(
            editingTask = editingTask, newTitle = newTitle, notes = notes, selectedNoteId = selectedNoteId,
            onTitleChange = { newTitle = it },
            onShowNotePicker = { showNotePicker = true },
            onConfirm = {
                if (editingTask != null) taskViewModel.saveTask(newTitle, selectedNoteId, editingTask!!.id)
                else taskViewModel.saveTask(newTitle, selectedNoteId)
                showDialog = false; newTitle = ""; selectedNoteId = null; editingTask = null
            },
            onDismiss = { showDialog = false; newTitle = ""; selectedNoteId = null; editingTask = null }
        )
    }
    if (showNotePicker) {
        TaskNotePickerDialog(notes = notes, selectedNoteId = selectedNoteId,
            onSelect = { selectedNoteId = it; showNotePicker = false },
            onDismiss = { showNotePicker = false })
    }
    if (taskToDelete != null) {
        TaskDeleteDialog(task = taskToDelete!!,
            onConfirm = { taskViewModel.deleteTask(taskToDelete!!); taskToDelete = null },
            onDismiss = { taskToDelete = null })
    }
}

@Composable
private fun TaskListHeader() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp).statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Menu, contentDescription = null, tint = OnSurfaceVariant)
        Text("NoteFlow", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant)
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(AccentColor), contentAlignment = Alignment.Center) {
                Text("أ", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TaskListTitle(activeCount: Int) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text("المهام", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        Text("لديك $activeCount مهام متبقية اليوم.", fontSize = 14.sp, color = OnSurfaceVariant)
    }
}

@Composable
private fun TaskListTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("اليوم", "القادمة", "المكتملة")
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
        .clip(RoundedCornerShape(12.dp)).background(SurfaceColor).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        tabs.forEachIndexed { index, tab ->
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                .background(if (selectedTab == index) Brush.horizontalGradient(listOf(PrimaryColor, AccentColor)) else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent)))
                .clickable { onTabSelected(index) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center) {
                Text(tab, fontSize = 13.sp,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == index) Color(0xFF1C0062) else OnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TaskSectionLabel(label: String, color: Color) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun TaskAddEditDialog(editingTask: Task?, newTitle: String, notes: List<Note>, selectedNoteId: Long?,
    onTitleChange: (String) -> Unit, onShowNotePicker: () -> Unit, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = SurfaceColor,
        title = { Text(if (editingTask != null) "تعديل المهمة" else "مهمة جديدة", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = newTitle, onValueChange = onTitleChange,
                    label = { Text("اسم المهمة") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryColor, unfocusedBorderColor = OnSurfaceVariant))
                OutlinedButton(onClick = onShowNotePicker, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor)) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(notes.find { it.id == selectedNoteId }?.title ?: "ربط بملاحظة")
                }
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("حفظ", color = PrimaryColor) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء", color = OnSurfaceVariant) } }
    )
}

@Composable
private fun TaskNotePickerDialog(notes: List<Note>, selectedNoteId: Long?, onSelect: (Long?) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = SurfaceColor,
        title = { Text("اختار ملاحظة", color = Color.White) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                item { TextButton(onClick = { onSelect(null) }, modifier = Modifier.fillMaxWidth()) { Text("بلاش ربط", color = OnSurfaceVariant) } }
                items(notes) { note ->
                    TextButton(onClick = { onSelect(note.id) }, modifier = Modifier.fillMaxWidth()) {
                        Text(note.title, color = if (selectedNoteId == note.id) PrimaryColor else Color.White)
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun TaskDeleteDialog(task: Task, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = SurfaceColor,
        title = { Text("حذف المهمة", color = Color.White) },
        text = { Text("متأكد إنك عايز تحذف \"${task.title}\"؟", color = OnSurfaceVariant) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("حذف", color = HighPriorityColor) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء", color = OnSurfaceVariant) } }
    )
}

@Composable
fun TaskCard(task: Task, linkedNoteName: String?, badge: String?, badgeColor: Color, isHighPriority: Boolean,
    onToggle: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit, onNoteClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
        .clip(RoundedCornerShape(12.dp)).background(SurfaceColor)
        .then(if (isHighPriority) Modifier.border(1.dp, HighPriorityColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)) else Modifier)
        .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        if (isHighPriority) {
            Box(modifier = Modifier.width(3.dp).height(40.dp).clip(RoundedCornerShape(2.dp)).background(HighPriorityColor))
            Spacer(modifier = Modifier.width(12.dp))
        }
        Box(modifier = Modifier.size(22.dp).clip(CircleShape)
            .background(if (task.isCompleted) Brush.linearGradient(listOf(PrimaryColor, AccentColor)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
            .border(1.5.dp, if (task.isCompleted) Color.Transparent else Color(0xFF47464C), CircleShape)
            .clickable { onToggle() }, contentAlignment = Alignment.Center) {
            if (task.isCompleted) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1C0062), modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(task.title, color = if (task.isCompleted) Color(0xFF929097) else Color.White,
                fontSize = 15.sp, fontWeight = FontWeight.Medium,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null)
            if (linkedNoteName != null) {
                Row(modifier = Modifier.clickable { onNoteClick() }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = TertiaryColor, modifier = Modifier.size(12.dp))
                    Text(linkedNoteName, fontSize = 11.sp, color = TertiaryColor)
                }
            }
            if (task.pomodoroCount > 0) Text("🍅 ${task.pomodoroCount}", fontSize = 11.sp, color = Color(0xFF929097))
        }
        if (badge != null) {
            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(badgeColor.copy(alpha = 0.15f))
                .border(1.dp, badgeColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(badge, fontSize = 10.sp, color = badgeColor, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
        if (!task.isCompleted) {
            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF929097), modifier = Modifier.size(16.dp))
            }
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = HighPriorityColor.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
        }
    }
}
