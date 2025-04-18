package com.example.parallaxliveapp.domain.usecase.user

import com.example.parallaxliveapp.domain.model.User
import com.example.parallaxliveapp.domain.repository.UserRepository
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving a user's profile.
 */
class GetUserProfileUseCase(private val userRepository: UserRepository) {

    /**
     * Gets the profile for a user by ID.
     *
     * @param userId ID of the user to retrieve
     * @return Flow of Resource containing the user profile or error
     */
    fun getUserProfile(userId: String): Flow<Resource<User>> {
        return userRepository.getUserProfile(userId)
    }

    /**
     * Gets detailed profile information for the currently authenticated user.
     *
     * Note: This method uses the userId parameter provided rather than trying to
     * determine the current user internally, to maintain separation of concerns.
     *
     * @param userId ID of the current user
     * @return Flow of Resource containing the user profile or error
     */
    fun getCurrentUserProfile(userId: String): Flow<Resource<User>> {
        return userRepository.getUserProfile(userId)
    }

    /**
     * Updates the user's profile photo.
     *
     * @param userId ID of the user
     * @param photoUri URI of the new profile photo
     * @return Flow of Resource containing the URL of the uploaded photo or error
     */
    fun updateProfilePhoto(
        userId: String,
        photoUri: String
    ): Flow<Resource<String>> {
        return userRepository.updateProfilePhoto(userId, photoUri)
    }
}