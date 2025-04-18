package com.example.parallaxliveapp.presentation.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val appModule = module {
    // Firebase Auth singleton
    single<FirebaseAuth> { Firebase.auth }

    // Firebase Realtime Database singleton
    single<FirebaseDatabase> {
        Firebase.database.apply {
            // Enable offline capabilities
            setPersistenceEnabled(true)
        }
    }

    // Credential Manager will be provided at the activity level
}