package com.noteflow.app.features.notes.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.presentation.NoteViewModel

// VisualTransformation تلوّن [[...]] أثناء الكتابة
class LinkVisualTransformation(
    private val linkColor: androidx.compose.ui.graphics.Color
) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        val annotated = buildAnnotatedString {
            val regex = Regex("""\[\[(.+?)]]""")
            var lastIndex = 0
            regex.findAll(text.text).forEach { match ->
                append(text.text.substring(lastIndex, match.range.first))
                withStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.Bold)) {
                    append(match.value)
                }
                lastIndex = match.range.last + 1
            }
            if (lastIndex < text.text.length) append(text.text.substring(lastIndex))
        }
        return TransformedText(annotated, OffsetMapping.Identity)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Long = 0,
    onBack: () -> Unit,
    onNavigateToNote: (Long) -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val backlinks by viewModel.backlinks.collectAsState()
    val existing = remember(noteId, notes) { notes.find { it.id == noteId } }

    var title by remember(existing) { mutableStateOf(existing?.title ?: "") }
    var content by remember(existing) { mutableStateOf(existing?.content ?: "") }
    var isEditMode by remember { mutableStateOf(noteId == 0L) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    val error by viewModel.error.collectAsState()

    LaunchedEffect(title, content) {
        if (title.isNotBlank()) {
            viewModel.triggerAutoSave(title, content, noteId)
        }
    }

    LaunchedEffect(error) {
        if (error != null) viewModel.clearError()
    }

    LaunchedEffect(noteId, title) {
        if (noteId != 0L && title.isNotBlank()) {
            viewModel.loadBacklinks(title, noteId)
        }
    }

    // وضع القراءة — الأقواس بتختفي والكلمة بس بتبان كرابط
    val readModeContent = remember(content, notes, primaryColor, errorColor) {
        buildAnnotatedString {
            val regex = Regex("""\[\[(.+?)]]""")
            var lastIndex = 0
            regex.findAll(content).forEach { match ->
                append(content.substring(lastIndex, match.range.first))
                val linkTitle = match.groupValues[1]
                val linkedNote = notes.find { it.title == linkTitle }
                if (linkedNote != null) {
                    pushStringAnnotation("NOTE_LINK", linkedNote.id.toString())
                    withStyle(SpanStyle(
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    )) {
                        append(linkTitle) // الكلمة بس بدون [[]]
                    }
                    pop()
                } else {
                    withStyle(SpanStyle(
                        color = errorColor,
                        fontWeight = FontWeight.Bold
                    )) {
                        append(linkTitle) // الكلمة بس بدون [[]]
                    }
                }
                lastIndex = match.range.last + 1
            }
            if (lastIndex < content.length) append(content.substring(lastIndex))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isEditMode) {
                        BasicTextField(
                            value = title,
                            onValueChange = { title = it },
                            textStyle = TextStyle(
                                color = onSurfaceColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(primaryColor),
                            decorationBox = { inner ->
                                if (title.isEmpty()) {
                                    Text("العنوان", color = onSurfaceVariantColor)
                                }
                                inner()
                            }
                        )
                    } else {
                        Text(title.ifBlank { "بدون عنوان" })
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditMode && title.isNotBlank()) {
                            viewModel.saveNote(title, content, noteId)
                        }
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (isEditMode && title.isNotBlank()) {
                            viewModel.saveNote(title, content, noteId)
                        }
                        isEditMode = !isEditMode
                    }) {
                        Icon(
                            if (isEditMode) Icons.Default.MenuBook else Icons.Default.Edit,
                            contentDescription = if (isEditMode) "وضع القراءة" else "وضع التعديل"
                        )
                    }
                    if (noteId != 0L) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = errorColor)
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (isEditMode) {
                    // وضع التعديل — [[...]] بتتلون أثناء الكتابة
                    BasicTextField(
                        value = content,
                        onValueChange = { content = it },
                        textStyle = TextStyle(
                            color = onSurfaceColor,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        ),
                        cursorBrush = SolidColor(primaryColor),
                        visualTransformation = LinkVisualTransformation(primaryColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp),
                        decorationBox = { inner ->
                            if (content.isEmpty()) {
                                Text("ابدأ الكتابة...", color = onSurfaceVariantColor)
                            }
                            inner()
                        }
                    )
                } else {
                    // وضع القراءة — الأقواس بتختفي والكلمة بس بتبان
                    if (content.isBlank()) {
                        Text(
                            text = "لا يوجد محتوى",
                            style = MaterialTheme.typography.bodyLarge,
                            color = onSurfaceVariantColor
                        )
                    } else {
                        ClickableText(
                            text = readModeContent,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = onSurfaceColor,
                                lineHeight = 24.sp
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { offset ->
                                readModeContent.getStringAnnotations("NOTE_LINK", offset, offset)
                                    .firstOrNull()?.let { annotation ->
                                        onNavigateToNote(annotation.item.toLong())
                                    }
                            }
                        )
                    }
                }
            }

            // Backlinks
            if (backlinks.isNotEmpty()) {
                item {
                    Divider()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "الملاحظات اللي بتلينك هنا (${backlinks.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = primaryColor
                    )
                }
                items(backlinks) { note ->
                    OutlinedCard(
                        onClick = { onNavigateToNote(note.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(note.title, style = MaterialTheme.typography.bodyMedium)
                            if (note.content.isNotBlank()) {
                                Text(
                                    text = note.content.take(80) + if (note.content.length > 80) "..." else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = onSurfaceVariantColor
                                )
                            }
                        }
                    }
                }
            }

            if (error != null) {
                item {
                    Text(error!!, color = errorColor)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف الملاحظة") },
            text = { Text("متأكد إنك عايز تحذف \"$title\"؟") },
            confirmButton = {
                TextButton(onClick = {
                    existing?.let { viewModel.deleteNote(it) }
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("حذف", color = errorColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
