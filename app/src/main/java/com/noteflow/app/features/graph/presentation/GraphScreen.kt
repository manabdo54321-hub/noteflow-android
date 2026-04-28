package com.noteflow.app.features.graph.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

private val BgColor      = Color(0xFF131313)
private val SurfaceColor = Color(0xFF1C1B1B)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor  = Color(0xFF8A70FF)

@Composable
fun GraphScreen(
    onBack: () -> Unit,
    viewModel: GraphViewModel = hiltViewModel()
) {
    val nodes        by viewModel.nodes.collectAsState()
    val edges        by viewModel.edges.collectAsState()
    val graphState   by viewModel.graphState.collectAsState()
    val scope        = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    var zoom         by remember { mutableStateOf(1f) }
    var offsetX      by remember { mutableStateOf(0f) }
    var offsetY      by remember { mutableStateOf(0f) }
    var canvasW      by remember { mutableStateOf(0f) }
    var canvasH      by remember { mutableStateOf(0f) }
    var selectedNode by remember { mutableStateOf<com.noteflow.app.features.graph.domain.GraphNode?>(null) }
    val settings     = remember { GraphSettings() }

    val connectionCounts = remember(edges) {
        val map = mutableMapOf<String, Int>()
        edges.forEach { e ->
            map[e.from] = (map[e.from] ?: 0) + 1
            map[e.to]   = (map[e.to]   ?: 0) + 1
        }
        map
    }

    LaunchedEffect(canvasW, canvasH) {
        if (canvasW > 0f && canvasH > 0f) {
            scope.launch {
                while (true) {
                    withFrameMillis {
                        val maxSpeed = nodes.maxOfOrNull { n ->
                            val vx = n.x - n.prevX
                            val vy = n.y - n.prevY
                            kotlin.math.sqrt((vx * vx + vy * vy).toDouble()).toFloat()
                        } ?: 0f
                        if (maxSpeed > 0.05f) {
                            viewModel.tickPhysics(canvasW, canvasH)
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomChange, _ ->
                        zoom = (zoom * zoomChange).coerceIn(0.3f, 3f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
                .pointerInput(nodes) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            val worldX = (tapOffset.x - offsetX) / zoom
                            val worldY = (tapOffset.y - offsetY) / zoom
                            val hit = nodes.firstOrNull { node ->
                                val dx = node.x - worldX
                                val dy = node.y - worldY
                                val r = (20f + kotlin.math.ln(
                                    ((connectionCounts[node.id] ?: 0) + 1).toDouble()
                                ).toFloat() * 8f).coerceIn(20f, 60f)
                                kotlin.math.sqrt((dx * dx + dy * dy).toDouble()) < r
                            }
                            selectedNode = hit
                            viewModel.setFocusedNode(hit?.id)
                        },
                        onLongPress = { tapOffset ->
                            val worldX = (tapOffset.x - offsetX) / zoom
                            val worldY = (tapOffset.y - offsetY) / zoom
                            val hit = nodes.firstOrNull { node ->
                                val dx = node.x - worldX
                                val dy = node.y - worldY
                                val r = (20f + kotlin.math.ln(
                                    ((connectionCounts[node.id] ?: 0) + 1).toDouble()
                                ).toFloat() * 8f).coerceIn(20f, 60f)
                                kotlin.math.sqrt((dx * dx + dy * dy).toDouble()) < r
                            }
                            if (hit != null) {
                                viewModel.setFocusedNode(hit.id)
                                viewModel.setMode(com.noteflow.app.features.graph.domain.GraphMode.FOCUS)
                            }
                        }
                    )
                }
                .graphicsLayer {
                    canvasW = size.width
                    canvasH = size.height
                    scaleX = zoom
                    scaleY = zoom
                    translationX = offsetX
                    translationY = offsetY
                }
        ) {
            canvasW = size.width
            canvasH = size.height
            val renderEdges = buildRenderEdges(
                edges, nodes, graphState.focusedNodeId, settings.edgeMode
            )
            val renderNodes = buildRenderNodes(
                nodes, graphState.focusedNodeId, connectionCounts
            )
            drawGraph(renderNodes, renderEdges, settings, textMeasurer)
        }

        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = PrimaryColor)
            }
            Text(
                "الخريطة",
                color = PrimaryColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (graphState.currentMode == com.noteflow.app.features.graph.domain.GraphMode.FOCUS) {
                IconButton(onClick = {
                    viewModel.setFocusedNode(null)
                    viewModel.setMode(com.noteflow.app.features.graph.domain.GraphMode.NORMAL)
                    selectedNode = null
                }) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = PrimaryColor)
                }
            }
        }

        // Zoom Controls
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceColor.copy(alpha = 0.9f))
        ) {
            IconButton(onClick = { zoom = (zoom * 1.2f).coerceAtMost(3f) }) {
                Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.1f))
            )
            IconButton(onClick = { zoom = (zoom / 1.2f).coerceAtLeast(0.3f) }) {
                Text("-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Center Button
        IconButton(
            onClick = { zoom = 1f; offsetX = 0f; offsetY = 0f },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp)
                .offset(y = (-80).dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceColor.copy(alpha = 0.9f))
                .size(44.dp)
        ) {
            Icon(Icons.Default.CenterFocusWeak, contentDescription = null, tint = PrimaryColor)
        }

        // Bottom Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-16).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceColor.copy(alpha = 0.95f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                graphState.currentMode.name,
                color = AccentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${nodes.size} node | ${edges.size} edge",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }

        // Node Preview
        selectedNode?.let { node ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .offset(y = (-70).dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceColor)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        node.label,
                        color = PrimaryColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        node.type.name + " - " + (connectionCounts[node.id] ?: 0) + " روابط",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        selectedNode = null
                        viewModel.setFocusedNode(null)
                    }) {
                        Text("اغلاق", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
