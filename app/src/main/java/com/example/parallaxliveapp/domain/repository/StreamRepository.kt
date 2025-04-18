package com.example.parallaxliveapp.domain.repository

import com.example.parallaxliveapp.domain.model.Comment
import com.example.parallaxliveapp.domain.model.Stream
import com.example.parallaxliveapp.domain.model.StreamConfig
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for stream-related operations.
 */
interface StreamRepository {
    /**
     * Creates a new stream with the given configuration
     *
     * @param userId ID of the user creating the stream
     * @param config Configuration for the stream
     * @return Flow of Resource containing the created stream or error
     */
    fun createStream(
        userId: String,
        config: StreamConfig
    ): Flow<Resource<Stream>>

    /**
     * Gets details for a specific stream
     *
     * @param streamId ID of the stream to retrieve
     * @return Flow of Resource containing the stream details or error
     */
    fun getStreamDetails(streamId: String): Flow<Resource<Stream>>

    /**
     * Gets the history of streams for a user
     *
     * @param userId ID of the user
     * @param limit Maximum number of streams to retrieve
     * @return Flow of Resource containing the list of streams or error
     */
    fun getUserStreamHistory(
        userId: String,
        limit: Int = 10
    ): Flow<Resource<List<Stream>>>

    /**
     * Ends an active stream
     *
     * @param streamId ID of the stream to end
     * @return Flow of Resource containing the updated stream or error
     */
    fun endStream(streamId: String): Flow<Resource<Stream>>

    /**
     * Adds a comment to a stream
     *
     * @param streamId ID of the stream
     * @param userId ID of the user making the comment
     * @param text Comment text
     * @return Flow of Resource containing the created comment or error
     */
    fun addComment(
        streamId: String,
        userId: String,
        text: String
    ): Flow<Resource<Comment>>

    /**
     * Gets comments for a stream in real-time
     *
     * @param streamId ID of the stream
     * @return Flow of Resource containing the list of comments (updates in real-time)
     */
    fun getStreamComments(streamId: String): Flow<Resource<List<Comment>>>

    /**
     * Reacts to a comment with an emoji
     *
     * @param commentId ID of the comment
     * @param reaction Emoji reaction code
     * @return Flow of Resource indicating success or error
     */
    fun addReactionToComment(
        commentId: String,
        reaction: String
    ): Flow<Resource<Unit>>
}