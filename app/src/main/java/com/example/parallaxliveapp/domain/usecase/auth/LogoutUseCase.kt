package com.example.parallaxliveapp.domain.usecase.auth

import com.example.parallaxliveapp.domain.repository.AuthRepository
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for handling user logout.
 */
class LogoutUseCase(private val authRepository: AuthRepository) {

    /**
     * Logs out the currently authenticated user.
     *
     * @return Flow of Resource indicating success or error
     */
    fun logout(): Flow<Resource<Unit>> {
        return authRepository.logout()
    }
}