package com.noteflow.app.features.graph.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noteflow.app.features.graph.domain.GraphMode
import com.noteflow.app.features.graph.domain.GraphNode
import com.noteflow.app.features.graph.domain.NodeType

private val SurfaceColor    = Color(0xFF1C1B1B)
private val SurfaceVariant  = Color(0xFF353534)
private val PrimaryColor    = Color(0xFFCABEFF)
private val TertiaryColor   = Color(0xFF75D1FF)
private val OutlineVariant  = Color(0xFF48454F)
private val TopBarBg        = Color(0xFF0E0E0E)

@Composable
fun GraphTopBar(
    modifier: Modifier = Modifier,
    currentMode: GraphMode,
    onBack: () -> Unit,
    onExitFocus: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TopBarBg.copy(alpha = 0.85f))
            .border(
                width = 1.dp,
                color = SurfaceColor,
                shape = RoundedCornerShape(0.dp)
            )
            .statusBarsPadding()
            .padding(horizontal = 12.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF71717A))
        }
        Text(
            "NOTEFLOW",
            color = PrimaryColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            modifier = Modifier.weight(1f)
        )
        if (currentMode == GraphMode.FOCUS) {
            IconButton(onClick = onExitFocus) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF71717A))
            }
        } else {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF71717A))
            }
        }
    }
}

@Composable
fun GraphZoomControls(
    modifier: Modifier = Modifier,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onCenter: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceColor.copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant)
                    .clickable { onZoomIn() },
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(1.dp)
                    .background(OutlineVariant)
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant)
                    .clickable { onZoomOut() },
                contentAlignment = Alignment.Center
            ) {
                Text("-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceColor.copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                .clickable { onCenter() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.FilterCenterFocus,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun GraphBottomBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(SurfaceColor.copy(alpha = 0.65f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(50.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GraphNavBtn {
                Icon(
                    Icons.Default.AccountTree,
                    contentDescription = null,
                    tint = Color(0xFF71717A),
                    modifier = Modifier.size(22.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(PrimaryColor, Color(0xFFA394FF))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = Color(0xFF0E0E0E),
                    modifier = Modifier.size(22.dp)
                )
            }
            GraphNavBtn {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF71717A),
                    modifier = Modifier.size(22.dp)
                )
            }
            GraphNavBtn {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = Color(0xFF71717A),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun GraphNavBtn(onClick: () -> Unit = {}, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
fun GraphNodeDetailsPanel(
    node: GraphNode,
    connectionCount: Int,
    linkedNodes: List<GraphNode>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(240.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceColor.copy(alpha = 0.65f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(nodeColorFor(node.type).copy(alpha = 0.2f))
                    .border(1.dp, nodeColorFor(node.type).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    nodeIconFor(node.type),
                    contentDescription = null,
                    tint = nodeColorFor(node.type),
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    node.label.take(22),
                    color = Color(0xFFE5E2E1),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$connectionCount connections",
                    color = Color(0xFF938F9A),
                    fontSize = 11.sp
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariant.copy(alpha = 0.5f))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Text(
                node.label,
                color = Color(0xFFC9C4D0),
                fontSize = 12.sp,
                maxLines = 3
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "LINKED NOTES",
            color = Color(0xFF938F9A),
            fontSize = 9.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        linkedNodes.take(4).forEach { linked ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {}
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    nodeIconFor(linked.type),
                    contentDescription = null,
                    tint = Color(0xFF938F9A),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    linked.label.take(20),
                    color = Color(0xFFE5E2E1),
                    fontSize = 12.sp
                )
            }
        }
    }
}

fun nodeColorFor(type: NodeType): Color = when (type) {
    NodeType.NOTE -> Color(0xFFCABEFF)
    NodeType.TASK -> Color(0xFF75D1FF)
    NodeType.GOAL -> Color(0xFFFFC870)
    NodeType.TAG  -> Color(0xFF8A70FF)
}

fun nodeIconFor(type: NodeType) = when (type) {
    NodeType.NOTE -> Icons.Default.Description
    NodeType.TASK -> Icons.Default.AccountTree
    NodeType.GOAL -> Icons.Default.FilterCenterFocus
    NodeType.TAG  -> Icons.Default.Search
}
