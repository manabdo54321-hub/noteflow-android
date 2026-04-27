package com.noteflow.app.features.tags.domain.repository

import com.noteflow.app.features.tags.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {

    fun getAllTags(): Flow<List<Tag>>
    suspend fun getTagByName(name: String): Tag?
    suspend fun upsertTag(tag: Tag): Long
    suspend fun deleteTag(tagId: Long)

    suspend fun syncTagsForNote(noteId: Long, tagNames: List<String>)
    suspend fun syncTagsForTask(taskId: Long, tagNames: List<String>)
    suspend fun syncTagsForGoal(goalId: Long, tagNames: List<String>)

    fun getTagsForNote(noteId: Long): Flow<List<Tag>>
    fun getTagsForTask(taskId: Long): Flow<List<Tag>>
    fun getTagsForGoal(goalId: Long): Flow<List<Tag>>

    fun getNoteIdsByTag(tagId: Long): Flow<List<Long>>
    fun getTaskIdsByTag(tagId: Long): Flow<List<Long>>
    fun getGoalIdsByTag(tagId: Long): Flow<List<Long>>

    fun getSuggestedTags(prefix: String): Flow<List<Tag>>
    fun getMostUsedTags(limit: Int = 10): Flow<List<Tag>>

    suspend fun renameTag(tagId: Long, newName: String)
    suspend fun mergeTags(sourceTagId: Long, targetTagId: Long)
    suspend fun updateTagColor(tagId: Long, color: String)
}
