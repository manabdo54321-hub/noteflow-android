package com.noteflow.app.features.tags.data.repository

import com.noteflow.app.features.tags.data.local.TagDao
import com.noteflow.app.features.tags.data.local.TagEntity
import com.noteflow.app.features.tags.data.local.NoteTagCrossRef
import com.noteflow.app.features.tags.data.local.TaskTagCrossRef
import com.noteflow.app.features.tags.data.local.GoalTagCrossRef
import com.noteflow.app.features.tags.domain.TagExtractor
import com.noteflow.app.features.tags.domain.model.Tag
import com.noteflow.app.features.tags.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    // ── Mapping ───────────────────────────────────────

    private fun TagEntity.toDomain() = Tag(
        id = id,
        name = name,
        normalizedName = normalizedName,
        color = color,
        usageCount = usageCount,
        lastUsedAt = lastUsedAt,
        createdAt = createdAt
    )

    private fun Tag.toEntity() = TagEntity(
        id = id,
        name = name,
        normalizedName = normalizedName,
        color = color,
        usageCount = usageCount,
        lastUsedAt = lastUsedAt,
        createdAt = createdAt
    )

    // ── CRUD ──────────────────────────────────────────

    override fun getAllTags(): Flow<List<Tag>> =
        tagDao.getAllTags().map { list -> list.map { it.toDomain() } }

    override suspend fun getTagByName(name: String): Tag? {
        val normalized = TagExtractor.normalize(name)
        return tagDao.getTagByNormalizedName(normalized)?.toDomain()
    }

    override suspend fun upsertTag(tag: Tag): Long {
        val existing = tagDao.getTagByNormalizedName(tag.normalizedName)
        return if (existing != null) {
            existing.id
        } else {
            tagDao.insertTag(tag.toEntity())
        }
    }

    override suspend fun deleteTag(tagId: Long) {
        tagDao.deleteTag(tagId)
    }

    // ── Suggestions ───────────────────────────────────

    override fun getSuggestedTags(prefix: String): Flow<List<Tag>> =
        tagDao.getSuggestedTags(TagExtractor.normalize(prefix))
            .map { list -> list.map { it.toDomain() } }

    override fun getMostUsedTags(limit: Int): Flow<List<Tag>> =
        tagDao.getMostUsedTags(limit)
            .map { list -> list.map { it.toDomain() } }

    // ── Management ────────────────────────────────────

    override suspend fun renameTag(tagId: Long, newName: String) {
        tagDao.renameTag(tagId, newName, TagExtractor.normalize(newName))
    }

    override suspend fun updateTagColor(tagId: Long, color: String) {
        tagDao.updateTagColor(tagId, color)
    }

    // ── Sync Helper ───────────────────────────────────

    private suspend fun upsertTagByName(name: String): Long {
        val normalized = TagExtractor.normalize(name)
        val existing = tagDao.getTagByNormalizedName(normalized)
        return if (existing != null) {
            tagDao.incrementUsage(existing.id)
            existing.id
        } else {
            val colors = listOf(
                "#8A70FF", "#75D1FF", "#FF7070",
                "#70FFB0", "#FFB870", "#FF70D1"
            )
            val color = colors[(normalized.hashCode() and 0x7FFFFFFF) % colors.size]
            tagDao.insertTag(
                TagEntity(
                    name = name,
                    normalizedName = normalized,
                    color = color,
                    usageCount = 1,
                    lastUsedAt = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    // ── Note Sync ─────────────────────────────────────

    override suspend fun syncTagsForNote(noteId: Long, tagNames: List<String>) {
        val oldTagIds = tagDao.getTagIdsForNote(noteId).toSet()
        val newTagIds = mutableSetOf<Long>()
        tagNames.forEach { name ->
            val id = upsertTagByName(name)
            newTagIds.add(id)
            tagDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, id))
        }
        oldTagIds.subtract(newTagIds).forEach { removedId ->
            tagDao.deleteNoteTagCrossRef(noteId, removedId)
            tagDao.decrementUsage(removedId)
        }
    }

    override fun getTagsForNote(noteId: Long): Flow<List<Tag>> =
        tagDao.getTagsForNote(noteId).map { list -> list.map { it.toDomain() } }

    override fun getNoteIdsByTag(tagId: Long): Flow<List<Long>> =
        tagDao.getNoteIdsByTag(tagId)

    // ── Task Sync ─────────────────────────────────────

    override suspend fun syncTagsForTask(taskId: Long, tagNames: List<String>) {
        val oldTagIds = tagDao.getTagIdsForTask(taskId).toSet()
        val newTagIds = mutableSetOf<Long>()
        tagNames.forEach { name ->
            val id = upsertTagByName(name)
            newTagIds.add(id)
            tagDao.insertTaskTagCrossRef(TaskTagCrossRef(taskId, id))
        }
        oldTagIds.subtract(newTagIds).forEach { removedId ->
            tagDao.deleteTaskTagCrossRef(taskId, removedId)
            tagDao.decrementUsage(removedId)
        }
    }

    override fun getTagsForTask(taskId: Long): Flow<List<Tag>> =
        tagDao.getTagsForTask(taskId).map { list -> list.map { it.toDomain() } }

    override fun getTaskIdsByTag(tagId: Long): Flow<List<Long>> =
        tagDao.getTaskIdsByTag(tagId)

    // ── Goal Sync ─────────────────────────────────────

    override suspend fun syncTagsForGoal(goalId: Long, tagNames: List<String>) {
        val oldTagIds = tagDao.getTagIdsForGoal(goalId).toSet()
        val newTagIds = mutableSetOf<Long>()
        tagNames.forEach { name ->
            val id = upsertTagByName(name)
            newTagIds.add(id)
            tagDao.insertGoalTagCrossRef(GoalTagCrossRef(goalId, id))
        }
        oldTagIds.subtract(newTagIds).forEach { removedId ->
            tagDao.deleteGoalTagCrossRef(goalId, removedId)
            tagDao.decrementUsage(removedId)
        }
    }

    override fun getTagsForGoal(goalId: Long): Flow<List<Tag>> =
        tagDao.getTagsForGoal(goalId).map { list -> list.map { it.toDomain() } }

    override fun getGoalIdsByTag(tagId: Long): Flow<List<Long>> =
        tagDao.getGoalIdsByTag(tagId)

    // ── Merge ─────────────────────────────────────────

    override suspend fun mergeTags(sourceTagId: Long, targetTagId: Long) {
        tagDao.mergeNoteRefs(sourceTagId, targetTagId)
        tagDao.mergeTaskRefs(sourceTagId, targetTagId)
        tagDao.mergeGoalRefs(sourceTagId, targetTagId)
        val source = tagDao.getTagById(sourceTagId)
        val target = tagDao.getTagById(targetTagId)
        if (source != null && target != null) {
            tagDao.updateTag(
                target.copy(usageCount = target.usageCount + source.usageCount)
            )
        }
        tagDao.deleteTag(sourceTagId)
    }
}
