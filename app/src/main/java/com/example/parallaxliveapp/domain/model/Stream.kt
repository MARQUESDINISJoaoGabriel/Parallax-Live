package com.example.parallaxliveapp.domain.model

import java.util.Date

/**
 * Represents a complete stream entity in the domain layer.
 *
 * @property id Unique identifier for the stream
 * @property title Title of the stream
 * @property createdBy ID of the user who created the stream
 * @property creatorName Name of the user who created the stream
 * @property startTime When the stream started
 * @property endTime When the stream ended (null if still active)
 * @property config Configuration settings for this stream
 * @property viewerCount Actual number of viewers who watched
 * @property comments List of comments made during the stream
 */
data class Stream(
    val id: String,
    val title: String,
    val createdBy: String,
    val creatorName: String,
    val startTime: Date,
    val endTime: Date? = null,
    val config: StreamConfig,
    val viewerCount: Int = 0,
    val comments: List<Comment> = emptyList()
)