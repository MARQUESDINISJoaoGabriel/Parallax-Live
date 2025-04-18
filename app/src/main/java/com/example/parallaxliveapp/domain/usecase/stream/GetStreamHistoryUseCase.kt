package com.example.parallaxliveapp.domain.usecase.stream

import com.example.parallaxliveapp.domain.model.Stream
import com.example.parallaxliveapp.domain.repository.StreamRepository
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting a user's stream history.
 */
class GetStreamHistoryUseCase(private val streamRepository: StreamRepository) {

    /**
     * Gets the history of streams for a specific user.
     *
     * @param userId ID of the user
     * @param limit Maximum number of streams to retrieve (default: 10)
     * @return Flow of Resource containing the list of streams or error
     */
    fun getUserStreamHistory(
        userId: String,
        limit: Int = 10
    ): Flow<Resource<List<Stream>>> {
        return streamRepository.getUserStreamHistory(userId, limit)
    }

    /**
     * Gets a filtered history of streams.
     * This could be extended with filtering parameters like date range, status, etc.
     *
     * @param userId ID of the user
     * @param activeOnly If true, returns only active streams
     * @param limit Maximum number of streams to retrieve
     * @return Flow of Resource containing the filtered list of streams or error
     */
    fun getFilteredStreamHistory(
        userId: String,
        activeOnly: Boolean = false,
        limit: Int = 10
    ): Flow<Resource<List<Stream>>> {
        // In a more complete implementation, this would apply additional filtering logic
        // For now, we'll just delegate to the repository method
        return streamRepository.getUserStreamHistory(userId, limit)
    }

    /**
     * Gets all popular streams from the platform.
     * In a real implementation, this would be a separate repository method.
     *
     * @param limit Maximum number of streams to retrieve
     * @return Flow of Resource containing the list of popular streams or error
     */
    fun getPopularStreams(limit: Int = 10): Flow<Resource<List<Stream>>> {
        // This is a placeholder - in a real app, we would have a separate repository method
        // For now, we'll just return an empty successful result
        return kotlinx.coroutines.flow.flow {
            emit(Resource.success(emptyList()))
        }
    }
}