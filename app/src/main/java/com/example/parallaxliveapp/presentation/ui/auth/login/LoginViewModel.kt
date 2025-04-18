package com.example.parallaxliveapp.presentation.ui.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parallaxliveapp.domain.usecase.auth.LoginUseCase
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set
    fun updateEmail(email: String) {
        uiState = uiState.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun updatePassword(password: String) {
        uiState = uiState.copy(
            password = password,
            passwordError = validatePassword(password)
        )
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun login() {
        val emailError = validateEmail(uiState.email)
        val passwordError = validatePassword(uiState.password)

        if (emailError != null || passwordError != null) {
            uiState = uiState.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }

        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            loginUseCase.loginWithEmailPassword(uiState.email, uiState.password)
                .collectLatest { result ->
                    when (result.status) {
                        Resource.Status.SUCCESS -> {
                            uiState = uiState.copy(
                                isLoading = false,
                                isLoggedIn = true
                            )
                        }
                        Resource.Status.ERROR -> {
                            uiState = uiState.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        Resource.Status.LOADING -> {
                            uiState = uiState.copy(isLoading = true)
                        }
                    }
                }
        }
    }

    private fun validateEmail(email: String): String? {
        return if (email.isBlank()) {
            "Email cannot be empty"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Invalid email format"
        } else {
            null
        }
    }

    private fun validatePassword(password: String): String? {
        return if (password.isBlank()) {
            "Password cannot be empty"
        } else if (password.length < 6) {
            "Password must be at least 6 characters"
        } else {
            null
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null
)