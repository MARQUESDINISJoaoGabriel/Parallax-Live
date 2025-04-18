package com.example.parallaxliveapp.data.model.mappers

import com.example.parallaxliveapp.data.model.CommentDto
import com.example.parallaxliveapp.data.model.StreamConfigDto
import com.example.parallaxliveapp.data.model.StreamDto
import com.example.parallaxliveapp.data.model.UserDto
import com.example.parallaxliveapp.domain.model.Comment
import com.example.parallaxliveapp.domain.model.Stream
import com.example.parallaxliveapp.domain.model.StreamConfig
import com.example.parallaxliveapp.domain.model.User
import java.util.Date

/**
 * Extension functions for mapping between domain models and data models.
 */

/**
 * Converts UserDto to User domain model.
 */
fun UserDto.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        photoUrl = photoUrl,
        isEmailVerified = isEmailVerified
    )
}

/**
 * Converts User domain model to UserDto.
 */
fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        name = name,
        email = email,
        photoUrl = photoUrl,
        isEmailVerified = isEmailVerified
    )
}

/**
 * Converts StreamConfigDto to StreamConfig domain model.
 */
fun StreamConfigDto.toDomain(): StreamConfig {
    return StreamConfig(
        targetViewerCount = targetViewerCount,
        messageType = when (messageType) {
            "QUESTIONS" -> StreamConfig.MessageType.QUESTIONS
            "MIXED" -> StreamConfig.MessageType.MIXED
            else -> StreamConfig.MessageType.POSITIVE
        },
        title = title,
        isPrivate = isPrivate
    )
}

/**
 * Converts StreamConfig domain model to StreamConfigDto.
 */
fun StreamConfig.toDto(): StreamConfigDto {
    return StreamConfigDto(
        targetViewerCount = targetViewerCount,
        messageType = messageType.name,
        title = title,
        isPrivate = isPrivate
    )
}

/**
 * Converts StreamDto to Stream domain model.
 */
fun StreamDto.toDomain(comments: List<Comment> = emptyList()): Stream {
    return Stream(
        id = id,
        title = title,
        createdBy = createdBy,
        creatorName = creatorName,
        startTime = Date(startTime),
        endTime = endTime?.let { Date(it) },
        config = config.toDomain(),
        viewerCount = viewerCount,
        comments = comments
    )
}

/**
 * Converts Stream domain model to StreamDto.
 */
fun Stream.toDto(): StreamDto {
    return StreamDto(
        id = id,
        title = title,
        createdBy = createdBy,
        creatorName = creatorName,
        startTime = startTime.time,
        endTime = endTime?.time,
        config = config.toDto(),
        viewerCount = viewerCount,
        isActive = endTime == null
    )
}

/**
 * Converts CommentDto to Comment domain model.
 */
fun CommentDto.toDomain(): Comment {
    return Comment(
        id = id,
        streamId = streamId,
        userId = userId,
        username = username,
        avatarUrl = avatarUrl,
        text = text,
        timestamp = Date(timestamp),
        reactions = reactions.keys.toList(),
        isSystemGenerated = isSystemGenerated
    )
}

/**
 * Converts Comment domain model to CommentDto.
 */
fun Comment.toDto(): CommentDto {
    val reactionMap = reactions.associateWith { true }

    return CommentDto(
        id = id,
        streamId = streamId,
        userId = userId,
        username = username,
        avatarUrl = avatarUrl,
        text = text,
        timestamp = timestamp.time,
        reactions = reactionMap,
        isSystemGenerated = isSystemGenerated
    )
}