package com.noteflow.app.features.ai.data

sealed class AiAction {
    data class AddTask(val title: String, val priority: String = "MEDIUM") : AiAction()
    data class CompleteTask(val title: String) : AiAction()
    data class DeleteTask(val title: String) : AiAction()
    data class AddNote(val title: String, val content: String) : AiAction()
    data class DeleteNote(val title: String) : AiAction()
    data class StartTimer(val minutes: Int, val taskTitle: String? = null) : AiAction()
    data class StopTimer(val dummy: String = "") : AiAction()
    data class ShowStats(val dummy: String = "") : AiAction()
    data class CreatePlan(val title: String, val items: List<String>) : AiAction()
    data class None(val message: String = "") : AiAction()
}
