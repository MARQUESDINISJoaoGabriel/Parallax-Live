package com.example.parallaxliveapp.domain.model

/**
 * Represents a user in the domain layer.
 *
 * @property id Unique identifier for the user
 * @property name Display name of the user
 * @property email Email address of the user
 * @property photoUrl URL to the user's profile photo (can be null)
 * @property isEmailVerified Whether the user's email is verified
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false
)