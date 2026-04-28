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
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants

private val BgColor     = Color(0xFF1A1A1A)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val SurfaceAct  = Color(0xFF3D3560)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor  = Color(0xFF8A70FF)
private val DividerColor = Color(0xFF3A3A3A)

// ─── Smart Enter ────────────────────────────────────────────
fun handleEnterKey(v: TextFieldValue): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val ls = if (c == 0) 0 else v.text.lastIndexOf('\n', c - 1) + 1
    val line = v.text.substring(ls, c)
    val continuation: String? = when {
        line.matches(Regex("^- \\[[ x]] .*"))    -> "\n- [ ] "
        line.matches(Regex("^\\d+\\. .+"))        -> {
            val num = line.substringBefore(".").toIntOrNull() ?: 1
            "\n${num + 1}. "
        }
        line.startsWith("- ") && line.length > 2 -> "\n- "
        line.startsWith("> ")                     -> "\n> "
        else                                      -> null
    }
    return if (continuation != null) {
        val t = v.text.substring(0, c) + continuation + v.text.substring(c)
        TextFieldValue(t, TextRange(c + continuation.length))
    } else v
}

// ─── Main Composable ────────────────────────────────────────
@Composable
fun ObsidianToolbar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
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
        verticalAlignment    = Alignment.CenterVertically
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

        // ── Headings ─────────────────────────────────────────
        TBtn("H1") { act(linePrefix(value, "# ")) }
        TBtn("H2") { act(linePrefix(value, "## ")) }
        TBtn("H3") { act(linePrefix(value, "### ")) }
        TDiv()

        // ── Inline styles ────────────────────────────────────
        TBtn(icon = Icons.Default.FormatBold)   { act(wrap(value, "**")) }
        TBtn(icon = Icons.Default.FormatItalic) { act(wrap(value, "*")) }
        TBtn("S̶") { act(wrap(value, "~~")) }
        TBtn("==") { act(wrap(value, "==")) }
        TBtn(icon = Icons.Default.Code) { act(wrap(value, "`")) }
        TDiv()

        // ── Lists ────────────────────────────────────────────
        TBtn("•")  { act(toggleLinePrefix(value, "- ")) }
        TBtn("1.") { act(insertNumberedItem(value)) }
        TBtn("☐")  { act(toggleLinePrefix(value, "- [ ] ")) }
        TDiv()

        // ── Block elements ───────────────────────────────────
        TBtn("❝")   { act(linePrefix(value, "> ")) }
        TBtn("```") { act(insertCodeBlock(value)) }
        TBtn("⊞")   { act(insertTable(value)) }
        TBtn("—")   { act(cursor(value, "\n---\n")) }
        TDiv()

        // ── Links & tags ─────────────────────────────────────
        TBtn("[[") { act(insertWikiLink(value)) }
        TBtn("#")  { act(insertTag(value)) }
        TDiv()

        // ── Indent ───────────────────────────────────────────
        TBtn("→") { act(linePrefix(value, "  ")) }
        TBtn("←") { act(rmLinePrefix(value, "  ")) }
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
    val view = LocalView.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceHigh)
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onClick()
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

// ─── Text engine ─────────────────────────────────────────────

// أدرج نص عند الـ cursor
private fun cursor(v: TextFieldValue, text: String, offset: Int = 0): TextFieldValue {
    val c = v.selection.end.coerceIn(0, v.text.length)
    val t = v.text.substring(0, c) + text + v.text.substring(c)
    val nc = c + if (offset > 0) offset else text.length
    return TextFieldValue(t, TextRange(nc.coerceIn(0, t.length)))
}

// أحط نص حول الـ selection (toggle-aware)
private fun wrap(v: TextFieldValue, open: String, close: String = open): TextFieldValue {
    val s = v.selection.start.coerceIn(0, v.text.length)
    val e = v.selection.end.coerceIn(0, v.text.length)
    return if (s == e) {
        val t = v.text.substring(0, e) + open + close + v.text.substring(e)
        TextFieldValue(t, TextRange(e + open.length))
    } else {
        val sel = v.text.substring(s, e)
        if (sel.startsWith(open) && sel.endsWith(close) && sel.length > open.length + close.length) {
            val u = sel.removePrefix(open).removeSuffix(close)
            TextFieldValue(v.text.substring(0, s) + u + v.text.substring(e), TextRange(s, s + u.length))
        } else {
            val t = v.text.substring(0, s) + open + sel + close + v.text.substring(e)
            TextFieldValue(t, TextRange(s + open.length, e + open.length))
        }
    }
}

// أضف/أزل prefix لسطر (toggle)
private fun linePrefix(v: TextFieldValue, prefix: String): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val ls = if (c == 0) 0 else v.text.lastIndexOf('\n', c - 1) + 1
    val line = v.text.substring(ls)
    return if (line.startsWith(prefix)) {
        val t = v.text.substring(0, ls) + line.removePrefix(prefix)
        TextFieldValue(t, TextRange(maxOf(ls, c - prefix.length)))
    } else {
        val t = v.text.substring(0, ls) + prefix + v.text.substring(ls)
        TextFieldValue(t, TextRange(c + prefix.length))
    }
}

