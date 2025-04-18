package com.example.parallaxliveapp.presentation.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parallaxliveapp.domain.model.Stream
import com.example.parallaxliveapp.domain.usecase.auth.LoginUseCase
import com.example.parallaxliveapp.domain.usecase.stream.GetStreamHistoryUseCase
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeViewModel(
    private val loginUseCase: LoginUseCase,
    private val getStreamHistoryUseCase: GetStreamHistoryUseCase
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set
    fun loadStreams() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            loginUseCase.getCurrentUser().collectLatest { userResource ->
                when (userResource.status) {
                    Resource.Status.SUCCESS -> {
                        val user = userResource.data
                        if (user != null) {
                            loadUserStreams(user.id)

                            loadPopularStreams()
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

    private fun loadUserStreams(userId: String) {
        viewModelScope.launch {
            getStreamHistoryUseCase.getUserStreamHistory(userId)
                .collectLatest { streamsResource ->
                    when (streamsResource.status) {
                        Resource.Status.SUCCESS -> {
                            val streams = streamsResource.data ?: emptyList()
                            uiState = uiState.copy(
                                isLoading = false,
                                userStreams = streams
                            )
                        }
                        Resource.Status.ERROR -> {
                            uiState = uiState.copy(
                                isLoading = false,
                                errorMessage = streamsResource.message
                            )
                        }
                        Resource.Status.LOADING -> {
                        }
                    }
                }
        }
    }

    /**
     * Loads popular streams from all users.
     * In a real app, this would call a different repository method.
     * For now, we're using an empty list.
     */
    private fun loadPopularStreams() {
        uiState = uiState.copy(
            popularStreams = emptyList()
        )
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val userStreams: List<Stream> = emptyList(),
    val popularStreams: List<Stream> = emptyList(),
    val errorMessage: String? = null
)