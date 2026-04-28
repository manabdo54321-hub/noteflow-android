package com.noteflow.app.features.graph.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
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
import com.noteflow.app.features.graph.domain.GraphMode
import com.noteflow.app.features.graph.domain.GraphNode
import com.noteflow.app.features.graph.domain.NodeType

private val SurfaceColor   = Color(0xFF1C1B1B)
private val SurfaceVariant = Color(0xFF353534)
private val PrimaryColor   = Color(0xFFCABEFF)
private val AccentColor    = Color(0xFF8A70FF)
private val OutlineVariant = Color(0xFF48454F)
private val TopBarBg       = Color(0xFF0E0E0E)
private val IconGray       = Color(0xFF71717A)

@Composable
fun GraphTopBar(
    modifier: Modifier = Modifier,
    currentMode: GraphMode,
    showSearch: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onBack: () -> Unit,
    onExitFocus: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TopBarBg.copy(alpha = 0.85f))
            .border(width = 1.dp, color = SurfaceColor, shape = RoundedCornerShape(0.dp))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = IconGray)
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
                    Icon(Icons.Default.Close, contentDescription = null, tint = IconGray)
                }
            }
        }
        if (showSearch) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(SurfaceColor)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(50.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = IconGray, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = Color(0xFFE5E2E1), fontSize = 14.sp),
                    cursorBrush = SolidColor(PrimaryColor),
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) {
                            Text("Filter nodes...", color = IconGray, fontSize = 14.sp)
                        }
                        inner()
                    }
                )
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                }
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
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(SurfaceColor.copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(50.dp))
            .padding(vertical = 6.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        ZoomBtn(label = "+", onClick = onZoomIn)
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(1.dp)
                .background(OutlineVariant.copy(alpha = 0.5f))
        )
        ZoomBtn(label = "-", onClick = onZoomOut)
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(1.dp)
                .background(OutlineVariant.copy(alpha = 0.5f))
        )
        ZoomBtn(label = "o", onClick = onCenter, isCenter = true)
    }
}

@Composable
private fun ZoomBtn(label: String, onClick: () -> Unit, isCenter: Boolean = false) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isCenter) {
            Icon(
                Icons.Default.FilterCenterFocus,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(label, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
fun GraphBottomBar(
    modifier: Modifier = Modifier,
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(SurfaceColor.copy(alpha = 0.92f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(50.dp))
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarBtn(
                icon = { Icon(Icons.Default.AccountTree, contentDescription = null, tint = if (selectedTab == 0) Color(0xFF0E0E0E) else IconGray, modifier = Modifier.size(22.dp)) },
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            BottomBarBtn(
                icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = if (selectedTab == 1) Color(0xFF0E0E0E) else IconGray, modifier = Modifier.size(22.dp)) },
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            BottomBarBtn(
                icon = { Icon(Icons.Default.Search, contentDescription = null, tint = if (selectedTab == 2) Color(0xFF0E0E0E) else IconGray, modifier = Modifier.size(22.dp)) },
                selected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            BottomBarBtn(
                icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = if (selectedTab == 3) Color(0xFF0E0E0E) else IconGray, modifier = Modifier.size(22.dp)) },
                selected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
private fun BottomBarBtn(
    icon: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(
                if (selected) androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(PrimaryColor, AccentColor)
                ) else androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
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
            Text(node.label, color = Color(0xFFC9C4D0), fontSize = 12.sp, maxLines = 3)
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
                Text(linked.label.take(20), color = Color(0xFFE5E2E1), fontSize = 12.sp)
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
