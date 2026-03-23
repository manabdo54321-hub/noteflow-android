package com.noteflow.app.features.notes.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.domain.model.Note
import com.noteflow.app.features.notes.presentation.NoteViewModel

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val OutlineVariant = Color(0xFF47464C)
private val ErrorColor = Color(0xFFFF6B6B)

class MarkdownVisualTransformation(
    private val primaryColor: Color,
    private val onSurface: Color
) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        val annotated = buildAnnotatedString {
            val lines = text.text.split("\n")
            lines.forEachIndexed { lineIndex, line ->
                when {
                    line.startsWith("# ") -> {
                        withStyle(SpanStyle(color = primaryColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)) { append(line) }
                    }
                    line.startsWith("## ") -> {
                        withStyle(SpanStyle(color = primaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)) { append(line) }
                    }
                    line.startsWith("- ") || line.startsWith("• ") -> {
                        withStyle(SpanStyle(color = onSurface)) { append("• ${line.substring(2)}") }
                    }
                    line.contains("[[") -> {
                        val regex = Regex("""\[\[(.+?)]]""")
                        var lastIndex = 0
                        regex.findAll(line).forEach { match ->
                            append(line.substring(lastIndex, match.range.first))
                            withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold)) { append(match.groupValues[1]) }
                            lastIndex = match.range.last + 1
                        }
                        if (lastIndex < line.length) append(line.substring(lastIndex))
                    }
                    line.contains("**") -> {
                        val parts = line.split("**")
                        parts.forEachIndexed { i, part ->
                            if (i % 2 == 1) {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = onSurface)) { append(part) }
                            } else {
                                withStyle(SpanStyle(color = onSurface)) { append(part) }
                            }
                        }
                    }
                    else -> { withStyle(SpanStyle(color = onSurface)) { append(line) } }
                }
                if (lineIndex < lines.size - 1) append("\n")
            }
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
    var content by remember(existing) { mutableStateOf(TextFieldValue(existing?.content ?: "")) }
    var isEditMode by remember { mutableStateOf(noteId == 0L) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val error by viewModel.error.collectAsState()

    LaunchedEffect(title, content) {
        if (title.isNotBlank()) viewModel.triggerAutoSave(title, content.text, noteId)
    }
    LaunchedEffect(error) { if (error != null) viewModel.clearError() }
    LaunchedEffect(noteId, title) {
        if (noteId != 0L && title.isNotBlank()) viewModel.loadBacklinks(title, noteId)
    }

    val tags = remember(content) {
        Regex("#(\\w+)").findAll(content.text).map { it.groupValues[1] }.toList()
    }

    Column(modifier = Modifier.fillMaxSize().background(BgColor)) {
        NoteDetailTopBar(
            noteId = noteId,
            isEditMode = isEditMode,
            onBack = { if (title.isNotBlank()) viewModel.saveNote(title, content.text, noteId); onBack() },
            onToggleEdit = {
                if (isEditMode && title.isNotBlank()) viewModel.saveNote(title, content.text, noteId)
                isEditMode = !isEditMode
            },
            onShowDelete = { showDeleteDialog = true }
        )

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            item { NoteDetailTitle(title, isEditMode) { title = it } }
            if (tags.isNotEmpty()) {
                item { NoteDetailTags(tags) }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                if (isEditMode) {
                    NoteDetailContentField(content) { content = it }
                } else {
                    ReadModeContent(content.text, notes, onNavigateToNote)
                }
            }
            if (backlinks.isNotEmpty()) {
                item { NoteDetailBacklinksHeader(backlinks.size) }
                items(backlinks) { note -> NoteDetailBacklinkItem(note, onNavigateToNote) }
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
                        Text("+ رابط جديد", fontSize = 13.sp, color = OnSurfaceVariant, modifier = Modifier.clickable { })
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        NoteDetailObsidianToolbar(isEditMode, content) { content = it }
    }

    if (showDeleteDialog) {
        NoteDetailDeleteDialog(
            title = title,
            onConfirm = { existing?.let { viewModel.deleteNote(it) }; showDeleteDialog = false; onBack() },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun NoteDetailTopBar(noteId: Long, isEditMode: Boolean, onBack: () -> Unit, onToggleEdit: () -> Unit, onShowDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp).statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = OnSurfaceVariant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (noteId != 0L) {
                Text("آخر تعديل الآن", fontSize = 11.sp, color = OnSurfaceVariant.copy(alpha = 0.6f))
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(if (isEditMode) Brush.horizontalGradient(listOf(AccentColor, PrimaryColor)) else Brush.horizontalGradient(listOf(SurfaceHigh, SurfaceHigh)))
                    .clickable { onToggleEdit() }.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (isEditMode) Icons.Default.CloudDone else Icons.Default.Edit, contentDescription = null,
                        tint = if (isEditMode) Color(0xFF1C0062) else OnSurfaceVariant, modifier = Modifier.size(14.dp))
                    Text(if (isEditMode) "تم" else "تعديل", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = if (isEditMode) Color(0xFF1C0062) else OnSurfaceVariant)
                }
            }
            IconButton(onClick = onShowDelete) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = OnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun NoteDetailTitle(title: String, isEditMode: Boolean, onTitleChange: (String) -> Unit) {
    if (isEditMode) {
        BasicTextField(
            value = title, onValueChange = onTitleChange,
            textStyle = TextStyle(color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp),
            cursorBrush = SolidColor(PrimaryColor),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            decorationBox = { inner ->
                if (title.isEmpty()) Text("العنوان", color = OnSurfaceVariant.copy(alpha = 0.5f), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                inner()
            }
        )
    } else {
        Text(text = title.ifBlank { "بدون عنوان" }, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
private fun NoteDetailTags(tags: List<String>) {
    Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tags.take(5).forEach { tag ->
            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(AccentColor.copy(alpha = 0.2f))
                .border(1.dp, AccentColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text("#$tag", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun NoteDetailContentField(content: TextFieldValue, onContentChange: (TextFieldValue) -> Unit) {
    BasicTextField(
        value = content, onValueChange = onContentChange,
        textStyle = TextStyle(color = Color.White, fontSize = 16.sp, lineHeight = 26.sp),
        cursorBrush = SolidColor(PrimaryColor),
        visualTransformation = MarkdownVisualTransformation(primaryColor = PrimaryColor, onSurface = Color.White),
        modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp),
        decorationBox = { inner ->
            if (content.text.isEmpty()) Text("واصل أفكارك...", color = OnSurfaceVariant.copy(alpha = 0.4f), fontSize = 16.sp)
            inner()
        }
    )
}

@Composable
private fun NoteDetailBacklinksHeader(count: Int) {
    Spacer(modifier = Modifier.height(32.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Default.AccountTree, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
        Text("الروابط ($count)", fontSize = 11.sp, letterSpacing = 2.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun NoteDetailBacklinkItem(note: Note, onNavigateToNote: (Long) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(10.dp))
            .background(SurfaceColor).clickable { onNavigateToNote(note.id) }.padding(14.dp)
    ) {
        Text(note.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        if (note.content.isNotBlank()) {
            Text("مذكور في: \"${note.content.take(50)}...\"", fontSize = 12.sp, color = OnSurfaceVariant)
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun NoteDetailBottomToolbar(isEditMode: Boolean, onAction: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(SurfaceColor).padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            Icons.Default.TextFields to "نص",
            Icons.Default.FormatBold to "عريض",
            Icons.Default.FormatListBulleted to "قائمة",
            Icons.Default.Link to "رابط",
            Icons.Default.Tag to "تاج",
            Icons.Default.Keyboard to "كيبورد"
        ).forEach { (icon, label) ->
            IconButton(onClick = { onAction(label) }) {
                Icon(icon, contentDescription = label, tint = if (isEditMode) OnSurfaceVariant else OnSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun NoteDetailDeleteDialog(title: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceColor,
        title = { Text("حذف الملاحظة", color = Color.White) },
        text = { Text("متأكد إنك عايز تحذف \"$title\"؟", color = OnSurfaceVariant) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("حذف", color = ErrorColor) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء", color = OnSurfaceVariant) } }
    )
}

@Composable
private fun ReadModeContent(content: String, notes: List<Note>, onNavigateToNote: (Long) -> Unit) {
    if (content.isBlank()) {
        Text("لا يوجد محتوى", color = OnSurfaceVariant.copy(alpha = 0.5f), fontSize = 16.sp)
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        content.split("\n").forEach { line ->
            when {
                line.startsWith("# ") -> Text(line.substring(2), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                line.startsWith("## ") -> Text(line.substring(3), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                line.startsWith("- ") || line.startsWith("• ") -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("•", color = PrimaryColor, fontSize = 16.sp)
                        Text(line.substring(2), color = Color.White, fontSize = 16.sp, lineHeight = 24.sp)
                    }
                }
                line.startsWith("> ") -> {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(3.dp).height(60.dp).background(AccentColor))
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)).background(SurfaceHigh).padding(12.dp)) {
                            Text(line.substring(2), color = OnSurfaceVariant, fontSize = 15.sp, fontStyle = FontStyle.Italic, lineHeight = 24.sp)
                        }
                    }
                }
                line.contains("[[") -> {
                    val annotated = buildAnnotatedString {
                        val regex = Regex("""\[\[(.+?)]]""")
                        var lastIndex = 0
                        regex.findAll(line).forEach { match ->
                            append(line.substring(lastIndex, match.range.first))
                            val linkTitle = match.groupValues[1]
                            val linkedNote = notes.find { it.title == linkTitle }
                            if (linkedNote != null) {
                                pushStringAnnotation("NOTE_LINK", linkedNote.id.toString())
                                withStyle(SpanStyle(color = PrimaryColor, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) { append(linkTitle) }
                                pop()
                            } else {
                                withStyle(SpanStyle(color = ErrorColor)) { append(linkTitle) }
                            }
                            lastIndex = match.range.last + 1
                        }
                        if (lastIndex < line.length) append(line.substring(lastIndex))
                    }
                    androidx.compose.foundation.text.ClickableText(
                        text = annotated,
                        style = TextStyle(color = Color.White, fontSize = 16.sp, lineHeight = 26.sp),
                        onClick = { offset -> annotated.getStringAnnotations("NOTE_LINK", offset, offset).firstOrNull()?.let { onNavigateToNote(it.item.toLong()) } }
                    )
                }
                line.contains("**") -> {
                    val annotated = buildAnnotatedString {
                        val parts = line.split("**")
                        parts.forEachIndexed { i, part ->
                            if (i % 2 == 1) withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) { append(part) }
                            else withStyle(SpanStyle(color = Color.White)) { append(part) }
                        }
                    }
                    Text(annotated, fontSize = 16.sp, lineHeight = 26.sp)
                }
                line.isBlank() -> Spacer(modifier = Modifier.height(8.dp))
                else -> Text(line, color = Color.White, fontSize = 16.sp, lineHeight = 26.sp)
            }
        }
    }
}

@Composable
private fun NoteDetailObsidianToolbar(
    isEditMode: Boolean,
    content: TextFieldValue,
    onContentChange: (TextFieldValue) -> Unit
) {
    if (!isEditMode) return
    val tools = listOf("H1", "H2", "H3", "B", "I", "•", "❝", "[[", "<>", "—", "☐", "@")
    val inserts = mapOf(
        "H1" to "# ", "H2" to "## ", "H3" to "### ",
        "B" to "****", "I" to "__",
        "•" to "\n- ", "❝" to "\n> ",
        "[[" to "[[]]", "<>" to "`<>`",
        "—" to "—", "☐" to "- [ ] ", "@" to "@"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceColor)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tools.forEach { tool ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceHigh)
                    .clickable {
                        val insert = inserts[tool] ?: return@clickable
                        val cursor = content.selection.end
                        val newText = content.text.substring(0, cursor) + insert + content.text.substring(cursor)
                        val newCursor = cursor + insert.length
                        onContentChange(TextFieldValue(text = newText, selection = TextRange(newCursor)))
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(tool, fontSize = 13.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}
