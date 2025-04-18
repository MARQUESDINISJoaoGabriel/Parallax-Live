package com.example.parallaxliveapp.domain.model

/**
 * Represents the configuration settings for a stream in the domain layer.
 *
 * @property targetViewerCount Number of viewers to simulate
 * @property messageType Type of messages to show (POSITIVE, QUESTIONS, MIXED)
 * @property title Optional title for the stream
 * @property isPrivate Whether the stream is private
 */
data class StreamConfig(
    val targetViewerCount: Int,
    val messageType: MessageType,
    val title: String = "",
    val isPrivate: Boolean = false
) {
    /**
     * Enum defining the types of messages that can appear in a stream.
     */
    enum class MessageType {
        POSITIVE,
        QUESTIONS,
        MIXED
    }
}