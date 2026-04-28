package com.noteflow.app.features.graph.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
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
            edge.type != androidx.compose.ui.graphics.toArgb().let {
                com.noteflow.app.features.graph.domain.EdgeType.WIKI_LINK
            }) return@forEach
        val color = EdgeColors[edge.type] ?: Color.White
        val strokeWidth = edge.strength * 3f
        drawLine(
            color = color.copy(alpha = edge.alpha * 0.6f),
            start = Offset(edge.fromX, edge.fromY),
            end   = Offset(edge.toX, edge.toY),
            strokeWidth = strokeWidth.coerceIn(1f, 4f)
        )
    }
}

private fun DrawScope.drawNodes(nodes: List<RenderNode>, settings: GraphSettings) {
    nodes.forEach { node ->
        val color = node.color.copy(alpha = node.alpha)
        if (settings.glowEnabled && node.glow > 0f) {
            drawCircle(
                color = node.color.copy(alpha = node.alpha * node.glow * 0.3f),
                radius = node.radius * 1.8f,
                center = Offset(node.x, node.y)
            )
            drawCircle(
                color = node.color.copy(alpha = node.alpha * node.glow * 0.15f),
                radius = node.radius * 2.5f,
                center = Offset(node.x, node.y)
            )
        }
        drawCircle(
            color = Color(0xFF1C1B1B).copy(alpha = node.alpha),
            radius = node.radius,
            center = Offset(node.x, node.y)
        )
        drawCircle(
            color = color,
            radius = node.radius * 0.85f,
            center = Offset(node.x, node.y)
        )
        if (node.isFocused) {
            drawCircle(
                color = node.color,
                radius = node.radius + 4f,
                center = Offset(node.x, node.y),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
        }
    }
}

private fun DrawScope.drawLabels(nodes: List<RenderNode>, textMeasurer: TextMeasurer) {
    nodes.forEach { node ->
        if (node.alpha < 0.3f) return@forEach
        val label = if (node.label.length > 12) node.label.take(10) + ".." else node.label
        val measured = textMeasurer.measure(
            label,
            style = TextStyle(
                fontSize = 10.sp,
                color = Color.White.copy(alpha = node.alpha * 0.9f)
            )
        )
        drawText(
            textLayoutResult = measured,
            topLeft = Offset(
                x = node.x - measured.size.width / 2f,
                y = node.y + node.radius + 6f
            )
        )
    }
}

fun buildRenderNodes(
    nodes: List<com.noteflow.app.features.graph.domain.GraphNode>,
    focusedId: String?,
    connectionCounts: Map<String, Int>
): List<RenderNode> {
    val now = System.currentTimeMillis()
    val oneDay = 86_400_000L
    val oneWeek = 7 * oneDay
    return nodes.map { node ->
        val connections = connectionCounts[node.id] ?: 0
        val radius = 20f + (ln((connections + 1).toDouble()) * 8f).toFloat()
        val glow = when {
            now - node.lastOpenedAt < oneDay  -> 1.0f
            now - node.lastOpenedAt < oneWeek -> 0.5f
            else -> 0.0f
        }
        val alpha = when {
            focusedId == null -> 1.0f
            node.id == focusedId -> 1.0f
            else -> 0.15f
        }
        RenderNode(
            id        = node.id,
            type      = node.type,
            x         = node.x,
            y         = node.y,
            radius    = radius.coerceIn(20f, 60f),
            color     = NodeColors[node.type] ?: Color.White,
            alpha     = alpha,
            glow      = glow,
            isFocused = node.id == focusedId,
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
            edge.type != com.noteflow.app.features.graph.domain.EdgeType.WIKI_LINK)
            return@mapNotNull null
        val alpha = when {
            focusedId == null -> 1.0f
            edge.from == focusedId || edge.to == focusedId -> 1.0f
            else -> 0.05f
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
