package com.example.parallaxliveapp.data.repository

import com.example.parallaxliveapp.domain.model.User
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /**
     * Gets the profile for a user by ID
     *
     * @param userId ID of the user to retrieve
     * @return Flow of Resource containing the user profile or error
     */
    fun getUserProfile(userId: String): Flow<Resource<User>>

    /**
     * Updates the user's profile information
     *
     * @param user Updated user profile data
     * @return Flow of Resource containing the updated user or error
     */
    fun updateUserProfile(user: User): Flow<Resource<User>>

    /**
     * Updates the user's profile photo
     *
     * @param userId ID of the user
     * @param photoUri URI of the new profile photo
     * @return Flow of Resource containing the URL of the uploaded photo or error
     */
    fun updateProfilePhoto(
        userId: String,
        photoUri: String
    ): Flow<Resource<String>>

    /**
     * Changes the user's password
     *
     * @param currentPassword User's current password
     * @param newPassword User's new password
     * @return Flow of Resource indicating success or error
     */
    fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>>

    /**
     * Updates the user's email address
     *
     * @param newEmail New email address
     * @param password Current password for verification
     * @return Flow of Resource containing the updated user or error
     */
    fun updateEmail(
        newEmail: String,
        password: String
    ): Flow<Resource<User>>
}