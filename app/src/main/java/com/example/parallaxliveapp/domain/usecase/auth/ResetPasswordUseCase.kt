package com.example.parallaxliveapp.domain.usecase.auth

import com.example.parallaxliveapp.domain.repository.AuthRepository
import com.example.parallaxliveapp.domain.repository.UserRepository
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for handling password reset.
 */
class ResetPasswordUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    /**
     * Sends a password reset email to the specified email address.
     *
     * @param email Email address to send password reset link to
     * @return Flow of Resource indicating success or error
     */
    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> {
        return authRepository.sendPasswordResetEmail(email)
    }

    /**
     * Changes the user's password directly when user is authenticated.
     *
     * @param currentPassword User's current password
     * @param newPassword User's new password
     * @return Flow of Resource indicating success or error
     */
    fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> {
        return userRepository.changePassword(currentPassword, newPassword)
    }
}