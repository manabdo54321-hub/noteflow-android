package com.noteflow.app.features.tags.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "task_tag_cross_ref",
    primaryKeys = ["taskId", "tagId"],
    indices = [Index(value = ["tagId"])]
)
data class TaskTagCrossRef(
    val taskId: Long,
    val tagId: Long
)
