package com.noteflow.app.features.tags.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.tags.domain.model.Tag

private val BgColor     = Color(0xFF131313)
private val SurfaceColor= Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor= Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)

// ─── الشاشة الرئيسية ───
@Composable
fun TagDashboardScreen(
    onBack: () -> Unit,
    viewModel: TagViewModel = hiltViewModel()
) {
    val allTags by viewModel.allTags.collectAsState()
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMergeDialog  by remember { mutableStateOf(false) }
    var selectedTag      by remember { mutableStateOf<Tag?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TagDashboardHeader(onBack = onBack)
            if (allTags.isEmpty()) {
                TagEmptyState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allTags, key = { it.id }) { tag ->
                        TagDashboardCard(
                            tag = tag,
                            onRename = { selectedTag = tag; showRenameDialog = true },
                            onDelete = { selectedTag = tag; showDeleteDialog = true },
                            onMerge  = { selectedTag = tag; showMergeDialog  = true }
                        )
                    }
                }
            }
        }

        if (showRenameDialog && selectedTag != null) {
            TagRenameDialog(
                tag = selectedTag!!,
                onConfirm = { newName ->
                    viewModel.renameTag(selectedTag!!.id, newName)
                    showRenameDialog = false
                },
                onDismiss = { showRenameDialog = false }
            )
        }
        if (showDeleteDialog && selectedTag != null) {
            TagDeleteDialog(
                tag = selectedTag!!,
                onConfirm = {
                    viewModel.deleteTag(selectedTag!!.id)
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
        if (showMergeDialog && selectedTag != null) {
            TagMergeDialog(
                sourceTag = selectedTag!!,
                allTags   = allTags.filter { it.id != selectedTag!!.id },
                onConfirm = { targetId ->
                    viewModel.mergeTags(selectedTag!!.id, targetId)
                    showMergeDialog = false
                },
                onDismiss = { showMergeDialog = false }
            )
        }
    }
}

// ─── Header ───
@Composable
fun TagDashboardHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                tint = Color.White.copy(alpha = 0.8f))
        }
        Text(
            text = "Tags",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

// ─── Empty State ───
@Composable
fun TagEmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Tag, contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(12.dp))
            Text("مفيش tags لسه",
                color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp)
            Text("اكتب #tagname في أي ملاحظة",
                color = Color.White.copy(alpha = 0.2f), fontSize = 12.sp)
        }
    }
}

// ─── Card ───
@Composable
fun TagDashboardCard(
    tag: Tag,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onMerge:  () -> Unit
) {
    val chipColor = try {
        Color(android.graphics.Color.parseColor(tag.color))
    } catch (e: Exception) { AccentColor }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(chipColor)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("#${tag.name}", color = PrimaryColor,
                fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text("${tag.usageCount} استخدام",
                color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp)
        }
        IconButton(onClick = onRename, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Rename",
                tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onMerge, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.MergeType, contentDescription = "Merge",
                tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete",
                tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
        }
    }
}

// ─── Rename Dialog ───
@Composable
fun TagRenameDialog(tag: Tag, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(tag.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceHigh,
        title = { Text("تغيير اسم التاج", color = Color.White, fontSize = 16.sp) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onConfirm(text.trim()) }) {
                Text("حفظ", color = AccentColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.White.copy(alpha = 0.5f))
            }
        }
    )
}

// ─── Delete Dialog ───
@Composable
fun TagDeleteDialog(tag: Tag, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceHigh,
        title = { Text("حذف التاج", color = Color.White, fontSize = 16.sp) },
        text = { Text("هتحذف #${tag.name} من كل الملاحظات والمهام؟",
            color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("حذف", color = Color(0xFFFF6B6B))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.White.copy(alpha = 0.5f))
            }
        }
    )
}

// ─── Merge Dialog ───
@Composable
fun TagMergeDialog(
    sourceTag: Tag,
    allTags: List<Tag>,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTargetId by remember { mutableStateOf<Long?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceHigh,
        title = { Text("دمج #${sourceTag.name} مع...", color = Color.White, fontSize = 16.sp) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                items(allTags, key = { it.id }) { tag ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTargetId = tag.id }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTargetId == tag.id,
                            onClick = { selectedTargetId = tag.id },
                            colors = RadioButtonDefaults.colors(selectedColor = AccentColor)
                        )
                        Text("#${tag.name}", color = PrimaryColor, fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { selectedTargetId?.let { onConfirm(it) } },
                enabled = selectedTargetId != null) {
                Text("دمج", color = AccentColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.White.copy(alpha = 0.5f))
            }
        }
    )
}
