package com.example.parallaxliveapp.data.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data Transfer Object for Stream to be stored in Firebase.
 */
@IgnoreExtraProperties
data class StreamDto(
    val id: String = "",
    val title: String = "",
    val createdBy: String = "",
    val creatorName: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val config: StreamConfigDto = StreamConfigDto(),
    val viewerCount: Int = 0,
    val isActive: Boolean = true
) {
    /**
     * Converts to a Map for Firebase Database.
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "createdBy" to createdBy,
            "creatorName" to creatorName,
            "startTime" to startTime,
            "endTime" to endTime,
            "config" to config.toMap(),
            "viewerCount" to viewerCount,
            "isActive" to isActive
        )
    }
}