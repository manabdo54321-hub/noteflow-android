package com.noteflow.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

val MdPrimary = Color(0xFFCABEFF)
val MdAccent = Color(0xFF8A70FF)
val MdTertiary = Color(0xFF75D1FF)
val MdSurface = Color(0xFF2A2A2A)
val MdError = Color(0xFFFF6B6B)
val MdWhite = Color(0xFFE5E2E1)
val MdGray = Color(0xFFC8C5CD)

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
        line.startsWith("# ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)) {
                append(line.removePrefix("# "))
            }
        }
        line.startsWith("## ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)) {
                append(line.removePrefix("## "))
            }
        }
        line.startsWith("### ") -> {
            withStyle(SpanStyle(color = MdPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)) {
                append(line.removePrefix("### "))
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

private fun AnnotatedString.Builder.appendInlineMarkdown(text: String) {
    var i = 0
    while (i < text.length) {
        when {
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
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace, color = MdTertiary, background = MdSurface)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else { append(text[i]); i++ }
            }
            text.startsWith("[[", i) -> {
                val end = text.indexOf("]]", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(color = MdAccent, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else { append(text[i]); i++ }
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
