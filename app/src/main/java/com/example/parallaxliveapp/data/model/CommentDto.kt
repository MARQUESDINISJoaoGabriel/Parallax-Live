package com.example.parallaxliveapp.data.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data Transfer Object for Comment to be stored in Firebase.
 */
@IgnoreExtraProperties
data class CommentDto(
    val id: String = "",
    val streamId: String = "",
    val userId: String? = null,
    val username: String = "",
    val avatarUrl: String? = null,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val reactions: Map<String, Boolean> = emptyMap(),
    val isSystemGenerated: Boolean = false
) {
    /**
     * Converts to a Map for Firebase Database.
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "streamId" to streamId,
            "userId" to userId,
            "username" to username,
            "avatarUrl" to avatarUrl,
            "text" to text,
            "timestamp" to timestamp,
            "reactions" to reactions,
            "isSystemGenerated" to isSystemGenerated
        )
    }
}