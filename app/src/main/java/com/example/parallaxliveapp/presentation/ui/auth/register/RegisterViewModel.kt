package com.example.parallaxliveapp.presentation.ui.auth.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parallaxliveapp.domain.usecase.auth.RegisterUseCase
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set
    fun updateName(name: String) {
        uiState = uiState.copy(
            name = name,
            nameError = validateName(name)
        )
    }

    fun updateEmail(email: String) {
        uiState = uiState.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun updatePassword(password: String) {
        uiState = uiState.copy(
            password = password,
            passwordError = validatePassword(password),
            confirmPasswordError = validatePasswordsMatch(password, uiState.confirmPassword)
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        uiState = uiState.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validatePasswordsMatch(uiState.password, confirmPassword)
        )
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun register() {
        val nameError = validateName(uiState.name)
        val emailError = validateEmail(uiState.email)
        val passwordError = validatePassword(uiState.password)
        val confirmPasswordError = validatePasswordsMatch(uiState.password, uiState.confirmPassword)

        if (nameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            uiState = uiState.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
            return
        }

        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            registerUseCase.registerWithEmailPassword(
                uiState.email,
                uiState.password,
                uiState.name
            ).collectLatest { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            isRegistered = true
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

    private fun validateName(name: String): String? {
        return if (name.isBlank()) {
            "Name cannot be empty"
        } else {
            null
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

    private fun validatePasswordsMatch(password: String, confirmPassword: String): String? {
        return if (confirmPassword.isBlank()) {
            "Please confirm your password"
        } else if (password != confirmPassword) {
            "Passwords do not match"
        } else {
            null
        }
    }
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val errorMessage: String? = null
)