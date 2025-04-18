package com.example.parallaxliveapp.domain.usecase.auth

import com.example.parallaxliveapp.domain.model.User
import com.example.parallaxliveapp.domain.repository.AuthRepository
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for handling user login.
 */
class LoginUseCase(private val authRepository: AuthRepository) {

    /**
     * Login with email and password.
     *
     * @param email User's email
     * @param password User's password
     * @return Flow of Resource containing the user or error
     */
    fun loginWithEmailPassword(email: String, password: String): Flow<Resource<User>> {
        return authRepository.loginWithEmailPassword(email, password)
    }

    /**
     * Login with Google authentication.
     *
     * @param idToken Google ID token
     * @return Flow of Resource containing the user or error
     */
    fun loginWithGoogle(idToken: String): Flow<Resource<User>> {
        return authRepository.loginWithGoogle(idToken)
    }

    /**
     * Gets the currently authenticated user.
     *
     * @return Flow of Resource containing the user if authenticated, or null if not
     */
    fun getCurrentUser(): Flow<Resource<User?>> {
        return authRepository.getCurrentUser()
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return Boolean indicating login status
     */
    fun isUserLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}