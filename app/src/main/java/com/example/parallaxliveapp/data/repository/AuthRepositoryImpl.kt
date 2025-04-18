package com.example.parallaxliveapp.data.repository

import com.example.parallaxliveapp.data.model.UserDto
import com.example.parallaxliveapp.data.model.mappers.toDomain
import com.example.parallaxliveapp.data.source.local.UserPreferences
import com.example.parallaxliveapp.data.source.remote.AuthRemoteDataSource
import com.example.parallaxliveapp.domain.model.User
import com.example.parallaxliveapp.domain.repository.AuthRepository
import com.example.parallaxliveapp.util.result.Resource
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of the AuthRepository interface.
 */
class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userPreferences: UserPreferences,
    private val database: FirebaseDatabase
) : AuthRepository {

    override fun getCurrentUser(): Flow<Resource<User?>> = flow {
        emit(Resource.loading())
        try {
            val firebaseUser = authRemoteDataSource.getCurrentUser()
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    isEmailVerified = firebaseUser.isEmailVerified
                )
                emit(Resource.success(user))
            } else {
                emit(Resource.success(null))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error", null))
        }
    }

    override fun registerWithEmailPassword(
        email: String,
        password: String,
        name: String
    ): Flow<Resource<User>> = flow {
        emit(Resource.loading())
        try {
            val firebaseUser = authRemoteDataSource.registerWithEmailPassword(email, password, name)
            val user = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                photoUrl = null,
                isEmailVerified = firebaseUser.isEmailVerified
            )

            // Save user data to database
            val userDto = UserDto(
                id = user.id,
                name = user.name,
                email = user.email,
                photoUrl = user.photoUrl,
                isEmailVerified = user.isEmailVerified
            )
            authRemoteDataSource.saveUserData(userDto, database)

            // Save last logged in user
            userPreferences.saveLastLoggedInUserId(user.id)

            emit(Resource.success(user))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Registration failed", null))
        }
    }

    override fun loginWithEmailPassword(
        email: String,
        password: String
    ): Flow<Resource<User>> = flow {
        emit(Resource.loading())
        try {
            val firebaseUser = authRemoteDataSource.loginWithEmailPassword(email, password)
            val user = User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                photoUrl = firebaseUser.photoUrl?.toString(),
                isEmailVerified = firebaseUser.isEmailVerified
            )

            // Save last logged in user
            userPreferences.saveLastLoggedInUserId(user.id)

            emit(Resource.success(user))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Login failed", null))
        }
    }

    override fun loginWithGoogle(idToken: String): Flow<Resource<User>> = flow {
        emit(Resource.loading())
        try {
            val firebaseUser = authRemoteDataSource.loginWithGoogle(idToken)
            val user = User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                photoUrl = firebaseUser.photoUrl?.toString(),
                isEmailVerified = firebaseUser.isEmailVerified
            )

            // Check if user exists in database, if not, create it
            val userDto = UserDto(
                id = user.id,
                name = user.name,
                email = user.email,
                photoUrl = user.photoUrl,
                isEmailVerified = user.isEmailVerified
            )
            authRemoteDataSource.saveUserData(userDto, database)

            // Save last logged in user
            userPreferences.saveLastLoggedInUserId(user.id)

            emit(Resource.success(user))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Google login failed", null))
        }
    }

    override fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        try {
            authRemoteDataSource.sendPasswordResetEmail(email)
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to send password reset email", null))
        }
    }

    override fun confirmEmailVerification(code: String): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        try {
            authRemoteDataSource.confirmEmailVerification(code)
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Failed to verify email", null))
        }
    }

    override fun logout(): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        try {
            authRemoteDataSource.logout()
            userPreferences.clearLastLoggedInUserId()
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Logout failed", null))
        }
    }

    override fun isLoggedIn(): Boolean {
        return authRemoteDataSource.isLoggedIn()
    }
}