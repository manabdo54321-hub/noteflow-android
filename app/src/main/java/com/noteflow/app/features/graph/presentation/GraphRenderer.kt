package com.noteflow.app.features.graph.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.sp
import kotlin.math.ln

fun DrawScope.drawGraph(
    renderNodes: List<RenderNode>,
    renderEdges: List<RenderEdge>,
    settings: GraphSettings,
    textMeasurer: TextMeasurer
) {
    drawEdges(renderEdges, settings)
    drawNodes(renderNodes, settings)
    if (settings.showLabels) drawLabels(renderNodes, textMeasurer)
}

private fun DrawScope.drawEdges(edges: List<RenderEdge>, settings: GraphSettings) {
    if (settings.edgeMode == EdgeMode.NONE) return
    edges.forEach { edge ->
        if (settings.edgeMode == EdgeMode.WIKI_ONLY &&
            edge.type != com.noteflow.app.features.graph.domain.EdgeType.WIKI_LINK
        ) return@forEach
        drawLine(
            color = Color(0xFFCABEFF).copy(alpha = edge.alpha * 0.15f),
            start = Offset(edge.fromX, edge.fromY),
            end   = Offset(edge.toX, edge.toY),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawNodes(nodes: List<RenderNode>, settings: GraphSettings) {
    nodes.forEach { node ->
        val color = node.color.copy(alpha = node.alpha)
        if (settings.glowEnabled && node.isFocused) {
            drawCircle(
                color  = Color(0xFF75D1FF).copy(alpha = 0.25f),
                radius = node.radius * 2.8f,
                center = Offset(node.x, node.y)
            )
            drawCircle(
                color  = Color(0xFF75D1FF).copy(alpha = 0.45f),
                radius = node.radius * 1.9f,
                center = Offset(node.x, node.y)
            )
            drawCircle(
                color  = Color(0xFF75D1FF).copy(alpha = 0.15f),
                radius = node.radius * 3.8f,
                center = Offset(node.x, node.y)
            )
        } else if (settings.glowEnabled && node.glow > 0f) {
            drawCircle(
                color  = node.color.copy(alpha = node.alpha * node.glow * 0.2f),
                radius = node.radius * 1.8f,
                center = Offset(node.x, node.y)
            )
        }
        drawCircle(
            color  = Color(0xFF1C1B1B).copy(alpha = node.alpha),
            radius = node.radius,
            center = Offset(node.x, node.y)
        )
        drawCircle(
            color  = color,
            radius = node.radius * 0.85f,
            center = Offset(node.x, node.y)
        )
        if (node.isFocused) {
            drawCircle(
                color  = Color(0xFF75D1FF),
                radius = node.radius + 3f,
                center = Offset(node.x, node.y),
                style  = Stroke(width = 2f)
            )
        }
    }
}

private fun DrawScope.drawLabels(nodes: List<RenderNode>, textMeasurer: TextMeasurer) {
    nodes.forEach { node ->
        if (node.alpha < 0.3f) return@forEach
        val label = if (node.label.length > 14) node.label.take(12) + ".." else node.label
        val measured = textMeasurer.measure(
            label,
            style = TextStyle(
                fontSize = 10.sp,
                color    = Color.White.copy(alpha = node.alpha * 0.7f)
            )
        )
        drawText(
            textLayoutResult = measured,
            topLeft = Offset(
                x = node.x - measured.size.width / 2f,
                y = node.y + node.radius + 8f
            )
        )
    }
}

fun buildRenderNodes(
    nodes: List<com.noteflow.app.features.graph.domain.GraphNode>,
    focusedId: String?,
    connectionCounts: Map<String, Int>
): List<RenderNode> {
    return nodes.map { node ->
        val connections = connectionCounts[node.id] ?: 0
        val radius = (20f + (ln((connections + 1).toDouble()) * 8f).toFloat()).coerceIn(20f, 60f)
        val alpha = when {
            focusedId == null    -> 1.0f
            node.id == focusedId -> 1.0f
            else                 -> 0.15f
        }
        val isFocused = node.id == focusedId
        val glow = if (isFocused) 1.0f else if (connections > 2) 0.4f else 0.0f
        RenderNode(
            id        = node.id,
            type      = node.type,
            x         = node.x,
            y         = node.y,
            radius    = radius,
            color     = if (isFocused) Color(0xFFCABEFF)
                        else Color(0xFF353534),
            alpha     = alpha,
            glow      = glow,
            isFocused = isFocused,
            label     = node.label
        )
    }
}

fun buildRenderEdges(
    edges: List<com.noteflow.app.features.graph.domain.Edge>,
    nodes: List<com.noteflow.app.features.graph.domain.GraphNode>,
    focusedId: String?,
    edgeMode: EdgeMode
): List<RenderEdge> {
    val nodeMap = nodes.associateBy { it.id }
    return edges.mapNotNull { edge ->
        val from = nodeMap[edge.from] ?: return@mapNotNull null
        val to   = nodeMap[edge.to]   ?: return@mapNotNull null
        if (edgeMode == EdgeMode.WIKI_ONLY &&
            edge.type != com.noteflow.app.features.graph.domain.EdgeType.WIKI_LINK
        ) return@mapNotNull null
        val alpha = when {
            focusedId == null                                      -> 1.0f
            edge.from == focusedId || edge.to == focusedId        -> 1.0f
            else                                                   -> 0.05f
        }
        RenderEdge(
            fromX    = from.x,
            fromY    = from.y,
            toX      = to.x,
            toY      = to.y,
            type     = edge.type,
            strength = edge.strength,
            alpha    = alpha,
            isDashed = edge.type == com.noteflow.app.features.graph.domain.EdgeType.SIMILAR
        )
    }
}
