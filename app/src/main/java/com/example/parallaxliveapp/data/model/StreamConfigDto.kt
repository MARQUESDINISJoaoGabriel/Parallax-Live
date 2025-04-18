package com.example.parallaxliveapp.data.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data Transfer Object for Stream Configuration to be stored in Firebase.
 */
@IgnoreExtraProperties
data class StreamConfigDto(
    val targetViewerCount: Int = 0,
    val messageType: String = "POSITIVE",
    val title: String = "",
    val isPrivate: Boolean = false
) {
    /**
     * Converts to a Map for Firebase Database.
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "targetViewerCount" to targetViewerCount,
            "messageType" to messageType,
            "title" to title,
            "isPrivate" to isPrivate
        )
    }
}