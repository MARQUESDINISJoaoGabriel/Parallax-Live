package com.example.parallaxliveapp.data.source.remote

import com.example.parallaxliveapp.data.model.UserDto
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.lang.Exception

/**
 * Remote data source for authentication operations using Firebase.
 */
class AuthRemoteDataSource(
    private val auth: FirebaseAuth
) {
    /**
     * Firebase path for users
     */
    private val usersPath = "users"

    /**
     * Returns the current authenticated user
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Registers a user with email and password
     */
    suspend fun registerWithEmailPassword(
        email: String,
        password: String,
        name: String
    ): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Registration failed")

        // Update display name
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        user.updateProfile(profileUpdates).await()

        return user
    }

    /**
     * Signs in a user with email and password
     */
    suspend fun loginWithEmailPassword(
        email: String,
        password: String
    ): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Login failed")
    }

    /**
     * Signs in a user with Google authentication
     */
    suspend fun loginWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return signInWithCredential(credential)
    }

    /**
     * Signs in a user with a credential
     */
    private suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser {
        val result = auth.signInWithCredential(credential).await()
        return result.user ?: throw Exception("Sign-in with credential failed")
    }

    /**
     * Sends a password reset email
     */
    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    /**
     * Confirms email verification with code
     */
    suspend fun confirmEmailVerification(code: String) {
        auth.applyActionCode(code).await()
    }

    /**
     * Signs out the current user
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Checks if a user is currently logged in
     */
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Save user data to Realtime Database
     */
    suspend fun saveUserData(userDto: UserDto, database: FirebaseDatabase) {
        database.reference
            .child(usersPath)
            .child(userDto.id)
            .setValue(userDto.toMap())
            .await()
    }

    /**
     * Updates the user's email
     */
    suspend fun updateEmail(newEmail: String): FirebaseUser {
        val user = auth.currentUser ?: throw Exception("No authenticated user")
        user.updateEmail(newEmail).await()
        return user
    }

    /**
     * Reauthenticates the user with their password
     */
    suspend fun reauthenticateWithPassword(password: String) {
        val user = auth.currentUser ?: throw Exception("No authenticated user")
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential).await()
    }
}