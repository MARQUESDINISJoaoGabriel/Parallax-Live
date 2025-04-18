package com.example.parallaxliveapp.data.repository

import com.example.parallaxliveapp.data.model.UserDto
import com.example.parallaxliveapp.data.model.mappers.toDomain
import com.example.parallaxliveapp.data.model.mappers.toDto
import com.example.parallaxliveapp.data.source.local.UserPreferences
import com.example.parallaxliveapp.data.source.remote.AuthRemoteDataSource
import com.example.parallaxliveapp.data.source.remote.UserRemoteDataSource
import com.example.parallaxliveapp.domain.model.User
import com.example.parallaxliveapp.domain.repository.UserRepository
import com.example.parallaxliveapp.util.result.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userPreferences: UserPreferences
) : UserRepository {

    override fun getUserProfile(userId: String): Flow<Resource<User>> = flow {
        emit(Resource.loading())
        try {
            userRemoteDataSource.getUserProfile(userId)
                .catch { e ->
                    emit(Resource.error(e.message ?: "Error fetching user profile", null))
                }
                .collect { userDto ->
                    emit(Resource.success(userDto.toDomain()))
                }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error fetching profile", null))
        }
    }

    override fun updateUserProfile(user: User): Flow<Resource<User>> = flow {
        emit(Resource.loading())
        try {
            val userDto = user.toDto()
            userRemoteDataSource.updateUserProfile(userDto)
            emit(Resource.success(user))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to update profile", null))
        }
    }

    override fun updateProfilePhoto(userId: String, photoUri: String): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        try {
            userRemoteDataSource.updateProfilePhotoUrl(userId, photoUri)
            emit(Resource.success(photoUri))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to update profile photo", null))
        }
    }

    override fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        try {
            userRemoteDataSource.changePassword(FirebaseAuth.getInstance(), currentPassword, newPassword)
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to change password", null))
        }
    }

    override fun updateEmail(
        newEmail: String,
        password: String
    ): Flow<Resource<User>> = flow {
        emit(Resource.loading())
        try {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser ?: throw Exception("No authenticated user")

            // Re-authenticate first
            val authRemoteDataSource = AuthRemoteDataSource(auth)
            authRemoteDataSource.reauthenticateWithPassword(password)

            // Update email
            userRemoteDataSource.updateEmail(auth, currentUser.uid, newEmail)

            // Return updated user
            val updatedUser = User(
                id = currentUser.uid,
                name = currentUser.displayName ?: "",
                email = newEmail,
                photoUrl = currentUser.photoUrl?.toString(),
                isEmailVerified = false // Email verification status resets
            )

            emit(Resource.success(updatedUser))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to update email", null))
        }
    }
}