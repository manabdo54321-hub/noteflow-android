package com.noteflow.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

val MdPrimary   = Color(0xFFCABEFF)
val MdAccent    = Color(0xFF8A70FF)
val MdTertiary  = Color(0xFF75D1FF)
val MdSurface   = Color(0xFF2A2A2A)
val MdError     = Color(0xFFFF6B6B)
val MdWhite     = Color(0xFFE5E2E1)
val MdGray      = Color(0xFFC8C5CD)

val MdCalloutInfo     = Color(0xFF75D1FF)
val MdCalloutWarning  = Color(0xFFFFB347)
val MdCalloutTip      = Color(0xFF7EC87E)
val MdCalloutDanger   = Color(0xFFFF6B6B)
val MdCalloutQuestion = Color(0xFFCABEFF)

fun buildMarkdownAnnotated(text: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        lines.forEachIndexed { index, line ->
            appendMarkdownLine(line)
            if (index < lines.size - 1) append("\n")
        }
    }
}

private fun AnnotatedString.Builder.appendMarkdownLine(line: String) {
    when {
        line.startsWith("> [!INFO]") -> appendCallout(line, "INFO", MdCalloutInfo)
        line.startsWith("> [!WARNING]") -> appendCallout(line, "WARN", MdCalloutWarning)
        line.startsWith("> [!TIP]") -> appendCallout(line, "TIP", MdCalloutTip)
        line.startsWith("> [!DANGER]") -> appendCallout(line, "DANGER", MdCalloutDanger)
        line.startsWith("> [!QUESTION]") -> appendCallout(line, "?", MdCalloutQuestion)
        line.startsWith("> [!NOTE]") -> appendCallout(line, "NOTE", MdCalloutInfo)
        line.startsWith("###### ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)) {
                append(line.removePrefix("###### "))
            }
        }
        line.startsWith("##### ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)) {
                append(line.removePrefix("##### "))
            }
        }
        line.startsWith("#### ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)) {
                append(line.removePrefix("#### "))
            }
        }
        line.startsWith("### ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)) {
                append(line.removePrefix("### "))
            }
        }
        line.startsWith("## ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)) {
                append(line.removePrefix("## "))
            }
        }
        line.startsWith("# ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)) {
                append(line.removePrefix("# "))
            }
        }
        line.startsWith("> ") -> {
            withStyle(SpanStyle(color = MdGray, fontStyle = FontStyle.Italic, background = MdSurface)) {
                append("  " + line.removePrefix("> "))
            }
        }
        line.startsWith("---") -> {
            withStyle(SpanStyle(color = MdGray)) { append("─".repeat(30)) }
        }
        line.startsWith("- [x] ") -> {
            withStyle(SpanStyle(color = MdPrimary)) { append("✅ ") }
            withStyle(SpanStyle(color = MdGray, textDecoration = TextDecoration.LineThrough)) {
                appendInlineMarkdown(line.removePrefix("- [x] "))
            }
        }
        line.startsWith("- [ ] ") -> {
            withStyle(SpanStyle(color = MdGray)) { append("☐ ") }
            appendInlineMarkdown(line.removePrefix("- [ ] "))
        }
        line.startsWith("- ") || line.startsWith("• ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontWeight = FontWeight.Bold)) { append("• ") }
            appendInlineMarkdown(line.removePrefix("- ").removePrefix("• "))
        }
        line.matches(Regex("^\\d+\\. .*")) -> {
            val num = line.substringBefore(".")
            withStyle(SpanStyle(color = MdPrimary, fontWeight = FontWeight.Bold)) { append("$num. ") }
            appendInlineMarkdown(line.substringAfter(". "))
        }
        line.startsWith("```") -> {
            withStyle(SpanStyle(color = MdTertiary, fontFamily = FontFamily.Monospace, background = MdSurface)) {
                append(line)
            }
        }
        else -> appendInlineMarkdown(line)
    }
}

private fun AnnotatedString.Builder.appendCallout(
    line: String,
    label: String,
    color: Color
) {
    val typeEnd = line.indexOf("]")
    val title = if (typeEnd != -1 && typeEnd + 1 < line.length)
        line.substring(typeEnd + 1).trim()
    else ""
    withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
        append("[$label] ")
    }
    withStyle(SpanStyle(color = color)) {
        if (title.isNotEmpty()) append(title)
        else append(line.substringAfter(">").trim())
    }
}

private fun AnnotatedString.Builder.appendInlineMarkdown(text: String) {
    var i = 0
    while (i < text.length) {
        when {
            text.startsWith("![[", i) -> {
                val end = text.indexOf("]]", i + 3)
                if (end != -1) {
                    withStyle(SpanStyle(color = MdTertiary, fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline)) {
                        append(">> " + text.substring(i + 3, end))
                    }
                    i = end + 2
                } else { append(text[i]); i++ }
            }
            text.startsWith("[[", i) -> {
                val end = text.indexOf("]]", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(color = MdAccent, fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline)) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else { append(text[i]); i++ }
            }
            text.startsWith("[", i) && !text.startsWith("[[", i) -> {
                val closeBracket = text.indexOf("]", i + 1)
                val openParen = if (closeBracket != -1) text.indexOf("(", closeBracket) else -1
                val closeParen = if (openParen == closeBracket + 1) text.indexOf(")", openParen + 1) else -1
                if (closeBracket != -1 && openParen == closeBracket + 1 && closeParen != -1) {
                    val linkText = text.substring(i + 1, closeBracket)
                    withStyle(SpanStyle(color = MdCalloutInfo,
                        textDecoration = TextDecoration.Underline)) {
                        append(linkText)
                    }
                    i = closeParen + 1
                } else { append(text[i]); i++ }
            }
            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MdWhite)) {
                        appendInlineMarkdown(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else { append(text[i]); i++ }
            }
            text.startsWith("~~", i) -> {
                val end = text.indexOf("~~", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough, color = MdGray)) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else { append(text[i]); i++ }
            }
            text.startsWith("==", i) -> {
                val end = text.indexOf("==", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(background = MdPrimary.copy(alpha = 0.25f), color = MdWhite)) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else { append(text[i]); i++ }
            }
            text.startsWith("*", i) && !text.startsWith("**", i) -> {
                val end = text.indexOf("*", i + 1)
                if (end != -1 && !text.startsWith("*", end + 1)) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = MdWhite)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else { append(text[i]); i++ }
            }
            text.startsWith("`", i) && !text.startsWith("```", i) -> {
                val end = text.indexOf("`", i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace,
                        color = MdTertiary, background = MdSurface)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else { append(text[i]); i++ }
            }
            text.startsWith("%%", i) -> {
                val end = text.indexOf("%%", i + 2)
                if (end != -1) {
                    i = end + 2
                } else { append(text[i]); i++ }
            }
            text[i] == '#' && (i == 0 || text[i-1] == ' ') -> {
                val end = text.indexOf(' ', i + 1).let { if (it == -1) text.length else it }
                withStyle(SpanStyle(color = MdAccent, fontWeight = FontWeight.Bold)) {
                    append(text.substring(i, end))
                }
                i = end
            }
            else -> { append(text[i]); i++ }
        }
    }
}

class MarkdownVisualTransformation(
    private val primaryColor: Color = MdPrimary,
    private val onSurface: Color = MdWhite
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val annotated = buildMarkdownAnnotated(text.text)
        return TransformedText(annotated, OffsetMapping.Identity)
    }
}
