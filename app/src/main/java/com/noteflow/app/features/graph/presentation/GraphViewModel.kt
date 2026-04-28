package com.noteflow.app.features.graph.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.goals.domain.repository.GoalRepository
import com.noteflow.app.features.graph.domain.*
import com.noteflow.app.features.notes.data.repository.NoteRepository
import com.noteflow.app.features.tags.domain.repository.TagRepository
import com.noteflow.app.features.tasks.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _nodes = MutableStateFlow<List<GraphNode>>(emptyList())
    val nodes: StateFlow<List<GraphNode>> = _nodes

    private val _edges = MutableStateFlow<List<Edge>>(emptyList())
    val edges: StateFlow<List<Edge>> = _edges

    private val _graphState = MutableStateFlow(GraphState())
    val graphState: StateFlow<GraphState> = _graphState

    init {
        loadGraph()
    }

    private fun loadGraph() {
        viewModelScope.launch {
            combine(
                noteRepository.getAllNotes(),
                taskRepository.getAllTasks(),
                goalRepository.getAllGoals(),
                tagRepository.getAllTags()
            ) { notes, tasks, goals, tags ->
                val nodes = mutableListOf<GraphNode>()
                val edges = mutableListOf<Edge>()

                notes.forEach { note ->
                    nodes.add(GraphNode(
                        id = "note_${note.id}",
                        label = note.title,
                        type = NodeType.NOTE,
                        intent = NodeIntent.IDEA,
                        lastOpenedAt = note.updatedAt
                    ))
                }

                tasks.forEach { task ->
                    nodes.add(GraphNode(
                        id = "task_${task.id}",
                        label = task.title,
                        type = NodeType.TASK,
                        intent = NodeIntent.ACTION
                    ))
                }

                goals.forEach { goal ->
                    nodes.add(GraphNode(
                        id = "goal_${goal.id}",
                        label = goal.title,
                        type = NodeType.GOAL,
                        intent = NodeIntent.PLAN
                    ))
                }

                tags.forEach { tag ->
                    nodes.add(GraphNode(
                        id = "tag_${tag.id}",
                        label = tag.name,
                        type = NodeType.TAG,
                        intent = NodeIntent.KNOWLEDGE
                    ))
                }

                notes.forEach { note ->
                    val wikiRegex = Regex("""\[\[(.+?)]]""")
                    wikiRegex.findAll(note.content).forEach { match ->
                        val linkedTitle = match.groupValues[1]
                        val linked = notes.find { it.title == linkedTitle }
                        if (linked != null) {
                            edges.add(Edge(
                                from = "note_${note.id}",
                                to = "note_${linked.id}",
                                strength = 1.0f,
                                type = EdgeType.WIKI_LINK
                            ))
                        }
                    }
                }

                Pair(nodes, edges)
            }.collectLatest { (nodes, edges) ->
                _nodes.value = nodes
                _edges.value = edges
            }
        }
    }

    fun updateGraphState(state: GraphState) {
        _graphState.value = state
    }

    fun setMode(mode: GraphMode) {
        _graphState.value = _graphState.value.copy(currentMode = mode)
    }

    fun tickPhysics(width: Float, height: Float) {
        val current = _nodes.value
        if (current.isEmpty()) return
        val initialized = if (current.first().x == 0f) {
            GraphEngine.initPositions(current, width, height)
        } else current
        _nodes.value = GraphEngine.step(initialized, _edges.value)
    }

    fun setFocusedNode(nodeId: String?) {
        _graphState.value = _graphState.value.copy(focusedNodeId = nodeId)
    }
}
