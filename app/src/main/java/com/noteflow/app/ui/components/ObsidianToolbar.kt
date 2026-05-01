package com.noteflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgColor      = Color(0xFF1A1A1A)
private val SurfaceHigh  = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val DividerColor = Color(0xFF3A3A3A)

@Composable
fun ObsidianToolbar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onCommand: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showHeadingMenu by remember { mutableStateOf(false) }
    val history   = remember { ArrayDeque<TextFieldValue>() }
    val redoStack = remember { ArrayDeque<TextFieldValue>() }

    fun act(newVal: TextFieldValue) {
        history.addLast(value)
        if (history.size > 40) history.removeFirst()
        redoStack.clear()
        val safeStart = newVal.selection.start.coerceIn(0, newVal.text.length)
        val safeEnd   = newVal.selection.end.coerceIn(0, newVal.text.length)
        val safe = if (safeStart == newVal.selection.start && safeEnd == newVal.selection.end) {
            newVal
        } else {
            newVal.copy(selection = androidx.compose.ui.text.TextRange(safeStart, safeEnd))
        }
        onValueChange(safe)
    }

    fun cmd(command: String) {
        onCommand?.invoke(command)
    }

    fun actOrCmd(command: String, fallback: () -> TextFieldValue) {
        if (onCommand != null) cmd(command) else act(fallback())
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
        // Undo / Redo
        TBtn("↩", tint = PrimaryColor) {
            if (onCommand != null) cmd("undo")
            else if (history.isNotEmpty()) { redoStack.addLast(value); onValueChange(history.removeLast()) }
        }
        TBtn("↪", tint = PrimaryColor) {
            if (onCommand != null) cmd("redo")
            else if (redoStack.isNotEmpty()) { history.addLast(value); onValueChange(redoStack.removeLast()) }
        }
        TDiv()

        // Headings Dropdown
        Box {
            TBtn("H", tint = if (showHeadingMenu) Color(0xFFCABEFF) else PrimaryColor) {
                showHeadingMenu = true
            }
            DropdownMenu(
                expanded = showHeadingMenu,
                onDismissRequest = { showHeadingMenu = false },
                modifier = Modifier.background(Color(0xFF2A2A2A))
            ) {
                listOf(
                    "H1" to Pair("h1", "# "),
                    "H2" to Pair("h2", "## "),
                    "H3" to Pair("h3", "### "),
                    "H4" to Pair("h4", "#### "),
                    "H5" to Pair("h5", "##### "),
                    "H6" to Pair("h6", "###### ")
                ).forEach { (label, data) ->
                    DropdownMenuItem(
                        text = { Text(label, color = PrimaryColor, fontWeight = FontWeight.Bold) },
                        onClick = {
                            actOrCmd(data.first) { tfLinePrefix(value, data.second) }
                            showHeadingMenu = false
                        }
                    )
                }
            }
        }
        TDiv()

        // Inline styles
        TBtn(icon = Icons.Default.FormatBold)   { actOrCmd("bold")          { tfWrap(value, "**") } }
        TBtn(icon = Icons.Default.FormatItalic) { actOrCmd("italic")        { tfWrap(value, "*") } }
        TBtn("S̶")                          { actOrCmd("strikethrough") { tfWrap(value, "~~") } }
        TBtn("==")                               { actOrCmd("highlight")     { tfWrap(value, "==") } }
        TBtn(icon = Icons.Default.Code)          { actOrCmd("inlineCode")    { tfWrap(value, "`") } }
        TDiv()

        // Lists
        TBtn("•")  { actOrCmd("bullet")   { tfToggleList(value, "- ") } }
        TBtn("1.") { actOrCmd("numbered") { tfNumbered(value) } }
        TBtn("☐")  { actOrCmd("checkbox") { tfToggleList(value, "- [ ] ") } }
        TDiv()

        // Block elements
        TBtn("❝")   { actOrCmd("quote")     { tfLinePrefix(value, "> ") } }
        TBtn("```") { actOrCmd("codeblock") { tfCodeBlock(value) } }
        TBtn("⊞")   { actOrCmd("table")     { tfTable(value) } }
        TBtn("—")   { actOrCmd("hr")        { tfCursor(value, "\n---\n") } }
        TDiv()

        // Callouts
        TBtn("!") { actOrCmd("callout") { tfCursor(value, "> [!INFO] ") } }
        TDiv()

        // Links & tags
        TBtn("[[")  { actOrCmd("wikilink") { tfWikiLink(value) } }
        TBtn("![[") { actOrCmd("embed")    { tfCursor(value, "![[]]") } }
        TBtn("[]()") { actOrCmd("link")   { tfCursor(value, "[](url)") } }
        TBtn("#")   { actOrCmd("tag")      { tfTag(value) } }
        TDiv()

        // Indent
        TBtn("→") { actOrCmd("indent")   { tfLinePrefix(value, "  ") } }
        TBtn("←") { actOrCmd("unindent") { tfRmPrefix(value, "  ") } }
    }
}

@Composable
private fun TBtn(
    label: String = "",
    icon: ImageVector? = null,
    tint: Color = PrimaryColor,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceHigh)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null)
            Icon(icon, null, tint = tint, modifier = Modifier.size(16.dp))
        else
            Text(label, fontSize = 12.sp, color = tint, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TDiv() {
    Box(modifier = Modifier.width(1.dp).height(20.dp).background(DividerColor))
}
