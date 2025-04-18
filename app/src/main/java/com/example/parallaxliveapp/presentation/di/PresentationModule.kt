package com.example.parallaxliveapp.presentation.di

import com.example.parallaxliveapp.presentation.ui.auth.login.LoginViewModel
import com.example.parallaxliveapp.presentation.ui.auth.password.PasswordRecoveryViewModel
import com.example.parallaxliveapp.presentation.ui.auth.register.RegisterViewModel
import com.example.parallaxliveapp.presentation.ui.history.StreamHistoryViewModel
import com.example.parallaxliveapp.presentation.ui.home.HomeViewModel
import com.example.parallaxliveapp.presentation.ui.profile.ProfileViewModel
import com.example.parallaxliveapp.presentation.ui.stream.config.StreamConfigViewModel
import com.example.parallaxliveapp.presentation.ui.stream.live.LiveStreamViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Provides presentation layer dependencies (ViewModels).
 */
val presentationModule = module {
    // Authentication ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { PasswordRecoveryViewModel(get()) }

    // Home ViewModel
    viewModel { HomeViewModel(get(), get()) }

    // Stream ViewModels
    viewModel { StreamConfigViewModel(get(), get()) }
    viewModel { LiveStreamViewModel(get(), get()) }

    // Profile ViewModel
    viewModel { ProfileViewModel(get()) }

    // History ViewModel
    viewModel { StreamHistoryViewModel(get()) }
}