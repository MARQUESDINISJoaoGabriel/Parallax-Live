package com.example.parallaxliveapp.presentation.ui.auth.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parallaxliveapp.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for the Welcome screen.
 */
class WelcomeViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    /**
     * Checks if a user is already logged in.
     *
     * @return True if user is logged in, false otherwise
     */
    fun isUserLoggedIn(): Boolean {
        return loginUseCase.isUserLoggedIn()
    }

    /**
     * Signs in with Google.
     *
     * @param idToken Google ID token
     * @param onResult Callback with result (success or failure)
     */
    fun signInWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            loginUseCase.loginWithGoogle(idToken).collectLatest { resource ->
                when (resource.status) {
                    com.example.parallaxliveapp.util.result.Resource.Status.SUCCESS -> {
                        onResult(true)
                    }
                    com.example.parallaxliveapp.util.result.Resource.Status.ERROR -> {
                        onResult(false)
                    }
                    else -> {} // Loading state, do nothing
                }
            }
        }
    }

    /**
     * Initiates sign-in with Google.
     * This is a placeholder that would be implemented with real Google Sign-In.
     *
     * @param onResult Callback with result (success or failure)
     */
    fun signInWithGoogle(onResult: (Boolean) -> Unit) {
        // In a real app, this would trigger Google Sign-In flow
        // For now, just a placeholder
        onResult(false)
    }
}