package com.noteflow.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

private val AccentColor   = Color(0xFF8A70FF)
private val TertiaryColor = Color(0xFF75D1FF)
private val SurfaceHigh   = Color(0xFF2A2A2A)
private val PrimaryColor  = Color(0xFFCABEFF)

fun AnnotatedString.Builder.appendInlineStyles(line: String) {
    var i = 0
    while (i < line.length) {
        when {
            line.startsWith("**", i) -> {
                val end = line.indexOf("**", i + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White))
                    append(line.substring(i + 2, end))
                    pop(); i = end + 2
                } else { append(line[i]); i++ }
            }
            line.startsWith("*", i) && !line.startsWith("**", i) -> {
                val end = line.indexOf("*", i + 1)
                if (end != -1 && !line.startsWith("**", end)) {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color.White))
                    append(line.substring(i + 1, end))
                    pop(); i = end + 1
                } else { append(line[i]); i++ }
            }
            line.startsWith("~~", i) -> {
                val end = line.indexOf("~~", i + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.White.copy(alpha = 0.5f)
                    ))
                    append(line.substring(i + 2, end))
                    pop(); i = end + 2
                } else { append(line[i]); i++ }
            }
            line.startsWith("==", i) -> {
                val end = line.indexOf("==", i + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(
                        background = AccentColor.copy(alpha = 0.3f),
                        color = Color.White
                    ))
                    append(line.substring(i + 2, end))
                    pop(); i = end + 2
                } else { append(line[i]); i++ }
            }
            line.startsWith("`", i) && !line.startsWith("```", i) -> {
                val end = line.indexOf("`", i + 1)
                if (end != -1) {
                    pushStyle(SpanStyle(
                        background = SurfaceHigh,
                        color = TertiaryColor,
                        fontFamily = FontFamily.Monospace
                    ))
                    append(line.substring(i + 1, end))
                    pop(); i = end + 1
                } else { append(line[i]); i++ }
            }
            line.startsWith("[[", i) -> {
                val end = line.indexOf("]]", i + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    ))
                    append(line.substring(i + 2, end))
                    pop(); i = end + 2
                } else { append(line[i]); i++ }
            }
            line[i] == '#' && (i == 0 || line[i - 1] == ' ') -> {
                val end = line.indexOf(' ', i + 1).let {
                    if (it == -1) line.length else it
                }
                pushStyle(SpanStyle(color = AccentColor, fontWeight = FontWeight.Medium))
                append(line.substring(i, end))
                pop(); i = end
            }
            else -> { append(line[i]); i++ }
        }
    }
}
