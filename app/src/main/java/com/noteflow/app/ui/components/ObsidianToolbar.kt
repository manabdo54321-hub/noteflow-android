package com.noteflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
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
    var savedValue by remember { mutableStateOf(value) }
    var showHeadingMenu by remember { mutableStateOf(false) }

    LaunchedEffect(value.selection, value.text) {
        savedValue = value
    }

    val history   = remember { ArrayDeque<TextFieldValue>() }
    val redoStack = remember { ArrayDeque<TextFieldValue>() }

    fun act(newVal: TextFieldValue) {
        history.addLast(savedValue)
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
                redoStack.addLast(savedValue)
                onValueChange(history.removeLast())
            }
        }
        TBtn("↪", tint = if (redoStack.isNotEmpty()) PrimaryColor else PrimaryColor.copy(0.3f)) {
            if (redoStack.isNotEmpty()) {
                history.addLast(savedValue)
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
                            act(tfLinePrefix(savedValue, data.first))
                            showHeadingMenu = false
                        }
                    )
                }
            }
        }
        TDiv()

        // ── Inline styles ────────────────────────────────────
        TBtn(icon = Icons.Default.FormatBold)   { act(tfWrap(savedValue, "**")) }
        TBtn(icon = Icons.Default.FormatItalic) { act(tfWrap(savedValue, "*")) }
        TBtn("S̶") { act(tfWrap(savedValue, "~~")) }
        TBtn("==") { act(tfWrap(savedValue, "==")) }
        TBtn(icon = Icons.Default.Code) { act(tfWrap(savedValue, "`")) }
        TDiv()

        // ── Lists ────────────────────────────────────────────
        TBtn("•")  { act(tfToggleList(savedValue, "- ")) }
        TBtn("1.") { act(tfNumbered(savedValue)) }
        TBtn("☐")  { act(tfToggleList(savedValue, "- [ ] ")) }
        TDiv()

        // ── Block elements ───────────────────────────────────
        TBtn("❝")   { act(tfLinePrefix(savedValue, "> ")) }
        TBtn("```") { act(tfCodeBlock(savedValue)) }
        TBtn("⊞")   { act(tfTable(savedValue)) }
        TBtn("—")   { act(tfCursor(savedValue, "\n---\n")) }
        TDiv()

        // ── Links & tags ─────────────────────────────────────
        TBtn("[[") { act(tfWikiLink(savedValue)) }
        TBtn("#")  { act(tfTag(savedValue)) }
        TDiv()

        // ── Indent ───────────────────────────────────────────
        TBtn("→") { act(tfLinePrefix(savedValue, "  ")) }
        TBtn("←") { act(tfRmPrefix(savedValue, "  ")) }
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
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() })
            }
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
