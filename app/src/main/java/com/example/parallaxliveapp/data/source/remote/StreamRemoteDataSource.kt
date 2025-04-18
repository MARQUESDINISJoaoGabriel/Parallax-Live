package com.example.parallaxliveapp.data.source.remote

import com.example.parallaxliveapp.data.model.CommentDto
import com.example.parallaxliveapp.data.model.StreamConfigDto
import com.example.parallaxliveapp.data.model.StreamDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Remote data source for stream operations using Firebase.
 */
class StreamRemoteDataSource(
    private val database: FirebaseDatabase
) {
    /**
     * Firebase paths
     */
    private val streamsPath = "streams"
    private val userStreamsPath = "user_streams"
    private val streamCommentsPath = "stream_comments"
    private val commentsPath = "comments"

    /**
     * Creates a new stream
     */
    suspend fun createStream(userId: String, creatorName: String, config: StreamConfigDto): StreamDto {
        val streamId = UUID.randomUUID().toString()
        val streamDto = StreamDto(
            id = streamId,
            title = config.title.ifEmpty { "Stream by $creatorName" },
            createdBy = userId,
            creatorName = creatorName,
            config = config,
            startTime = System.currentTimeMillis(),
            isActive = true
        )

        // Save stream data
        database.reference
            .child(streamsPath)
            .child(streamId)
            .setValue(streamDto.toMap())
            .await()

        // Add reference to user's streams
        database.reference
            .child(userStreamsPath)
            .child(userId)
            .child(streamId)
            .setValue(true)
            .await()

        return streamDto
    }

    /**
     * Gets stream details
     */
    fun getStreamDetails(streamId: String): Flow<StreamDto> = callbackFlow {
        val streamRef = database.reference.child(streamsPath).child(streamId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val streamMap = snapshot.value as? Map<String, Any> ?: return

                    val configMap = streamMap["config"] as? Map<String, Any> ?: mapOf()
                    val configDto = StreamConfigDto(
                        targetViewerCount = (configMap["targetViewerCount"] as? Number)?.toInt() ?: 0,
                        messageType = configMap["messageType"] as? String ?: "POSITIVE",
                        title = configMap["title"] as? String ?: "",
                        isPrivate = configMap["isPrivate"] as? Boolean ?: false
                    )

                    val streamDto = StreamDto(
                        id = streamId,
                        title = streamMap["title"] as? String ?: "",
                        createdBy = streamMap["createdBy"] as? String ?: "",
                        creatorName = streamMap["creatorName"] as? String ?: "",
                        startTime = streamMap["startTime"] as? Long ?: 0L,
                        endTime = streamMap["endTime"] as? Long,
                        config = configDto,
                        viewerCount = (streamMap["viewerCount"] as? Number)?.toInt() ?: 0,
                        isActive = streamMap["isActive"] as? Boolean ?: true
                    )

                    trySend(streamDto)
                } catch (e: Exception) {
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        streamRef.addValueEventListener(listener)

        awaitClose {
            streamRef.removeEventListener(listener)
        }
    }

    /**
     * Gets user's stream history
     */
    suspend fun getUserStreamHistory(userId: String, limit: Int): List<StreamDto> {
        val userStreamsRef = database.reference
            .child(userStreamsPath)
            .child(userId)
            .limitToLast(limit)

        val snapshot = userStreamsRef.get().await()
        if (!snapshot.exists()) {
            return emptyList()
        }

        val streamIds = snapshot.children.mapNotNull { it.key }
        return streamIds.mapNotNull { streamId ->
            try {
                val streamSnapshot = database.reference
                    .child(streamsPath)
                    .child(streamId)
                    .get()
                    .await()

                val streamMap = streamSnapshot.value as? Map<String, Any> ?: return@mapNotNull null

                val configMap = streamMap["config"] as? Map<String, Any> ?: mapOf()
                val configDto = StreamConfigDto(
                    targetViewerCount = (configMap["targetViewerCount"] as? Number)?.toInt() ?: 0,
                    messageType = configMap["messageType"] as? String ?: "POSITIVE",
                    title = configMap["title"] as? String ?: "",
                    isPrivate = configMap["isPrivate"] as? Boolean ?: false
                )

                StreamDto(
                    id = streamId,
                    title = streamMap["title"] as? String ?: "",
                    createdBy = streamMap["createdBy"] as? String ?: "",
                    creatorName = streamMap["creatorName"] as? String ?: "",
                    startTime = streamMap["startTime"] as? Long ?: 0L,
                    endTime = streamMap["endTime"] as? Long,
                    config = configDto,
                    viewerCount = (streamMap["viewerCount"] as? Number)?.toInt() ?: 0,
                    isActive = streamMap["isActive"] as? Boolean ?: false
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Ends a stream
     */
    suspend fun endStream(streamId: String): StreamDto {
        val streamRef = database.reference.child(streamsPath).child(streamId)

        // Get current stream data
        val snapshot = streamRef.get().await()
        val streamMap = snapshot.value as? Map<String, Any>
            ?: throw Exception("Stream not found")

        val configMap = streamMap["config"] as? Map<String, Any> ?: mapOf()
        val configDto = StreamConfigDto(
            targetViewerCount = (configMap["targetViewerCount"] as? Number)?.toInt() ?: 0,
            messageType = configMap["messageType"] as? String ?: "POSITIVE",
            title = configMap["title"] as? String ?: "",
            isPrivate = configMap["isPrivate"] as? Boolean ?: false
        )

        // Update the stream to set it as inactive and add end time
        val endTime = System.currentTimeMillis()
        streamRef.child("endTime").setValue(endTime).await()
        streamRef.child("isActive").setValue(false).await()

        return StreamDto(
            id = streamId,
            title = streamMap["title"] as? String ?: "",
            createdBy = streamMap["createdBy"] as? String ?: "",
            creatorName = streamMap["creatorName"] as? String ?: "",
            startTime = streamMap["startTime"] as? Long ?: 0L,
            endTime = endTime,
            config = configDto,
            viewerCount = (streamMap["viewerCount"] as? Number)?.toInt() ?: 0,
            isActive = false
        )
    }

    /**
     * Adds a comment to a stream
     */
    suspend fun addComment(streamId: String, commentDto: CommentDto): CommentDto {
        val commentId = commentDto.id.ifEmpty { UUID.randomUUID().toString() }
        val comment = commentDto.copy(id = commentId)

        // Save comment
        database.reference
            .child(commentsPath)
            .child(commentId)
            .setValue(comment.toMap())
            .await()

        // Add reference to stream's comments
        database.reference
            .child(streamCommentsPath)
            .child(streamId)
            .child(commentId)
            .setValue(System.currentTimeMillis())
            .await()

        return comment
    }

    /**
     * Gets comments for a stream
     */
    fun getStreamComments(streamId: String): Flow<List<CommentDto>> = callbackFlow {
        val commentsRef = database.reference
            .child(streamCommentsPath)
            .child(streamId)
            .orderByValue()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (!snapshot.exists()) {
                        trySend(emptyList())
                        return
                    }

                    val commentIds = snapshot.children.mapNotNull { it.key }

                    if (commentIds.isEmpty()) {
                        trySend(emptyList())
                        return
                    }

                    // Fetch actual comments
                    database.reference
                        .child(commentsPath)
                        .get()
                        .addOnSuccessListener { commentsSnapshot ->
                            val comments = commentIds.mapNotNull { commentId ->
                                val commentSnapshot = commentsSnapshot.child(commentId)
                                if (!commentSnapshot.exists()) return@mapNotNull null

                                val commentMap = commentSnapshot.value as? Map<String, Any>
                                    ?: return@mapNotNull null

                                val reactionsMap = (commentMap["reactions"] as? Map<String, Boolean>) ?: emptyMap()

                                CommentDto(
                                    id = commentId,
                                    streamId = commentMap["streamId"] as? String ?: streamId,
                                    userId = commentMap["userId"] as? String,
                                    username = commentMap["username"] as? String ?: "",
                                    avatarUrl = commentMap["avatarUrl"] as? String,
                                    text = commentMap["text"] as? String ?: "",
                                    timestamp = commentMap["timestamp"] as? Long ?: 0L,
                                    reactions = reactionsMap,
                                    isSystemGenerated = commentMap["isSystemGenerated"] as? Boolean ?: false
                                )
                            }

                            trySend(comments)
                        }
                        .addOnFailureListener { exception ->
                            close(exception)
                        }

                } catch (e: Exception) {
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        commentsRef.addValueEventListener(listener)

        awaitClose {
            commentsRef.removeEventListener(listener)
        }
    }

    /**
     * Adds a reaction to a comment
     */
    suspend fun addReactionToComment(commentId: String, reaction: String) {
        database.reference
            .child(commentsPath)
            .child(commentId)
            .child("reactions")
            .child(reaction)
            .setValue(true)
            .await()
    }
}