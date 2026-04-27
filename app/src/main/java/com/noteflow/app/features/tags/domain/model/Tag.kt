package com.noteflow.app.features.tags.domain.model

data class Tag(
    val id: Long = 0,
    val name: String,
    val normalizedName: String,
    val color: String = "#8A70FF",
    val usageCount: Int = 0,
    val lastUsedAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
