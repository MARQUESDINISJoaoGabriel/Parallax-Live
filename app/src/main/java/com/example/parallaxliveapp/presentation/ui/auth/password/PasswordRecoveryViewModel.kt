package com.example.parallaxliveapp.presentation.ui.auth.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parallaxliveapp.domain.usecase.auth.ResetPasswordUseCase
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PasswordRecoveryViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    var uiState by mutableStateOf(PasswordRecoveryUiState())
        private set
    fun updateEmail(email: String) {
        uiState = uiState.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun sendPasswordResetEmail() {
        val emailError = validateEmail(uiState.email)
        if (emailError != null) {
            uiState = uiState.copy(emailError = emailError)
            return
        }

        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            resetPasswordUseCase.sendPasswordResetEmail(uiState.email)
                .collectLatest { result ->
                    when (result.status) {
                        Resource.Status.SUCCESS -> {
                            uiState = uiState.copy(
                                isLoading = false,
                                isResetSent = true
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
}

data class PasswordRecoveryUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isResetSent: Boolean = false,
    val emailError: String? = null,
    val errorMessage: String? = null
)