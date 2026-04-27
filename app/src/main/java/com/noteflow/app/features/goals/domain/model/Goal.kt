package com.noteflow.app.features.goals.domain.model

data class Goal(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val progress: Int = 0,
    val targetDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
