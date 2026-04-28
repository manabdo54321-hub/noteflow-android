package com.noteflow.app.features.graph.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import com.noteflow.app.features.graph.domain.GraphMode
import com.noteflow.app.features.graph.domain.GraphNode
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@Composable
fun GraphCanvas(
    nodes: List<GraphNode>,
    edges: List<com.noteflow.app.features.graph.domain.Edge>,
    focusedNodeId: String?,
    zoom: Float,
    offsetX: Float,
    offsetY: Float,
    onZoomChange: (Float) -> Unit,
    onOffsetChange: (Float, Float) -> Unit,
    onCanvasSizeChange: (Float, Float) -> Unit,
    onNodeTap: (GraphNode?) -> Unit,
    onNodeLongPress: (GraphNode?) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val settings = remember { GraphSettings() }

    val connectionCounts = remember(edges) {
        val map = mutableMapOf<String, Int>()
        edges.forEach { e ->
            map[e.from] = (map[e.from] ?: 0) + 1
            map[e.to]   = (map[e.to]   ?: 0) + 1
        }
        map
    }

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoomChange, _ ->
                    onZoomChange((zoom * zoomChange).coerceIn(0.3f, 3f))
                    onOffsetChange(offsetX + pan.x, offsetY + pan.y)
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
                            sqrt((dx * dx + dy * dy).toDouble()) < r
                        }
                        onNodeTap(hit)
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
                            sqrt((dx * dx + dy * dy).toDouble()) < r
                        }
                        onNodeLongPress(hit)
                    }
                )
            }
            .graphicsLayer {
                onCanvasSizeChange(size.width, size.height)
                scaleX = zoom
                scaleY = zoom
                translationX = offsetX
                translationY = offsetY
            }
    ) {
        onCanvasSizeChange(size.width, size.height)
        val renderEdges = buildRenderEdges(edges, nodes, focusedNodeId, settings.edgeMode)
        val renderNodes = buildRenderNodes(nodes, focusedNodeId, connectionCounts)
        drawGraph(renderNodes, renderEdges, settings, textMeasurer)
    }
}
