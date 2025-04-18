package com.example.parallaxliveapp.data.repository

import com.example.parallaxliveapp.data.model.CommentDto
import com.example.parallaxliveapp.data.model.mappers.toDomain
import com.example.parallaxliveapp.data.model.mappers.toDto
import com.example.parallaxliveapp.data.source.remote.StreamRemoteDataSource
import com.example.parallaxliveapp.domain.model.Comment
import com.example.parallaxliveapp.domain.model.Stream
import com.example.parallaxliveapp.domain.model.StreamConfig
import com.example.parallaxliveapp.domain.repository.StreamRepository
import com.example.parallaxliveapp.util.result.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.Date

/**
 * Implementation of the StreamRepository interface.
 */
class StreamRepositoryImpl(
    private val streamRemoteDataSource: StreamRemoteDataSource
) : StreamRepository {

    override fun createStream(
        userId: String,
        config: StreamConfig
    ): Flow<Resource<Stream>> = flow {
        emit(Resource.loading())
        try {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser ?: throw Exception("No authenticated user")
            val creatorName = currentUser.displayName ?: "User"

            val streamDto = streamRemoteDataSource.createStream(
                userId = userId,
                creatorName = creatorName,
                config = config.toDto()
            )

            val stream = Stream(
                id = streamDto.id,
                title = streamDto.title,
                createdBy = streamDto.createdBy,
                creatorName = streamDto.creatorName,
                startTime = Date(streamDto.startTime),
                config = config,
                viewerCount = 0,
                comments = emptyList()
            )

            emit(Resource.success(stream))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to create stream", null))
        }
    }

    override fun getStreamDetails(streamId: String): Flow<Resource<Stream>> = flow {
        emit(Resource.loading())

        try {
            // Get stream data
            val streamFlow = streamRemoteDataSource.getStreamDetails(streamId)
                .catch { e ->
                    emit(Resource.error(e.message ?: "Error fetching stream", null))
                }

            // Get comments data
            val commentsFlow = streamRemoteDataSource.getStreamComments(streamId)
                .catch { e ->
                    // Just log the error, don't fail the whole operation
                    println("Error fetching comments: ${e.message}")
                }

            // Collect stream data
            streamFlow.collect { streamDto ->
                // Get latest comments
                var comments = emptyList<Comment>()
                try {
                    commentsFlow.collect { commentDtos ->
                        comments = commentDtos.map { it.toDomain() }
                    }
                } catch (e: Exception) {
                    // Just log the error, continue with empty comments
                    println("Error processing comments: ${e.message}")
                }

                val stream = streamDto.toDomain(comments)
                emit(Resource.success(stream))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Error fetching stream details", null))
        }
    }

    override fun getUserStreamHistory(
        userId: String,
        limit: Int
    ): Flow<Resource<List<Stream>>> = flow {
        emit(Resource.loading())
        try {
            val streamDtos = streamRemoteDataSource.getUserStreamHistory(userId, limit)
            val streams = streamDtos.map { it.toDomain() }
            emit(Resource.success(streams))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to get stream history", null))
        }
    }

    override fun endStream(streamId: String): Flow<Resource<Stream>> = flow {
        emit(Resource.loading())
        try {
            val streamDto = streamRemoteDataSource.endStream(streamId)
            val stream = streamDto.toDomain()
            emit(Resource.success(stream))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to end stream", null))
        }
    }

    override fun addComment(
        streamId: String,
        userId: String,
        text: String
    ): Flow<Resource<Comment>> = flow {
        emit(Resource.loading())
        try {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            val commentDto = CommentDto(
                id = UUID.randomUUID().toString(),
                streamId = streamId,
                userId = userId,
                username = currentUser?.displayName ?: "User",
                avatarUrl = currentUser?.photoUrl?.toString(),
                text = text,
                timestamp = System.currentTimeMillis(),
                isSystemGenerated = false
            )

            val savedComment = streamRemoteDataSource.addComment(streamId, commentDto)
            emit(Resource.success(savedComment.toDomain()))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to add comment", null))
        }
    }

    override fun getStreamComments(streamId: String): Flow<Resource<List<Comment>>> = flow {
        emit(Resource.loading())

        try {
            streamRemoteDataSource.getStreamComments(streamId)
                .catch { e ->
                    emit(Resource.error(e.message ?: "Error fetching comments", null))
                }
                .collect { commentDtos ->
                    val comments = commentDtos.map { it.toDomain() }
                    emit(Resource.success(comments))
                }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Error fetching comments", null))
        }
    }

    override fun addReactionToComment(
        commentId: String,
        reaction: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        try {
            streamRemoteDataSource.addReactionToComment(commentId, reaction)
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to add reaction", null))
        }
    }
}