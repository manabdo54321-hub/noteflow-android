package com.noteflow.app.ui.components

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

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

// ─── Insert at cursor ───────────────────────────────────────
fun tfCursor(v: TextFieldValue, text: String, offset: Int = 0): TextFieldValue {
    val c = v.selection.end.coerceIn(0, v.text.length)
    val t = v.text.substring(0, c) + text + v.text.substring(c)
    val nc = c + if (offset > 0) offset else text.length
    return TextFieldValue(t, TextRange(nc.coerceIn(0, t.length)))
}

// ─── Wrap selection ─────────────────────────────────────────
fun tfWrap(v: TextFieldValue, open: String, close: String = open): TextFieldValue {
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

// ─── Line prefix (toggle) ───────────────────────────────────
fun tfLinePrefix(v: TextFieldValue, prefix: String): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val ls = if (c == 0) 0 else v.text.lastIndexOf('\n', c - 1) + 1
    val le = v.text.indexOf('\n', c).let { if (it == -1) v.text.length else it }
    val line = v.text.substring(ls, le)
    return if (line.startsWith(prefix)) {
        val t = v.text.substring(0, ls) + line.removePrefix(prefix) + v.text.substring(le)
        TextFieldValue(t, TextRange(maxOf(ls, c - prefix.length)))
    } else {
        val t = v.text.substring(0, ls) + prefix + v.text.substring(ls)
        TextFieldValue(t, TextRange(c + prefix.length))
    }
}

// ─── Toggle list prefix ─────────────────────────────────────
fun tfToggleList(v: TextFieldValue, prefix: String): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val ls = if (c == 0) 0 else v.text.lastIndexOf('\n', c - 1) + 1
    val le = v.text.indexOf('\n', c).let { if (it == -1) v.text.length else it }
    val line = v.text.substring(ls, le)
    val listPrefixes = listOf("- [ ] ", "- [x] ", "- ", "  ")
    val existing = listPrefixes.firstOrNull { line.startsWith(it) }
    return when {
        line.startsWith(prefix) -> {
            val t = v.text.substring(0, ls) + line.removePrefix(prefix) + v.text.substring(le)
            TextFieldValue(t, TextRange(maxOf(ls, c - prefix.length)))
        }
        existing != null -> {
            val t = v.text.substring(0, ls) + prefix + line.removePrefix(existing) + v.text.substring(le)
            TextFieldValue(t, TextRange(ls + prefix.length + (c - ls - existing.length).coerceAtLeast(0)))
        }
        else -> {
            val t = v.text.substring(0, ls) + prefix + v.text.substring(ls)
            TextFieldValue(t, TextRange(c + prefix.length))
        }
    }
}

// ─── Remove line prefix ─────────────────────────────────────
fun tfRmPrefix(v: TextFieldValue, prefix: String): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val ls = if (c == 0) 0 else v.text.lastIndexOf('\n', c - 1) + 1
    return if (v.text.substring(ls).startsWith(prefix)) {
        val t = v.text.substring(0, ls) + v.text.substring(ls + prefix.length)
        TextFieldValue(t, TextRange(maxOf(ls, c - prefix.length)))
    } else v
}

// ─── Wiki link ──────────────────────────────────────────────
fun tfWikiLink(v: TextFieldValue): TextFieldValue {
    val s = v.selection.start.coerceIn(0, v.text.length)
    val e = v.selection.end.coerceIn(0, v.text.length)
    return if (s != e) {
        val sel = v.text.substring(s, e)
        val t   = v.text.substring(0, s) + "[[" + sel + "]]" + v.text.substring(e)
        TextFieldValue(t, TextRange(s + 2, e + 2))
    } else {
        val t = v.text.substring(0, e) + "[[]]" + v.text.substring(e)
        TextFieldValue(t, TextRange(e + 2))
    }
}

// ─── Tag ────────────────────────────────────────────────────
fun tfTag(v: TextFieldValue): TextFieldValue {
    val c = v.selection.end.coerceIn(0, v.text.length)
    val needsSpace = c > 0 && v.text[c - 1] != ' ' && v.text[c - 1] != '\n'
    val insert = if (needsSpace) " #" else "#"
    val t = v.text.substring(0, c) + insert + v.text.substring(c)
    return TextFieldValue(t, TextRange(c + insert.length))
}

// ─── Numbered list ──────────────────────────────────────────
fun tfNumbered(v: TextFieldValue): TextFieldValue {
    val c  = v.selection.end.coerceIn(0, v.text.length)
    val ls = if (c == 0) 0 else v.text.lastIndexOf('\n', c - 1) + 1
    val prevLine = if (ls > 0) {
        val prevEnd   = ls - 1
        val prevStart = if (prevEnd == 0) 0 else v.text.lastIndexOf('\n', prevEnd - 1) + 1
        v.text.substring(prevStart, prevEnd)
    } else ""
    val prevNum = Regex("^(\\d+)\\. ").find(prevLine)?.groupValues?.get(1)?.toIntOrNull()
    val num     = (prevNum ?: 0) + 1
    val prefix  = "$num. "
    val currentLine = v.text.substring(ls)
    return if (currentLine.matches(Regex("^\\d+\\. .*"))) {
        val insert = "\n$num. "
        val t = v.text.substring(0, c) + insert + v.text.substring(c)
        TextFieldValue(t, TextRange(c + insert.length))
    } else {
        val t = v.text.substring(0, ls) + prefix + v.text.substring(ls)
        TextFieldValue(t, TextRange(c + prefix.length))
    }
}

// ─── Code block ─────────────────────────────────────────────
fun tfCodeBlock(v: TextFieldValue): TextFieldValue {
    val c = v.selection.end.coerceIn(0, v.text.length)
    val s = v.selection.start.coerceIn(0, v.text.length)
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

// ─── Table ──────────────────────────────────────────────────
fun tfTable(v: TextFieldValue): TextFieldValue {
    val c      = v.selection.end.coerceIn(0, v.text.length)
    val insert = "\n| العنوان 1 | العنوان 2 |\n| --- | --- |\n| خلية 1 | خلية 2 |\n"
    val t      = v.text.substring(0, c) + insert + v.text.substring(c)
    return TextFieldValue(t, TextRange(c + insert.length))
}
