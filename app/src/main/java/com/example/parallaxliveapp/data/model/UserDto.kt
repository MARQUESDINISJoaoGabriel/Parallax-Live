package com.example.parallaxliveapp.data.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data Transfer Object for User to be stored in Firebase.
 */
@IgnoreExtraProperties
data class UserDto(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSignInAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts to a Map for Firebase Database.
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "photoUrl" to photoUrl,
            "isEmailVerified" to isEmailVerified,
            "createdAt" to createdAt,
            "lastSignInAt" to lastSignInAt
        )
    }
}