// toggle للقوائم — يشيل أي prefix قائمة موجود ويحط الجديد
private fun toggleLinePrefix(v: TextFieldValue, prefix: String): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val ls = if (c == 0) 0 else v.text.lastIndexOf('\n', c - 1) + 1
    val line = v.text.substring(ls)
    val listPrefixes = listOf("- [ ] ", "- [x] ", "- ", "  ")
    val existing = listPrefixes.firstOrNull { line.startsWith(it) }
    return when {
        line.startsWith(prefix) -> {
            // نفس الـ prefix موجود → أزله
            val t = v.text.substring(0, ls) + line.removePrefix(prefix)
            TextFieldValue(t, TextRange(maxOf(ls, c - prefix.length)))
        }
        existing != null -> {
            // prefix مختلف موجود → استبدله
            val t = v.text.substring(0, ls) + prefix + line.removePrefix(existing)
            TextFieldValue(t, TextRange(ls + prefix.length + (c - ls - existing.length).coerceAtLeast(0)))
        }
        else -> {
            // مفيش prefix → أضفه
            val t = v.text.substring(0, ls) + prefix + v.text.substring(ls)
            TextFieldValue(t, TextRange(c + prefix.length))
        }
    }
}

private fun rmLinePrefix(v: TextFieldValue, prefix: String): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val ls = if (c == 0) 0 else v.text.lastIndexOf('\n', c - 1) + 1
    return if (v.text.substring(ls).startsWith(prefix)) {
        val t = v.text.substring(0, ls) + v.text.substring(ls + prefix.length)
        TextFieldValue(t, TextRange(maxOf(ls, c - prefix.length)))
    } else v
}

// ─── Specialized insertions ──────────────────────────────────

private fun insertWikiLink(v: TextFieldValue): TextFieldValue {
    val s = v.selection.start.coerceIn(0, v.text.length)
    val e = v.selection.end.coerceIn(0, v.text.length)
    return if (s != e) {
        val sel  = v.text.substring(s, e)
        val t    = v.text.substring(0, s) + "[[" + sel + "]]" + v.text.substring(e)
        TextFieldValue(t, TextRange(s + 2, e + 2))
    } else {
        val t = v.text.substring(0, e) + "[[]]" + v.text.substring(e)
        TextFieldValue(t, TextRange(e + 2))
    }
}

private fun insertTag(v: TextFieldValue): TextFieldValue {
    val c = v.selection.end.coerceIn(0, v.text.length)
    // لو قبل الـ cursor في نص (مش مسافة أو بداية) → أضف مسافة أولاً
    val needsSpace = c > 0 && v.text[c - 1] != ' ' && v.text[c - 1] != '\n'
    val insert = if (needsSpace) " #" else "#"
    val t  = v.text.substring(0, c) + insert + v.text.substring(c)
    return TextFieldValue(t, TextRange(c + insert.length))
}

private fun insertNumberedItem(v: TextFieldValue): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val ls = if (c == 0) 0 else v.text.lastIndexOf('\n', c - 1) + 1
    // اكتشف الرقم الأخير في القائمة المرقّمة
    val prevLine = if (ls > 0) {
        val prevEnd = ls - 1
        val prevStart = if (prevEnd == 0) 0 else v.text.lastIndexOf('\n', prevEnd - 1) + 1
        v.text.substring(prevStart, prevEnd)
    } else ""
    val prevNum = Regex("^(\\d+)\\. ").find(prevLine)?.groupValues?.get(1)?.toIntOrNull()
    val num = (prevNum ?: 0) + 1
    val prefix = "$num. "
    val currentLine = v.text.substring(ls)
    return if (currentLine.matches(Regex("^\\d+\\. .*"))) {
        // السطر الحالي فيه رقم → ابدأ سطر جديد
        val insert = "\n$num. "
        val t = v.text.substring(0, c) + insert + v.text.substring(c)
        TextFieldValue(t, TextRange(c + insert.length))
    } else {
        val t = v.text.substring(0, ls) + prefix + v.text.substring(ls)
        TextFieldValue(t, TextRange(c + prefix.length))
    }
}

private fun insertCodeBlock(v: TextFieldValue): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val s  = v.selection.start.coerceIn(0, v.text.length)
    return if (s != c) {
        val sel    = v.text.substring(s, c)
        val insert = "\n```\n$sel\n```\n"
        val t      = v.text.substring(0, s) + insert + v.text.substring(c)
        TextFieldValue(t, TextRange(s + 5, s + 5 + sel.length))
    } else {
        val insert = "\n```\n\n```\n"
        val t = v.text.substring(0, c) + insert + v.text.substring(c)
        TextFieldValue(t, TextRange(c + 5))
    }
}

private fun insertTable(v: TextFieldValue): TextFieldValue {
    val c      = v.selection.end.coerceIn(0, v.text.length)
    val insert = "\n| العنوان 1 | العنوان 2 |\n| --- | --- |\n| خلية 1 | خلية 2 |\n"
    val t      = v.text.substring(0, c) + insert + v.text.substring(c)
    // ضع الـ cursor داخل أول خلية
    return TextFieldValue(t, TextRange(c + insert.length))
}
