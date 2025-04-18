package com.example.parallaxliveapp.presentation.di

import com.example.parallaxliveapp.domain.usecase.auth.LoginUseCase
import com.example.parallaxliveapp.domain.usecase.auth.LogoutUseCase
import com.example.parallaxliveapp.domain.usecase.auth.RegisterUseCase
import com.example.parallaxliveapp.domain.usecase.auth.ResetPasswordUseCase
import com.example.parallaxliveapp.domain.usecase.stream.CreateStreamUseCase
import com.example.parallaxliveapp.domain.usecase.stream.GetStreamDetailsUseCase
import com.example.parallaxliveapp.domain.usecase.stream.GetStreamHistoryUseCase
import com.example.parallaxliveapp.domain.usecase.user.GetUserProfileUseCase
import com.example.parallaxliveapp.domain.usecase.user.UpdateUserProfileUseCase
import org.koin.dsl.module

/**
 * Provides domain layer dependencies (use cases).
 */
val domainModule = module {
    // Auth use cases
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { ResetPasswordUseCase(get()) }

    // Stream use cases
    factory { CreateStreamUseCase(get()) }
    factory { GetStreamDetailsUseCase(get()) }
    factory { GetStreamHistoryUseCase(get()) }

    // User use cases
    factory { GetUserProfileUseCase(get()) }
    factory { UpdateUserProfileUseCase(get()) }
}