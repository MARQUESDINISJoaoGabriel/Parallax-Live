package com.example.parallaxliveapp.domain.usecase.stream

import com.example.parallaxliveapp.domain.model.Stream
import com.example.parallaxliveapp.domain.repository.StreamRepository
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting details of a specific stream.
 */
class GetStreamDetailsUseCase(private val streamRepository: StreamRepository) {

    /**
     * Gets detailed information about a specific stream.
     *
     * @param streamId ID of the stream to retrieve
     * @return Flow of Resource containing the stream details or error
     */
    fun getStreamDetails(streamId: String): Flow<Resource<Stream>> {
        return streamRepository.getStreamDetails(streamId)
    }

    /**
     * Gets comments for a stream in real-time.
     *
     * @param streamId ID of the stream
     * @return Flow of Resource containing the list of comments (updates in real-time)
     */
    fun getStreamComments(streamId: String): Flow<Resource<List<com.example.parallaxliveapp.domain.model.Comment>>> {
        return streamRepository.getStreamComments(streamId)
    }

    /**
     * Adds a comment to a stream.
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
    ): Flow<Resource<com.example.parallaxliveapp.domain.model.Comment>> {
        return streamRepository.addComment(streamId, userId, text)
    }

    /**
     * Adds a reaction to a comment.
     *
     * @param commentId ID of the comment
     * @param reaction Emoji reaction code
     * @return Flow of Resource indicating success or error
     */
    fun addReactionToComment(
        commentId: String,
        reaction: String
    ): Flow<Resource<Unit>> {
        return streamRepository.addReactionToComment(commentId, reaction)
    }

    /**
     * Ends an active stream.
     *
     * @param streamId ID of the stream to end
     * @return Flow of Resource containing the updated stream or error
     */
    fun endStream(streamId: String): Flow<Resource<Stream>> {
        return streamRepository.endStream(streamId)
    }
}