package com.noteflow.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor  = Color(0xFF8A70FF)

class MarkdownVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            buildMarkdownAnnotated(text.text),
            OffsetMapping.Identity
        )
    }
}

fun buildMarkdownAnnotated(text: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        lines.forEachIndexed { lineIndex, line ->
            when {
                line.startsWith("###### ") -> {
                    pushStyle(SpanStyle(color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 13.sp))
                    append(line); pop()
                }
                line.startsWith("##### ") -> {
                    pushStyle(SpanStyle(color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 15.sp))
                    append(line); pop()
                }
                line.startsWith("#### ") -> {
                    pushStyle(SpanStyle(color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 17.sp))
                    append(line); pop()
                }
                line.startsWith("### ") -> {
                    pushStyle(SpanStyle(color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 19.sp))
                    append(line); pop()
                }
                line.startsWith("## ") -> {
                    pushStyle(SpanStyle(color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 22.sp))
                    append(line); pop()
                }
                line.startsWith("# ") -> {
                    pushStyle(SpanStyle(color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 26.sp))
                    append(line); pop()
                }
                line.startsWith("> ") -> {
                    pushStyle(SpanStyle(color = Color(0xFFC8C5CD), fontStyle = FontStyle.Italic))
                    append(line); pop()
                }
                line.trimStart().startsWith("---") -> {
                    pushStyle(SpanStyle(color = Color(0xFF47464C)))
                    append(line); pop()
                }
                line.startsWith("- [x] ") || line.startsWith("- [X] ") -> {
                    pushStyle(SpanStyle(
                        color = PrimaryColor.copy(alpha = 0.5f),
                        textDecoration = TextDecoration.LineThrough
                    ))
                    append(line); pop()
                }
                line.startsWith("- [ ] ") -> {
                    pushStyle(SpanStyle(color = Color(0xFFC8C5CD)))
                    append(line); pop()
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    pushStyle(SpanStyle(color = Color(0xFFC8C5CD)))
                    append(line); pop()
                }
                line.matches(Regex("^\\d+\\. .*")) -> {
                    pushStyle(SpanStyle(color = Color(0xFFC8C5CD)))
                    append(line); pop()
                }
                else -> appendInlineStyles(line)
            }
            if (lineIndex < lines.size - 1) append("\n")
        }
    }
}
