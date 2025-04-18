package com.example.parallaxliveapp.presentation.di

import android.content.Context
import com.example.parallaxliveapp.data.repository.AuthRepositoryImpl
import com.example.parallaxliveapp.data.repository.StreamRepositoryImpl
import com.example.parallaxliveapp.data.repository.UserRepositoryImpl
import com.example.parallaxliveapp.data.source.local.UserPreferences
import com.example.parallaxliveapp.data.source.remote.AuthRemoteDataSource
import com.example.parallaxliveapp.data.source.remote.StreamRemoteDataSource
import com.example.parallaxliveapp.data.source.remote.UserRemoteDataSource
import com.example.parallaxliveapp.domain.repository.AuthRepository
import com.example.parallaxliveapp.domain.repository.StreamRepository
import com.example.parallaxliveapp.domain.repository.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Provides data layer dependencies.
 */
val dataModule = module {
    // Local data sources
    single { UserPreferences(androidContext()) }

    // Remote data sources
    single { AuthRemoteDataSource(get()) }
    single { UserRemoteDataSource(get()) }
    single { StreamRemoteDataSource(get()) }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<StreamRepository> { StreamRepositoryImpl(get()) }
}