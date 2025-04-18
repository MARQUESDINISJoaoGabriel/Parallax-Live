package com.example.parallaxliveapp.data.source.remote

import com.example.parallaxliveapp.data.model.UserDto
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Remote data source for user operations using Firebase.
 */
class UserRemoteDataSource(
    private val database: FirebaseDatabase
) {
    /**
     * Firebase path for users
     */
    private val usersPath = "users"

    /**
     * Gets user profile by user ID
     */
    fun getUserProfile(userId: String): Flow<UserDto> = callbackFlow {
        val userRef = database.reference.child(usersPath).child(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userMap = snapshot.value as? Map<String, Any> ?: return

                val userDto = UserDto(
                    id = userId,
                    name = userMap["name"] as? String ?: "",
                    email = userMap["email"] as? String ?: "",
                    photoUrl = userMap["photoUrl"] as? String,
                    isEmailVerified = userMap["isEmailVerified"] as? Boolean ?: false,
                    createdAt = userMap["createdAt"] as? Long ?: 0L,
                    lastSignInAt = userMap["lastSignInAt"] as? Long ?: 0L
                )

                trySend(userDto)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userRef.addValueEventListener(listener)

        awaitClose {
            userRef.removeEventListener(listener)
        }
    }

    /**
     * Updates user profile data
     */
    suspend fun updateUserProfile(userDto: UserDto) {
        database.reference
            .child(usersPath)
            .child(userDto.id)
            .updateChildren(userDto.toMap())
            .await()
    }

    /**
     * Updates the user's profile photo URL
     */
    suspend fun updateProfilePhotoUrl(userId: String, photoUrl: String) {
        database.reference
            .child(usersPath)
            .child(userId)
            .child("photoUrl")
            .setValue(photoUrl)
            .await()
    }

    /**
     * Changes the user's password
     */
    suspend fun changePassword(
        auth: FirebaseAuth,
        currentPassword: String,
        newPassword: String
    ) {
        val user = auth.currentUser ?: throw Exception("No authenticated user")
        val email = user.email ?: throw Exception("User has no email")

        // Re-authenticate
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).await()

        // Change password
        user.updatePassword(newPassword).await()
    }

    /**
     * Updates the user's email address
     */
    suspend fun updateEmail(
        auth: FirebaseAuth,
        userId: String,
        newEmail: String
    ) {
        val user = auth.currentUser ?: throw Exception("No authenticated user")

        // Update email in Firebase Auth
        user.updateEmail(newEmail).await()

        // Update email in Realtime Database
        database.reference
            .child(usersPath)
            .child(userId)
            .child("email")
            .setValue(newEmail)
            .await()
    }
}