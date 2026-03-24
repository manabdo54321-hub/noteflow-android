package com.noteflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgColor = Color(0xFF1A1A1A)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val DividerColor = Color(0xFF3A3A3A)

data class ToolbarItem(
    val label: String,
    val icon: ImageVector? = null,
    val tint: Color = PrimaryColor,
    val action: (TextFieldValue) -> TextFieldValue
)

@Composable
fun ObsidianToolbar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    val history = remember { ArrayDeque<TextFieldValue>() }
    val redoStack = remember { ArrayDeque<TextFieldValue>() }

    fun doAction(newValue: TextFieldValue) {
        history.addLast(value)
        if (history.size > 50) history.removeFirst()
        redoStack.clear()
        onValueChange(newValue)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BgColor)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolbarBtn("↩", tint = if (history.isNotEmpty()) PrimaryColor else PrimaryColor.copy(0.3f)) {
            if (history.isNotEmpty()) { redoStack.addLast(value); onValueChange(history.removeLast()) }
        }
        ToolbarBtn("↪", tint = if (redoStack.isNotEmpty()) PrimaryColor else PrimaryColor.copy(0.3f)) {
            if (redoStack.isNotEmpty()) { history.addLast(value); onValueChange(redoStack.removeLast()) }
        }
        ToolbarDivider()
        ToolbarBtn("H1") { doAction(toggleLinePrefix(value, "# ")) }
        ToolbarBtn("H2") { doAction(toggleLinePrefix(value, "## ")) }
        ToolbarBtn("H3") { doAction(toggleLinePrefix(value, "### ")) }
        ToolbarDivider()
        ToolbarBtn(icon = Icons.Default.FormatBold) { doAction(wrapSelection(value, "**")) }
        ToolbarBtn(icon = Icons.Default.FormatItalic) { doAction(wrapSelection(value, "*")) }
        ToolbarBtn("S̶") { doAction(wrapSelection(value, "~~")) }
        ToolbarBtn("U̲") { doAction(wrapSelection(value, "<u>", "</u>")) }
        ToolbarBtn("==") { doAction(wrapSelection(value, "==")) }
        ToolbarDivider()
        ToolbarBtn("x²") { doAction(wrapSelection(value, "<sup>", "</sup>")) }
        ToolbarBtn("x₂") { doAction(wrapSelection(value, "<sub>", "</sub>")) }
        ToolbarDivider()
        ToolbarBtn("•") { doAction(insertAtCursor(value, "\n- ")) }
        ToolbarBtn("❝") { doAction(toggleLinePrefix(value, "> ")) }
        ToolbarBtn("☐") { doAction(insertAtCursor(value, "- [ ] ")) }
        ToolbarDivider()
        ToolbarBtn("[[") { doAction(insertAtCursor(value, "[[]]", 2)) }
        ToolbarBtn("<>") { doAction(wrapSelection(value, "`")) }
        ToolbarBtn("```") { doAction(insertAtCursor(value, "\n```\n\n```\n", 5)) }
        ToolbarDivider()
        ToolbarBtn("⊞") { doAction(insertTable(value)) }
        ToolbarBtn("—") { doAction(insertAtCursor(value, "\n---\n")) }
        ToolbarDivider()
        ToolbarBtn("→") { doAction(toggleLinePrefix(value, "  ")) }
        ToolbarBtn("←") { doAction(removeFromLineStart(value, "  ")) }
    }
}

@Composable
private fun ToolbarBtn(
    label: String = "",
    icon: ImageVector? = null,
    tint: Color = PrimaryColor,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(16.dp))
        } else {
            Text(label, fontSize = 12.sp, color = tint, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ToolbarDivider() {
    Box(modifier = Modifier.width(1.dp).height(20.dp).background(DividerColor))
}

private fun insertAtCursor(value: TextFieldValue, text: String, cursorOffset: Int = 0): TextFieldValue {
    val cursor = value.selection.end.coerceIn(0, value.text.length)
    val newText = value.text.substring(0, cursor) + text + value.text.substring(cursor)
    val newCursor = cursor + (if (cursorOffset > 0) cursorOffset else text.length)
    return TextFieldValue(newText, TextRange(newCursor.coerceIn(0, newText.length)))
}

private fun wrapSelection(value: TextFieldValue, wrapper: String, closing: String = wrapper): TextFieldValue {
    val start = value.selection.start.coerceIn(0, value.text.length)
    val end = value.selection.end.coerceIn(0, value.text.length)
    return if (start == end) {
        val newText = value.text.substring(0, end) + wrapper + closing + value.text.substring(end)
        TextFieldValue(newText, TextRange(end + wrapper.length))
    } else {
        val selected = value.text.substring(start, end)
        if (selected.startsWith(wrapper) && selected.endsWith(closing)) {
            val unwrapped = selected.removePrefix(wrapper).removeSuffix(closing)
            val newText = value.text.substring(0, start) + unwrapped + value.text.substring(end)
            TextFieldValue(newText, TextRange(start, start + unwrapped.length))
        } else {
            val newText = value.text.substring(0, start) + wrapper + selected + closing + value.text.substring(end)
            TextFieldValue(newText, TextRange(start + wrapper.length, end + wrapper.length))
        }
    }
}

private fun toggleLinePrefix(value: TextFieldValue, prefix: String): TextFieldValue {
    val cursor = value.selection.end.coerceIn(0, value.text.length)
    val lineStart = if (cursor == 0) 0 else (value.text.lastIndexOf('\n', cursor - 1) + 1)
    val lineText = value.text.substring(lineStart)
    return if (lineText.startsWith(prefix)) {
        val newText = value.text.substring(0, lineStart) + lineText.removePrefix(prefix)
        TextFieldValue(newText, TextRange(maxOf(lineStart, cursor - prefix.length)))
    } else {
        val newText = value.text.substring(0, lineStart) + prefix + value.text.substring(lineStart)
        TextFieldValue(newText, TextRange(cursor + prefix.length))
    }
}

private fun removeFromLineStart(value: TextFieldValue, prefix: String): TextFieldValue {
    val cursor = value.selection.end.coerceIn(0, value.text.length)
    val lineStart = if (cursor == 0) 0 else (value.text.lastIndexOf('\n', cursor - 1) + 1)
    return if (value.text.substring(lineStart).startsWith(prefix)) {
        val newText = value.text.substring(0, lineStart) + value.text.substring(lineStart + prefix.length)
        TextFieldValue(newText, TextRange(maxOf(lineStart, cursor - prefix.length)))
    } else value
}

private fun insertTable(value: TextFieldValue): TextFieldValue {
    val table = "\n| العنوان 1 | العنوان 2 | العنوان 3 |\n| --- | --- | --- |\n| خلية 1 | خلية 2 | خلية 3 |\n"
    return insertAtCursor(value, table)
}
