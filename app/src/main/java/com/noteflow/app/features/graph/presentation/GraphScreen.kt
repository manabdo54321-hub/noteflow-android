package com.noteflow.app.features.graph.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

private val BgColor = Color(0xFF131313)

@Composable
fun GraphScreen(
    onBack: () -> Unit,
    viewModel: GraphViewModel = hiltViewModel()
) {
    val nodes      by viewModel.nodes.collectAsState()
    val edges      by viewModel.edges.collectAsState()
    val graphState by viewModel.graphState.collectAsState()
    val scope      = rememberCoroutineScope()

    var zoom    by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var canvasW by remember { mutableStateOf(0f) }
    var canvasH by remember { mutableStateOf(0f) }

    var selectedNode by remember {
        mutableStateOf<com.noteflow.app.features.graph.domain.GraphNode?>(null)
    }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        GraphCanvas(
            nodes          = nodes,
            edges          = edges,
            focusedNodeId  = graphState.focusedNodeId,
            zoom           = zoom,
            offsetX        = offsetX,
            offsetY        = offsetY,
            onZoomChange   = { zoom = it },
            onOffsetChange = { x, y -> offsetX = x; offsetY = y },
            onCanvasSizeChange = { w, h -> canvasW = w; canvasH = h },
            onNodeTap = { hit ->
                selectedNode = hit
                viewModel.setFocusedNode(hit?.id)
            },
            onNodeLongPress = { hit ->
                if (hit != null) {
                    viewModel.setFocusedNode(hit.id)
                    viewModel.setMode(
                        com.noteflow.app.features.graph.domain.GraphMode.FOCUS
                    )
                }
            }
        )

        GraphTopBar(
            modifier       = Modifier.align(Alignment.TopStart),
            currentMode    = graphState.currentMode,
            onBack         = onBack,
            onExitFocus    = {
                viewModel.setFocusedNode(null)
                viewModel.setMode(
                    com.noteflow.app.features.graph.domain.GraphMode.NORMAL
                )
                selectedNode = null
            }
        )

        GraphZoomControls(
            modifier  = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 120.dp),
            onZoomIn  = { zoom = (zoom * 1.2f).coerceAtMost(3f) },
            onZoomOut = { zoom = (zoom / 1.2f).coerceAtLeast(0.3f) },
            onCenter  = { zoom = 1f; offsetX = 0f; offsetY = 0f }
        )

        if (selectedNode != null) {
            val linked = remember(selectedNode, edges, nodes) {
                val nodeId = selectedNode!!.id
                val linkedIds = edges
                    .filter { it.from == nodeId || it.to == nodeId }
                    .map { if (it.from == nodeId) it.to else it.from }
                    .toSet()
                nodes.filter { it.id in linkedIds }
            }
            GraphNodeDetailsPanel(
                node            = selectedNode!!,
                connectionCount = connectionCounts[selectedNode!!.id] ?: 0,
                linkedNodes     = linked,
                modifier        = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 80.dp)
            )
        }

        GraphBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
