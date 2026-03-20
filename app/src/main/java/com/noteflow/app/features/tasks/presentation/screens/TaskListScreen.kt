package com.noteflow.app.features.tasks.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.presentation.NoteViewModel
import com.noteflow.app.features.tasks.domain.model.Task
import com.noteflow.app.features.tasks.presentation.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateToNote: (Long) -> Unit = {},
    taskViewModel: TaskViewModel = hiltViewModel(),
    noteViewModel: NoteViewModel = hiltViewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val notes by noteViewModel.notes.collectAsState()

    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }

    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var newTitle by remember { mutableStateOf("") }
    var selectedNoteId by remember { mutableStateOf<Long?>(null) }
    var showNotePicker by remember { mutableStateOf(false) }
    var showCompleted by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("المهام") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingTask = null
                newTitle = ""
                selectedNoteId = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "مهمة جديدة")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // المهام النشطة
            if (activeTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("مفيش مهام لسه ✨", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(activeTasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        linkedNoteName = notes.find { it.id == task.noteId }?.title,
                        onToggle = { taskViewModel.toggleComplete(task) },
                        onDelete = { taskToDelete = task },
                        onEdit = {
                            editingTask = task
                            newTitle = task.title
                            selectedNoteId = task.noteId
                            showDialog = true
                        },
                        onNoteClick = { task.noteId?.let { nid -> onNavigateToNote(nid) } }
                    )
                }
            }

            // سجل المكتملة
            if (completedTasks.isNotEmpty()) {
                item {
                    Divider()
                    TextButton(onClick = { showCompleted = !showCompleted }) {
                        Text(
                            if (showCompleted) "إخفاء المكتملة (${completedTasks.size})"
                            else "عرض المكتملة (${completedTasks.size})",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (showCompleted) {
                    items(completedTasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            linkedNoteName = notes.find { it.id == task.noteId }?.title,
                            onToggle = { taskViewModel.toggleComplete(task) },
                            onDelete = { taskToDelete = task },
                            onEdit = {
                                editingTask = task
                                newTitle = task.title
                                selectedNoteId = task.noteId
                                showDialog = true
                            },
                            onNoteClick = { task.noteId?.let { nid -> onNavigateToNote(nid) } }
                        )
                    }
                }
            }
        }
    }

    // Dialog إضافة/تعديل مهمة
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; newTitle = ""; selectedNoteId = null; editingTask = null },
            title = { Text(if (editingTask != null) "تعديل المهمة" else "مهمة جديدة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("اسم المهمة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    val linkedNote = notes.find { it.id == selectedNoteId }
                    OutlinedButton(
                        onClick = { showNotePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(linkedNote?.title ?: "ربط بملاحظة (اختياري)")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editingTask != null) {
                        taskViewModel.saveTask(newTitle, selectedNoteId, editingTask!!.id)
                    } else {
                        taskViewModel.saveTask(newTitle, selectedNoteId)
                    }
                    showDialog = false
                    newTitle = ""
                    selectedNoteId = null
                    editingTask = null
                }) { Text("حفظ") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    newTitle = ""
                    selectedNoteId = null
                    editingTask = null
                }) { Text("إلغاء") }
            }
        )
    }

    // Dialog اختيار ملاحظة
    if (showNotePicker) {
        AlertDialog(
            onDismissRequest = { showNotePicker = false },
            title = { Text("اختار ملاحظة") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    item {
                        TextButton(
                            onClick = { selectedNoteId = null; showNotePicker = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("بلاش ربط") }
                    }
                    items(notes) { note ->
                        TextButton(
                            onClick = { selectedNoteId = note.id; showNotePicker = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                note.title,
                                color = if (selectedNoteId == note.id)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Dialog تأكيد الحذف
    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("حذف المهمة") },
            text = { Text("متأكد إنك عايز تحذف \"${taskToDelete!!.title}\"؟") },
            confirmButton = {
                TextButton(onClick = {
                    taskViewModel.deleteTask(taskToDelete!!)
                    taskToDelete = null
                }) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    linkedNoteName: String?,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onNoteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = if (task.isCompleted)
                        MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough)
                    else
                        MaterialTheme.typography.bodyLarge
                )
                if (linkedNoteName != null) {
                    TextButton(
                        onClick = onNoteClick,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(linkedNoteName, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            if (task.pomodoroCount > 0) {
                Text("🍅 ${task.pomodoroCount}", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(4.dp))
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "تعديل",
                    modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "حذف",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp))
            }
        }
    }
}
