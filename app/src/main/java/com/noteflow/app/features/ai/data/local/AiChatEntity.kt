package com.noteflow.app.features.ai.data.local

import androidx.room.*

@Entity(tableName = "ai_chats")
data class AiChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface AiChatDao {
    @Query("SELECT * FROM ai_chats ORDER BY createdAt ASC")
    suspend fun getAllChats(): List<AiChatEntity>

    @Insert
    suspend fun insertChat(chat: AiChatEntity)

    @Query("DELETE FROM ai_chats")
    suspend fun clearChats()
}
