package com.example.parallaxliveapp.presentation.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parallaxliveapp.data.source.local.UserPreferences
import com.example.parallaxliveapp.domain.model.User
import com.example.parallaxliveapp.domain.usecase.auth.LoginUseCase
import com.example.parallaxliveapp.domain.usecase.auth.LogoutUseCase
import com.example.parallaxliveapp.domain.usecase.user.GetUserProfileUseCase
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreferences: UserPreferences,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {

        loadAppPreferences()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            loginUseCase.getCurrentUser().collectLatest { userResource ->
                when (userResource.status) {
                    Resource.Status.SUCCESS -> {
                        val user = userResource.data
                        if (user != null) {
                            getUserProfileUseCase.getUserProfile(user.id)
                                .collectLatest { profileResource ->
                                    when (profileResource.status) {
                                        Resource.Status.SUCCESS -> {
                                            uiState = uiState.copy(
                                                isLoading = false,
                                                user = profileResource.data
                                            )
                                        }
                                        Resource.Status.ERROR -> {
                                            uiState = uiState.copy(
                                                isLoading = false,
                                                user = user,
                                                errorMessage = profileResource.message
                                            )
                                        }
                                        Resource.Status.LOADING -> {
                                        }
                                    }
                                }
                        } else {
                            uiState = uiState.copy(
                                isLoading = false,
                                errorMessage = "User not found"
                            )
                        }
                    }
                    Resource.Status.ERROR -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = userResource.message
                        )
                    }
                    Resource.Status.LOADING -> {

                    }
                }
            }
        }
    }


    private fun loadAppPreferences() {
        viewModelScope.launch {

            userPreferences.isDarkModeEnabled.collectLatest { darkModeEnabled ->
                uiState = uiState.copy(darkModeEnabled = darkModeEnabled)
            }
        }

        viewModelScope.launch {

            userPreferences.areNotificationsEnabled.collectLatest { notificationsEnabled ->
                uiState = uiState.copy(notificationsEnabled = notificationsEnabled)
            }
        }
    }


    fun updateNotificationSettings(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
            uiState = uiState.copy(notificationsEnabled = enabled)
        }
    }


    fun updateDarkModeSettings(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkModeEnabled(enabled)
            uiState = uiState.copy(darkModeEnabled = enabled)
        }
    }


    fun logout() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            logoutUseCase.logout().collectLatest { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            isLoggedOut = true
                        )
                    }
                    Resource.Status.ERROR -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = resource.message
                        )
                    }
                    Resource.Status.LOADING -> {

                    }
                }
            }
        }
    }


    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}


data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val darkModeEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null
)