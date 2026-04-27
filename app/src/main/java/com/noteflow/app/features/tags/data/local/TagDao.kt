package com.noteflow.app.features.tags.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    // ── CRUD ──────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Update
    suspend fun updateTag(tag: TagEntity)

    @Query("SELECT * FROM tags ORDER BY usageCount DESC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE normalizedName = :normalizedName LIMIT 1")
    suspend fun getTagByNormalizedName(normalizedName: String): TagEntity?

    @Query("SELECT * FROM tags WHERE id = :id LIMIT 1")
    suspend fun getTagById(id: Long): TagEntity?

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTag(tagId: Long)

    // ── Suggestions ───────────────────────────────────

    @Query("""
        SELECT * FROM tags 
        WHERE normalizedName LIKE :prefix || '%'
        ORDER BY usageCount DESC, lastUsedAt DESC
        LIMIT 10
    """)
    fun getSuggestedTags(prefix: String): Flow<List<TagEntity>>

    @Query("""
        SELECT * FROM tags 
        ORDER BY usageCount DESC 
        LIMIT :limit
    """)
    fun getMostUsedTags(limit: Int): Flow<List<TagEntity>>

    // ── Usage Count ───────────────────────────────────

    @Query("""
        UPDATE tags 
        SET usageCount = usageCount + 1, lastUsedAt = :time 
        WHERE id = :tagId
    """)
    suspend fun incrementUsage(tagId: Long, time: Long = System.currentTimeMillis())

    @Query("""
        UPDATE tags 
        SET usageCount = MAX(0, usageCount - 1) 
        WHERE id = :tagId
    """)
    suspend fun decrementUsage(tagId: Long)

    // ── Note CrossRef ─────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNoteTagCrossRef(crossRef: NoteTagCrossRef)

    @Query("DELETE FROM note_tag_cross_ref WHERE noteId = :noteId AND tagId = :tagId")
    suspend fun deleteNoteTagCrossRef(noteId: Long, tagId: Long)

    @Query("SELECT tagId FROM note_tag_cross_ref WHERE noteId = :noteId")
    suspend fun getTagIdsForNote(noteId: Long): List<Long>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN note_tag_cross_ref ref ON t.id = ref.tagId
        WHERE ref.noteId = :noteId
        ORDER BY t.usageCount DESC
    """)
    fun getTagsForNote(noteId: Long): Flow<List<TagEntity>>

    @Query("SELECT noteId FROM note_tag_cross_ref WHERE tagId = :tagId")
    fun getNoteIdsByTag(tagId: Long): Flow<List<Long>>

    // ── Task CrossRef ─────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTagCrossRef(crossRef: TaskTagCrossRef)

    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId AND tagId = :tagId")
    suspend fun deleteTaskTagCrossRef(taskId: Long, tagId: Long)

    @Query("SELECT tagId FROM task_tag_cross_ref WHERE taskId = :taskId")
    suspend fun getTagIdsForTask(taskId: Long): List<Long>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN task_tag_cross_ref ref ON t.id = ref.tagId
        WHERE ref.taskId = :taskId
        ORDER BY t.usageCount DESC
    """)
    fun getTagsForTask(taskId: Long): Flow<List<TagEntity>>

    @Query("SELECT taskId FROM task_tag_cross_ref WHERE tagId = :tagId")
    fun getTaskIdsByTag(tagId: Long): Flow<List<Long>>

    // ── Goal CrossRef ─────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoalTagCrossRef(crossRef: GoalTagCrossRef)

    @Query("DELETE FROM goal_tag_cross_ref WHERE goalId = :goalId AND tagId = :tagId")
    suspend fun deleteGoalTagCrossRef(goalId: Long, tagId: Long)

    @Query("SELECT tagId FROM goal_tag_cross_ref WHERE goalId = :goalId")
    suspend fun getTagIdsForGoal(goalId: Long): List<Long>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN goal_tag_cross_ref ref ON t.id = ref.tagId
        WHERE ref.goalId = :goalId
        ORDER BY t.usageCount DESC
    """)
    fun getTagsForGoal(goalId: Long): Flow<List<TagEntity>>

    @Query("SELECT goalId FROM goal_tag_cross_ref WHERE tagId = :tagId")
    fun getGoalIdsByTag(tagId: Long): Flow<List<Long>>

    // ── Merge ─────────────────────────────────────────

    @Query("UPDATE note_tag_cross_ref SET tagId = :targetId WHERE tagId = :sourceId")
    suspend fun mergeNoteRefs(sourceId: Long, targetId: Long)

    @Query("UPDATE task_tag_cross_ref SET tagId = :targetId WHERE tagId = :sourceId")
    suspend fun mergeTaskRefs(sourceId: Long, targetId: Long)

    @Query("UPDATE goal_tag_cross_ref SET tagId = :targetId WHERE tagId = :sourceId")
    suspend fun mergeGoalRefs(sourceId: Long, targetId: Long)

    @Query("UPDATE tags SET name = :name, normalizedName = :normalized WHERE id = :tagId")
    suspend fun renameTag(tagId: Long, name: String, normalized: String)

    @Query("UPDATE tags SET color = :color WHERE id = :tagId")
    suspend fun updateTagColor(tagId: Long, color: String)
}
