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
private val SurfaceAct   = Color(0xFF3D3560)
private val PrimaryColor = Color(0xFFCABEFF)
private val DividerColor = Color(0xFF3A3A3A)

@Composable
fun ObsidianToolbar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    var showHeadingMenu by remember { mutableStateOf(false) }

    val history   = remember { ArrayDeque<TextFieldValue>() }
    val redoStack = remember { ArrayDeque<TextFieldValue>() }

    fun act(newVal: TextFieldValue) {
        history.addLast(value)
        if (history.size > 40) history.removeFirst()
        redoStack.clear()
        onValueChange(newVal)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BgColor)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // ── Undo / Redo ──────────────────────────────────────
        TBtn("↩", tint = if (history.isNotEmpty()) PrimaryColor else PrimaryColor.copy(0.3f)) {
            if (history.isNotEmpty()) {
                redoStack.addLast(value)
                onValueChange(history.removeLast())
            }
        }
        TBtn("↪", tint = if (redoStack.isNotEmpty()) PrimaryColor else PrimaryColor.copy(0.3f)) {
            if (redoStack.isNotEmpty()) {
                history.addLast(value)
                onValueChange(redoStack.removeLast())
            }
        }
        TDiv()

        // ── Headings Dropdown ────────────────────────────────
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
                    "H1" to Pair("# ",  20.sp),
                    "H2" to Pair("## ", 17.sp),
                    "H3" to Pair("### ", 15.sp),
                    "H4" to Pair("#### ", 13.sp),
                    "H5" to Pair("##### ", 12.sp),
                    "H6" to Pair("###### ", 11.sp)
                ).forEach { (label, data) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                label,
                                color = PrimaryColor,
                                fontSize = data.second,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            act(tfLinePrefix(value, data.first))
                            showHeadingMenu = false
                        }
                    )
                }
            }
        }
        TDiv()

        // ── Inline styles ────────────────────────────────────
        TBtn(icon = Icons.Default.FormatBold)   { act(tfWrap(value, "**")) }
        TBtn(icon = Icons.Default.FormatItalic) { act(tfWrap(value, "*")) }
        TBtn("S̶") { act(tfWrap(value, "~~")) }
        TBtn("==") { act(tfWrap(value, "==")) }
        TBtn(icon = Icons.Default.Code) { act(tfWrap(value, "`")) }
        TDiv()

        // ── Lists ────────────────────────────────────────────
        TBtn("•")  { act(tfToggleList(value, "- ")) }
        TBtn("1.") { act(tfNumbered(value)) }
        TBtn("☐")  { act(tfToggleList(value, "- [ ] ")) }
        TDiv()

        // ── Block elements ───────────────────────────────────
        TBtn("❝")   { act(tfLinePrefix(value, "> ")) }
        TBtn("```") { act(tfCodeBlock(value)) }
        TBtn("⊞")   { act(tfTable(value)) }
        TBtn("—")   { act(tfCursor(value, "\n---\n")) }
        TDiv()
        // ── Callouts ─────────────────────────────────────────
        TBtn("!") { act(tfCursor(value, "> [!INFO] ")) }
        TDiv()
        // ── Links & tags ─────────────────────────────────────
        TBtn("[[") { act(tfWikiLink(value)) }
        TBtn("![[") { act(tfCursor(value, "![[]]")) }
        TBtn("[]()"){ act(tfCursor(value, "[](url)")) }
        TBtn("#")  { act(tfTag(value)) }
        TDiv()

        // ── Indent ───────────────────────────────────────────
        TBtn("→") { act(tfLinePrefix(value, "  ")) }
        TBtn("←") { act(tfRmPrefix(value, "  ")) }
    }
}

// ─── UI helpers ─────────────────────────────────────────────
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
