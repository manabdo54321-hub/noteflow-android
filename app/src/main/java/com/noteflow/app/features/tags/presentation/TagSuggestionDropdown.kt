package com.noteflow.app.features.tags.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noteflow.app.features.tags.domain.model.Tag

// ألوان التطبيق
private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)

// ─────────────────────────────────────────
// الـ Component الرئيسي
// ─────────────────────────────────────────
@Composable
fun TagSuggestionDropdown(
    query: String,                          // النص الحالي في الـ TextField
    suggestions: List<Tag>,                 // من TagViewModel.suggestions
    selectedTags: List<Tag>,                // التاجز المختارة حالياً
    onQueryChange: (String) -> Unit,        // لما يغير الـ query
    onTagSelected: (Tag) -> Unit,           // لما يختار تاج من الـ suggestions
    onTagRemoved: (Tag) -> Unit,            // لما يضغط X على تاج مختار
    modifier: Modifier = Modifier
) {
    // استخرج الـ prefix الحالي (الكلمة بعد آخر #)
    val currentPrefix = remember(query) {
        extractCurrentTagPrefix(query)
    }

    val showSuggestions = suggestions.isNotEmpty() && currentPrefix != null

    Column(modifier = modifier) {

        // ── التاجز المختارة (Chips أفقية) ──
        if (selectedTags.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                items(selectedTags, key = { it.id }) { tag ->
                    SelectedTagChip(tag = tag, onRemove = { onTagRemoved(tag) })
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // ── قائمة الـ Suggestions ──
        AnimatedVisibility(
            visible = showSuggestions,
            enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
        ) {
            SuggestionsList(
                suggestions = suggestions,
                onTagSelected = { tag ->
                    // استبدل الـ prefix الحالي بالتاج المختار
                    val newQuery = replaceCurrentPrefix(query, tag.name)
                    onQueryChange(newQuery)
                    onTagSelected(tag)
                }
            )
        }
    }
}

// ─────────────────────────────────────────
// قائمة الـ Suggestions
// ─────────────────────────────────────────
@Composable
private fun SuggestionsList(
    suggestions: List<Tag>,
    onTagSelected: (Tag) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceHigh)
            .padding(vertical = 4.dp)
    ) {
        suggestions.forEach { tag ->
            SuggestionItem(tag = tag, onClick = { onTagSelected(tag) })
        }
    }
}

@Composable
private fun SuggestionItem(
    tag: Tag,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Tag,
            contentDescription = null,
            tint = AccentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = tag.name,
            color = PrimaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        if (tag.usageCount > 0) {
            Text(
                text = "${tag.usageCount}",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 11.sp
            )
        }
    }
}

// ─────────────────────────────────────────
// Chip للتاج المختار
// ─────────────────────────────────────────
@Composable
private fun SelectedTagChip(
    tag: Tag,
    onRemove: () -> Unit
) {
    val chipColor = if (tag.color != null) {
        try { Color(android.graphics.Color.parseColor(tag.color)) }
        catch (e: Exception) { AccentColor }
    } else AccentColor

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(chipColor.copy(alpha = 0.15f))
            .padding(start = 10.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "#${tag.name}",
            color = chipColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = chipColor.copy(alpha = 0.7f),
            modifier = Modifier
                .size(14.dp)
                .clickable(onClick = onRemove)
        )
    }
}

// ─────────────────────────────────────────
// Helper Functions
// ─────────────────────────────────────────

// بيرجع الـ prefix بعد آخر # في النص
fun extractCurrentTagPrefix(text: String): String? {
    val lastHash = text.lastIndexOf('#')
    if (lastHash == -1) return null
    val afterHash = text.substring(lastHash + 1)
    // لو فيه مسافة بعد الـ # → مش بيكتب تاج
    if (afterHash.contains(' ') || afterHash.contains('\n')) return null
    return afterHash
}

// بيستبدل الـ prefix الحالي بالتاج المختار
fun replaceCurrentPrefix(text: String, tagName: String): String {
    val lastHash = text.lastIndexOf('#')
    if (lastHash == -1) return text
    return text.substring(0, lastHash) + "#$tagName "
}
