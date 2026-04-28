package com.noteflow.app.features.graph.domain

enum class NodeType { NOTE, TASK, GOAL, TAG }

enum class NodeIntent { IDEA, ACTION, PLAN, KNOWLEDGE }

enum class EdgeType {
    WIKI_LINK,
    TAG_SHARED,
    SIMILAR,
    GOAL_TASK
}

enum class GraphMode { NORMAL, FOCUS, TIMELINE, DISCOVERY }

data class GraphNode(
    val id: String,
    val label: String,
    val type: NodeType,
    val intent: NodeIntent,
    var x: Float = 0f,
    var y: Float = 0f,
    var prevX: Float = 0f,
    var prevY: Float = 0f,
    val linkCount: Int = 0,
    val lastOpenedAt: Long = 0L
)

data class Edge(
    val from: String,
    val to: String,
    val strength: Float,
    val type: EdgeType
)

data class GraphState(
    val zoom: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val focusedNodeId: String? = null,
    val currentMode: GraphMode = GraphMode.NORMAL
)
