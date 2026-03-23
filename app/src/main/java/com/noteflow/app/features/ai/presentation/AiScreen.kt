package com.noteflow.app.features.ai.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

private val BgColor = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val TertiaryColor = Color(0xFF75D1FF)
private val OnSurface = Color(0xFFE5E2E1)
private val OnSurfaceVariant = Color(0xFFC8C5CD)

private val quickPrompts = listOf(
    "لخص ملاحظاتي",
    "إيه مهامي النشطة؟",
    "ساعدني أركز",
    "اقترح أفكار جديدة",
    "رتب أولوياتي",
    "حفزني"
)

@Composable
fun AiScreen(
    onBack: () -> Unit,
    viewModel: AiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(uiState.messages.size - 1) }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BgColor)
    ) {
        AiTopBar(onBack = onBack, onClear = viewModel::clearChat)

        if (uiState.messages.isEmpty()) {
            AiWelcomeScreen(
                onPromptClick = { prompt ->
                    viewModel.sendMessage(prompt)
                }
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.messages) { message ->
                    AiMessageBubble(message = message)
                }
                if (uiState.isLoading) {
                    item { AiTypingIndicator() }
                }
            }
        }

        uiState.error?.let { error ->
            AiErrorBar(error = error, onDismiss = viewModel::clearError)
        }

        AiQuickPrompts(
            visible = uiState.messages.isEmpty().not() && !uiState.isLoading,
            onPromptClick = { viewModel.sendMessage(it) }
        )

        AiInputBar(
            text = inputText,
            isLoading = uiState.isLoading,
            onTextChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                }
            }
        )
    }
}

@Composable
private fun AiTopBar(onBack: () -> Unit, onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceColor)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = OnSurfaceVariant)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF4CAF50))
            )
            Text("مساعد NoteFlow", fontSize = 16.sp,
                fontWeight = FontWeight.Bold, color = OnSurface)
        }
        IconButton(onClick = onClear) {
            Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = OnSurfaceVariant)
        }
    }
}

@Composable
private fun AiWelcomeScreen(onPromptClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AccentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null,
                tint = AccentColor, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("مرحباً! أنا مساعدك الذكي", fontSize = 20.sp,
            fontWeight = FontWeight.Bold, color = OnSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text("اسألني عن ملاحظاتك، مهامك، أو أي حاجة تانية",
            fontSize = 14.sp, color = OnSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))
        quickPrompts.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceColor)
                            .clickable { onPromptClick(prompt) }
                            .padding(12.dp)
                    ) {
                        Text(prompt, fontSize = 13.sp, color = PrimaryColor,
                            fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AiMessageBubble(message: com.noteflow.app.features.ai.data.AiMessage) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null,
                    tint = AccentColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = if (isUser) 16.dp else 4.dp,
                        topEnd = if (isUser) 4.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(if (isUser) AccentColor.copy(alpha = 0.3f) else SurfaceColor)
                .padding(12.dp)
        ) {
            Text(message.text, fontSize = 14.sp, color = OnSurface, lineHeight = 22.sp)
        }
        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentColor.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null,
                    tint = PrimaryColor, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun AiTypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null,
                tint = AccentColor, modifier = Modifier.size(16.dp))
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = AccentColor,
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun AiQuickPrompts(visible: Boolean, onPromptClick: (String) -> Unit) {
    if (!visible) return
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(quickPrompts) { prompt ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceColor)
                    .clickable { onPromptClick(prompt) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(prompt, fontSize = 12.sp, color = PrimaryColor)
            }
        }
    }
}

@Composable
private fun AiErrorBar(error: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFF6B6B).copy(alpha = 0.15f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(error, fontSize = 12.sp, color = Color(0xFFFF6B6B), modifier = Modifier.weight(1f))
        IconButton(onClick = onDismiss, modifier = Modifier.size(20.dp)) {
            Icon(Icons.Default.Close, contentDescription = null,
                tint = Color(0xFFFF6B6B), modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun AiInputBar(
    text: String,
    isLoading: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceColor)
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceHigh)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = TextStyle(color = OnSurface, fontSize = 15.sp),
                cursorBrush = SolidColor(PrimaryColor),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (text.isEmpty()) {
                        Text("اكتب رسالتك...", color = OnSurfaceVariant, fontSize = 15.sp)
                    }
                    inner()
                }
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(if (text.isNotBlank() && !isLoading) AccentColor else SurfaceHigh)
                .clickable(enabled = text.isNotBlank() && !isLoading) { onSend() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Send, contentDescription = null,
                tint = if (text.isNotBlank() && !isLoading) Color.White else OnSurfaceVariant,
                modifier = Modifier.size(20.dp))
        }
    }
}
