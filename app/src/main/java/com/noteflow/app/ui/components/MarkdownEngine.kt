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
        append(text)
        var pos = 0
        val lines = text.split("\n")
        for (line in lines) {
            val lineStart = pos
            val lineEnd = pos + line.length
            when {
                line.startsWith("###### ") -> addStyle(SpanStyle(color = MdPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                line.startsWith("##### ")  -> addStyle(SpanStyle(color = MdPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                line.startsWith("#### ")   -> addStyle(SpanStyle(color = MdPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                line.startsWith("### ")    -> addStyle(SpanStyle(color = MdPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                line.startsWith("## ")     -> addStyle(SpanStyle(color = MdPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                line.startsWith("# ")      -> addStyle(SpanStyle(color = MdPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold), lineStart, lineEnd)
                line.startsWith("> ")      -> addStyle(SpanStyle(color = MdGray), lineStart, lineEnd)
                line.startsWith("- [ ] ") || line.startsWith("- [x] ") -> addStyle(SpanStyle(color = MdWhite), lineStart, lineEnd)
                line.startsWith("- ")      -> addStyle(SpanStyle(color = MdWhite), lineStart, lineEnd)
            }
            val inlinePatterns = listOf(
                Regex("""\*\*(.+?)\*\*""") to Pair(SpanStyle(fontWeight = FontWeight.Bold, color = MdWhite), true),
                Regex("""~~(.+?)~~""")      to Pair(SpanStyle(color = MdGray, textDecoration = TextDecoration.LineThrough), true),
                Regex("""==(.+?)==""")      to Pair(SpanStyle(background = MdPrimary.copy(0.3f), color = MdWhite), true),
                Regex("""\*(.+?)\*""")      to Pair(SpanStyle(fontStyle = FontStyle.Italic, color = MdWhite), true),
                Regex("""`(.+?)`""")        to Pair(SpanStyle(color = MdTertiary, background = MdSurface), true),
                Regex("""#\w+""")           to Pair(SpanStyle(color = MdPrimary), false),
                Regex("""\[\[(.+?)]]""")    to Pair(SpanStyle(color = MdPrimary, textDecoration = TextDecoration.Underline), false)
            )
            for ((regex, pair) in inlinePatterns) {
                regex.findAll(line).forEach { match ->
                    addStyle(pair.first, lineStart + match.range.first, lineStart + match.range.last + 1)
                }
            }
            pos += line.length + 1
        }
    }
}


class MarkdownVisualTransformation(
    private val primaryColor: Color = MdPrimary,
    private val onSurface: Color = MdWhite
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val annotated = buildMarkdownColorsOnly(text.text)
        return TransformedText(annotated, OffsetMapping.Identity)
    }
}

fun buildMarkdownColorsOnly(text: String): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        val patterns = listOf(
            Regex("""\*\*(.+?)\*\*""") to SpanStyle(color = MdWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            Regex("""\*(.+?)\*""") to SpanStyle(color = MdWhite, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
            Regex("""~~(.+?)~~""") to SpanStyle(color = MdGray, textDecoration = TextDecoration.LineThrough),
            Regex("""==(.+?)==""") to SpanStyle(background = MdPrimary.copy(alpha = 0.3f), color = MdWhite),
            Regex("""`(.+?)`""") to SpanStyle(color = MdTertiary, background = MdSurface),
            Regex("""#\w+""") to SpanStyle(color = MdPrimary),
            Regex("""\[\[(.+?)]]""") to SpanStyle(color = MdPrimary, textDecoration = TextDecoration.Underline),
            Regex("""^#{1,6} .+""", RegexOption.MULTILINE) to SpanStyle(color = MdPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            Regex("^> .+", RegexOption.MULTILINE) to SpanStyle(color = MdGray),
            Regex("^- .+", RegexOption.MULTILINE) to SpanStyle(color = MdWhite),
            Regex("^\d+\. .+", RegexOption.MULTILINE) to SpanStyle(color = MdWhite)
        )
        for ((regex, style) in patterns) {
            regex.findAll(text).forEach { match ->
                addStyle(style, match.range.first, match.range.last + 1)
            }
        }
    }
}
