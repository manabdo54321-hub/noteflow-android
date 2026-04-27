package com.noteflow.app.features.tags.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "goal_tag_cross_ref",
    primaryKeys = ["goalId", "tagId"],
    indices = [Index(value = ["tagId"])]
)
data class GoalTagCrossRef(
    val goalId: Long,
    val tagId: Long
)
