package com.example.parallaxliveapp.domain.usecase.stream

import com.example.parallaxliveapp.domain.model.Stream
import com.example.parallaxliveapp.domain.model.StreamConfig
import com.example.parallaxliveapp.domain.repository.StreamRepository
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for creating a new stream.
 */
class CreateStreamUseCase(private val streamRepository: StreamRepository) {

    /**
     * Creates a new stream with the given configuration.
     *
     * @param userId ID of the user creating the stream
     * @param config Configuration for the stream
     * @return Flow of Resource containing the created stream or error
     */
    fun createStream(userId: String, config: StreamConfig): Flow<Resource<Stream>> {
        return streamRepository.createStream(userId, config)
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