package com.noteflow.app.features.tags.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tags",
    indices = [
        Index(value = ["normalizedName"], unique = true),
        Index(value = ["usageCount"]),
        Index(value = ["lastUsedAt"])
    ]
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val normalizedName: String,
    val color: String = "#8A70FF",
    val usageCount: Int = 0,
    val lastUsedAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
