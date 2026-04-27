package com.noteflow.app.features.goals.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noteflow.app.features.goals.domain.model.Goal

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val progress: Int = 0,
    val targetDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = Goal(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        progress = progress,
        targetDate = targetDate,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(goal: Goal) = GoalEntity(
            id = goal.id,
            title = goal.title,
            description = goal.description,
            isCompleted = goal.isCompleted,
            progress = goal.progress,
            targetDate = goal.targetDate,
            createdAt = goal.createdAt
        )
    }
}
