package com.example.parallaxliveapp.domain.usecase.auth

import com.example.parallaxliveapp.domain.model.User
import com.example.parallaxliveapp.domain.repository.AuthRepository
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for handling user registration.
 */
class RegisterUseCase(private val authRepository: AuthRepository) {

    /**
     * Registers a new user with email and password.
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
    ): Flow<Resource<User>> {
        return authRepository.registerWithEmailPassword(email, password, name)
    }

    /**
     * Confirms email verification after user clicks link in email.
     *
     * @param code Verification code from the email link
     * @return Flow of Resource indicating success or error
     */
    fun confirmEmailVerification(code: String): Flow<Resource<Unit>> {
        return authRepository.confirmEmailVerification(code)
    }
}