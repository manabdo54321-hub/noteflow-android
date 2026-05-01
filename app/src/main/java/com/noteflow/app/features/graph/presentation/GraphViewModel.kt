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
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _graphJson = MutableStateFlow("")
    val graphJson: StateFlow<String> = _graphJson

    private val _graphState = MutableStateFlow(GraphState())
    val graphState: StateFlow<GraphState> = _graphState

    init { loadGraph() }

    private fun loadGraph() {
        viewModelScope.launch {
            combine(
                noteRepository.getAllNotes(),
                taskRepository.getAllTasks(),
                goalRepository.getAllGoals(),
                tagRepository.getAllTags()
            ) { notes, tasks, goals, tags ->
                val nodesArray = JSONArray()
                val edgesArray = JSONArray()

                val wikiRegex = Regex("""\[\[(.+?)]]""")
                val linkCountMap = mutableMapOf<String, Int>()

                notes.forEach { note ->
                    wikiRegex.findAll(note.content).forEach { match ->
                        val linkedTitle = match.groupValues[1]
                        val linked = notes.find { it.title == linkedTitle }
                        if (linked != null) {
                            val fromId = "note_" + note.id
                            val toId   = "note_" + linked.id
                            linkCountMap[fromId] = (linkCountMap[fromId] ?: 0) + 1
                            linkCountMap[toId]   = (linkCountMap[toId]   ?: 0) + 1
                            edgesArray.put(JSONObject().apply {
                                put("source", fromId)
                                put("target", toId)
                                put("strength", 1.0)
                                put("type", "WIKI_LINK")
                            })
                        }
                    }
                }

                notes.forEach { note ->
                    val id = "note_" + note.id
                    nodesArray.put(JSONObject().apply {
                        put("id", id)
                        put("label", note.title)
                        put("type", "NOTE")
                        put("group", 1)
                        put("linkCount", linkCountMap[id] ?: 0)
                    })
                }
                tasks.forEach { task ->
                    val id = "task_" + task.id
                    nodesArray.put(JSONObject().apply {
                        put("id", id)
                        put("label", task.title)
                        put("type", "TASK")
                        put("group", 2)
                        put("linkCount", 0)
                    })
                }
                goals.forEach { goal ->
                    val id = "goal_" + goal.id
                    nodesArray.put(JSONObject().apply {
                        put("id", id)
                        put("label", goal.title)
                        put("type", "GOAL")
                        put("group", 3)
                        put("linkCount", 0)
                    })
                }
                tags.forEach { tag ->
                    val id = "tag_" + tag.id
                    nodesArray.put(JSONObject().apply {
                        put("id", id)
                        put("label", tag.name)
                        put("type", "TAG")
                        put("group", 4)
                        put("linkCount", 0)
                    })
                }

                JSONObject().apply {
                    put("nodes", nodesArray)
                    put("edges", edgesArray)
                }.toString()
            }.collectLatest { json ->
                _graphJson.value = json
            }
        }
    }

    fun setMode(mode: GraphMode) {
        _graphState.value = _graphState.value.copy(currentMode = mode)
    }

    fun setFocusedNode(nodeId: String?) {
        _graphState.value = _graphState.value.copy(focusedNodeId = nodeId)
    }
}