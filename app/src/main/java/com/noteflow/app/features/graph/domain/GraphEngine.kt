package com.noteflow.app.features.graph.domain

data class Force(val x: Float, val y: Float) {
    operator fun plus(other: Force) = Force(x + other.x, y + other.y)
}

object GraphEngine {

    private const val DAMPING = 0.85f
    private const val MAX_SPEED = 10f
    private const val REPULSION = 3000f
    private const val ATTRACTION = 0.01f
    private const val MIN_DIST = 30f

    fun step(
        nodes: List<GraphNode>,
        edges: List<Edge>
    ): List<GraphNode> {
        if (nodes.isEmpty()) return nodes

        val forces = Array(nodes.size) { Force(0f, 0f) }

        // Repulsion بين كل الـ Nodes
        for (i in nodes.indices) {
            for (j in nodes.indices) {
                if (i == j) continue
                val dx = nodes[i].x - nodes[j].x
                val dy = nodes[i].y - nodes[j].y
                val dist = maxOf(Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat(), MIN_DIST)
                val force = REPULSION / (dist * dist)
                forces[i] = forces[i] + Force(
                    x = (dx / dist) * force,
                    y = (dy / dist) * force
                )
            }
        }

        // Attraction على حسب Edge.strength
        val nodeIndex = nodes.mapIndexed { i, n -> n.id to i }.toMap()
        for (edge in edges) {
            val fromIdx = nodeIndex[edge.from] ?: continue
            val toIdx = nodeIndex[edge.to] ?: continue
            val from = nodes[fromIdx]
            val to = nodes[toIdx]
            val dx = to.x - from.x
            val dy = to.y - from.y
            val dist = maxOf(Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat(), MIN_DIST)
            val force = ATTRACTION * dist * edge.strength
            val fx = (dx / dist) * force
            val fy = (dy / dist) * force
            forces[fromIdx] = forces[fromIdx] + Force(fx, fy)
            forces[toIdx] = forces[toIdx] + Force(-fx, -fy)
        }

        // Verlet Integration
        return nodes.mapIndexed { i, node ->
            var vx = (node.x - node.prevX) * DAMPING + forces[i].x
            var vy = (node.y - node.prevY) * DAMPING + forces[i].y
            val speed = Math.sqrt((vx * vx + vy * vy).toDouble()).toFloat()
            if (speed > MAX_SPEED) {
                vx = (vx / speed) * MAX_SPEED
                vy = (vy / speed) * MAX_SPEED
            }
            node.copy(
                prevX = node.x,
                prevY = node.y,
                x = node.x + vx,
                y = node.y + vy
            )
        }
    }

    fun initPositions(nodes: List<GraphNode>, width: Float, height: Float): List<GraphNode> {
        val cx = width / 2f
        val cy = height / 2f
        val radius = minOf(width, height) / 3f
        return nodes.mapIndexed { i, node ->
            val angle = (2 * Math.PI * i / nodes.size).toFloat()
            node.copy(
                x = cx + radius * Math.cos(angle.toDouble()).toFloat(),
                y = cy + radius * Math.sin(angle.toDouble()).toFloat(),
                prevX = cx + radius * Math.cos(angle.toDouble()).toFloat(),
                prevY = cy + radius * Math.sin(angle.toDouble()).toFloat()
            )
        }
    }
}
