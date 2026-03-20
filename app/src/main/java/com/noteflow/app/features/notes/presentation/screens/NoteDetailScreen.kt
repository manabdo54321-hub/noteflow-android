package com.noteflow.app.features.notes.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.presentation.NoteViewModel

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

    val error by viewModel.error.collectAsState()

    LaunchedEffect(error) {
        if (error != null) viewModel.clearError()
    }

    LaunchedEffect(noteId, title) {
        if (noteId != 0L && title.isNotBlank()) {
            viewModel.loadBacklinks(title, noteId)
        }
    }

    // بناء النص مع الـ [[links]] قابلة للضغط
    val annotatedContent = remember(content, notes) {
        buildAnnotatedString {
            val regex = Regex("""\[\[(.+?)]]""")
            var lastIndex = 0
            regex.findAll(content).forEach { match ->
                append(content.substring(lastIndex, match.range.first))
                val linkTitle = match.groupValues[1]
                val linkedNote = notes.find { it.title == linkTitle }
                if (linkedNote != null) {
                    pushStringAnnotation("NOTE_LINK", linkedNote.id.toString())
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("[[${linkTitle}]]")
                    }
                    pop()
                } else {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
                        append("[[${linkTitle}]]")
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
                title = { Text(if (noteId == 0L) "ملاحظة جديدة" else "تعديل") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveNote(title, content, noteId)
                        onBack()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "حفظ")
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
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("العنوان") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("المحتوى") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    maxLines = Int.MAX_VALUE
                )
            }

            // عرض النص مع الـ links قابلة للضغط
            if (noteId != 0L && content.contains("[[")) {
                item {
                    Text(
                        text = "معاينة الروابط",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ClickableText(
                        text = annotatedContent,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        onClick = { offset ->
                            annotatedContent.getStringAnnotations("NOTE_LINK", offset, offset)
                                .firstOrNull()?.let { annotation ->
                                    onNavigateToNote(annotation.item.toLong())
                                }
                        }
                    )
                }
            }

            // قائمة الـ Backlinks
            if (backlinks.isNotEmpty()) {
                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الملاحظات اللي بتلينك هنا (${backlinks.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(backlinks) { note ->
                    OutlinedCard(
                        onClick = { onNavigateToNote(note.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (note.content.isNotBlank()) {
                                Text(
                                    text = note.content.take(80) + if (note.content.length > 80) "..." else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (error != null) {
                item {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
