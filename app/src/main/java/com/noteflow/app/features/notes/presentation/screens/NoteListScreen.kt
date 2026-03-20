package com.noteflow.app.features.notes.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.notes.presentation.NoteViewModel

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val OutlineVariant = Color(0xFF47464C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    onNoteClick: (Long) -> Unit,
    onAddNote: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }
    val filters = listOf("الكل", "شخصي", "عمل", "أفكار", "أرشيف")

    val filtered = if (searchQuery.isBlank()) notes
        else notes.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.content.contains(searchQuery, ignoreCase = true)
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = OnSurfaceVariant)
                Text("ملاحظات", fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = Color.White)
                Icon(Icons.Default.Add, contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.clickable { onAddNote() })
            }
        }

        // Search
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceColor)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White, fontSize = 14.sp
                        ),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) {
                                Text("ابحث في الملاحظات...",
                                    color = OnSurfaceVariant, fontSize = 14.sp)
                            }
                            inner()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Graph View button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountTree, contentDescription = null,
                        tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Filters
        item {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected)
                                    Brush.horizontalGradient(listOf(PrimaryColor, AccentColor))
                                else
                                    Brush.horizontalGradient(listOf(SurfaceColor, SurfaceColor))
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            color = if (isSelected) Color(0xFF1C0062) else OnSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Notes List
        if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✨", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("لا توجد ملاحظات", color = OnSurfaceVariant, fontSize = 16.sp)
                    }
                }
            }
        } else {
            items(filtered, key = { it.id }) { note ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceColor)
                        .clickable { onNoteClick(note.id) }
                        .padding(16.dp)
                ) {
                    // عنوان
                    Text(
                        text = note.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    if (note.content.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = note.content.take(120) +
                                if (note.content.length > 120) "..." else "",
                            fontSize = 13.sp,
                            color = OnSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // الـ divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(OutlineVariant.copy(alpha = 0.3f))
                    )
                }
            }
        }
    }
}
