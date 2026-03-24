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
private val OnSurfaceVariant = Color(0xFFC8C5CD)

data class ToolbarItem(
    val label: String,
    val icon: ImageVector? = null,
    val action: (TextFieldValue) -> TextFieldValue
)

@Composable
fun ObsidianToolbar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = rememberToolbarItems()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BgColor)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            ToolbarButton(
                label = item.label,
                icon = item.icon,
                onClick = { onValueChange(item.action(value)) }
            )
        }
    }
}

@Composable
private fun ToolbarButton(label: String, icon: ImageVector?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = label,
                tint = PrimaryColor, modifier = Modifier.size(16.dp))
        } else {
            Text(label, fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun rememberToolbarItems(): List<ToolbarItem> {
    return remember {
        listOf(
            ToolbarItem("H1") { insertAtLineStart(it, "# ") },
            ToolbarItem("H2") { insertAtLineStart(it, "## ") },
            ToolbarItem("H3") { insertAtLineStart(it, "### ") },
            ToolbarItem("B", Icons.Default.FormatBold) { wrapSelection(it, "**") },
            ToolbarItem("I", Icons.Default.FormatItalic) { wrapSelection(it, "*") },
            ToolbarItem("S") { wrapSelection(it, "~~") },
            ToolbarItem("•") { insertAtCursor(it, "\n- ") },
            ToolbarItem("1.") { insertAtCursor(it, "\n1. ") },
            ToolbarItem("❝") { insertAtLineStart(it, "> ") },
            ToolbarItem("[[") { insertAtCursor(it, "[[]]", cursorOffset = 2) },
            ToolbarItem("<>") { wrapSelection(it, "`") },
            ToolbarItem("☐") { insertAtCursor(it, "- [ ] ") },
            ToolbarItem("—") { insertAtCursor(it, "---\n") },
            ToolbarItem("→") { insertAtLineStart(it, "  ") },
            ToolbarItem("←") { removeFromLineStart(it, "  ") }
        )
    }
}

private fun insertAtCursor(
    value: TextFieldValue,
    text: String,
    cursorOffset: Int = 0
): TextFieldValue {
    val cursor = value.selection.end
    val newText = value.text.substring(0, cursor) + text + value.text.substring(cursor)
    val newCursor = cursor + (if (cursorOffset > 0) cursorOffset else text.length)
    return TextFieldValue(newText, TextRange(newCursor))
}

private fun wrapSelection(value: TextFieldValue, wrapper: String): TextFieldValue {
    val start = value.selection.start
    val end = value.selection.end
    return if (start == end) {
        val newText = value.text.substring(0, end) + wrapper + wrapper + value.text.substring(end)
        TextFieldValue(newText, TextRange(end + wrapper.length))
    } else {
        val selected = value.text.substring(start, end)
        val newText = value.text.substring(0, start) + wrapper + selected + wrapper + value.text.substring(end)
        TextFieldValue(newText, TextRange(start + wrapper.length, end + wrapper.length))
    }
}

private fun insertAtLineStart(value: TextFieldValue, prefix: String): TextFieldValue {
    val cursor = value.selection.end
    val lineStart = value.text.lastIndexOf('\n', cursor - 1) + 1
    val newText = value.text.substring(0, lineStart) + prefix + value.text.substring(lineStart)
    return TextFieldValue(newText, TextRange(cursor + prefix.length))
}

private fun removeFromLineStart(value: TextFieldValue, prefix: String): TextFieldValue {
    val cursor = value.selection.end
    val lineStart = value.text.lastIndexOf('\n', cursor - 1) + 1
    return if (value.text.substring(lineStart).startsWith(prefix)) {
        val newText = value.text.substring(0, lineStart) + value.text.substring(lineStart + prefix.length)
        TextFieldValue(newText, TextRange(maxOf(lineStart, cursor - prefix.length)))
    } else value
}
