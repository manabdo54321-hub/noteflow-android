package com.noteflow.app.features.graph.presentation

import androidx.compose.ui.graphics.Color
import com.noteflow.app.features.graph.domain.EdgeType
import com.noteflow.app.features.graph.domain.NodeType

val NodeColors = mapOf(
    NodeType.NOTE to Color(0xFFCABEFF),
    NodeType.TASK to Color(0xFF75D1FF),
    NodeType.GOAL to Color(0xFF8A70FF),
    NodeType.TAG  to Color(0xFFFFB347)
)

val EdgeColors = mapOf(
    EdgeType.WIKI_LINK  to Color(0xFFCABEFF),
    EdgeType.TAG_SHARED to Color(0xFFFFB347),
    EdgeType.GOAL_TASK  to Color(0xFF8A70FF),
    EdgeType.SIMILAR    to Color(0xFF75D1FF)
)

data class RenderNode(
    val id: String,
    val type: NodeType,
    val x: Float,
    val y: Float,
    val radius: Float,
    val color: Color,
    val alpha: Float,
    val glow: Float,
    val isFocused: Boolean,
    val label: String
)

data class RenderEdge(
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float,
    val type: EdgeType,
    val strength: Float,
    val alpha: Float,
    val isDashed: Boolean
)

enum class NodeSizeMode { FIXED, ADAPTIVE }
enum class EdgeMode { ALL, WIKI_ONLY, NONE }

data class GraphSettings(
    val showLabels: Boolean = true,
    val physicsEnabled: Boolean = true,
    val glowEnabled: Boolean = true,
    val nodeSizeMode: NodeSizeMode = NodeSizeMode.ADAPTIVE,
    val edgeMode: EdgeMode = EdgeMode.ALL
)
