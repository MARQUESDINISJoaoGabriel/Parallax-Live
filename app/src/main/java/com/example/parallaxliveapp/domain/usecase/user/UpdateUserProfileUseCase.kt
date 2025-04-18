package com.example.parallaxliveapp.domain.usecase.user

import com.example.parallaxliveapp.domain.model.User
import com.example.parallaxliveapp.domain.repository.UserRepository
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for updating a user's profile information.
 */
class UpdateUserProfileUseCase(private val userRepository: UserRepository) {

    /**
     * Updates the user's profile information.
     *
     * @param user Updated user profile data
     * @return Flow of Resource containing the updated user or error
     */
    fun updateUserProfile(user: User): Flow<Resource<User>> {
        return userRepository.updateUserProfile(user)
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

    /**
     * Changes the user's password.
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

    /**
     * Updates the user's email address.
     *
     * @param newEmail New email address
     * @param password Current password for verification
     * @return Flow of Resource containing the updated user or error
     */
    fun updateEmail(
        newEmail: String,
        password: String
    ): Flow<Resource<User>> {
        return userRepository.updateEmail(newEmail, password)
    }

    /**
     * Validates a password for correctness.
     *
     * @param password Password to validate
     * @return Error message if invalid, null if valid
     */
    fun validatePassword(password: String): String? {
        if (password.isEmpty()) {
            return "Password cannot be empty"
        }

        if (password.length < 6) {
            return "Password must be at least 6 characters"
        }

        return null
    }

    /**
     * Validates that two passwords match.
     *
     * @param password First password
     * @param confirmPassword Second password to confirm
     * @return Error message if they don't match, null if they match
     */
    fun validatePasswordsMatch(password: String, confirmPassword: String): String? {
        if (password != confirmPassword) {
            return "Passwords do not match"
        }

        return null
    }

    /**
     * Validates an email format.
     *
     * @param email Email to validate
     * @return Error message if invalid, null if valid
     */
    fun validateEmail(email: String): String? {
        if (email.isEmpty()) {
            return "Email cannot be empty"
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format"
        }

        return null
    }
}