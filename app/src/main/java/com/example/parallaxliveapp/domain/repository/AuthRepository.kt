package com.example.parallaxliveapp.domain.repository

import com.example.parallaxliveapp.domain.model.User
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication-related operations.
 */
interface AuthRepository {
    /**
     * Gets the currently authenticated user
     * @return Flow of Resource containing the user if authenticated, or null if not
     */
    fun getCurrentUser(): Flow<Resource<User?>>

    /**
     * Registers a new user with email and password
     *
     * @param email User's email
     * @param password User's password
     * @param name User's display name
     * @return Flow of Resource containing the new user or error
     */
    fun registerWithEmailPassword(
        email: String,
        password: String,
        name: String
    ): Flow<Resource<User>>

    /**
     * Signs in a user with email and password
     *
     * @param email User's email
     * @param password User's password
     * @return Flow of Resource containing the authenticated user or error
     */
    fun loginWithEmailPassword(
        email: String,
        password: String
    ): Flow<Resource<User>>

    /**
     * Signs in a user with Google authentication
     *
     * @param idToken Google ID token
     * @return Flow of Resource containing the authenticated user or error
     */
    fun loginWithGoogle(idToken: String): Flow<Resource<User>>

    /**
     * Sends a password reset email to the specified email address
     *
     * @param email Email address to send password reset link to
     * @return Flow of Resource indicating success or error
     */
    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>>

    /**
     * Confirms email verification after user clicks link in email
     *
     * @param code Verification code from the email link
     * @return Flow of Resource indicating success or error
     */
    fun confirmEmailVerification(code: String): Flow<Resource<Unit>>

    /**
     * Signs out the current user
     *
     * @return Flow of Resource indicating success or error
     */
    fun logout(): Flow<Resource<Unit>>

    /**
     * Checks if the user is logged in
     *
     * @return True if a user is logged in, false otherwise
     */
    fun isLoggedIn(): Boolean
}