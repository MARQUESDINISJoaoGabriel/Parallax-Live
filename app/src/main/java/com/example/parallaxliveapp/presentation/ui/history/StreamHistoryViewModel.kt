package com.example.parallaxliveapp.presentation.ui.history

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


class StreamHistoryViewModel(
    private val loginUseCase: LoginUseCase,
    private val getStreamHistoryUseCase: GetStreamHistoryUseCase
) : ViewModel() {


    var uiState by mutableStateOf(StreamHistoryUiState())
        private set

    fun loadStreamHistory() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            loginUseCase.getCurrentUser().collectLatest { userResource ->
                when (userResource.status) {
                    Resource.Status.SUCCESS -> {
                        val user = userResource.data
                        if (user != null) {
                            getStreamHistoryUseCase.getUserStreamHistory(user.id)
                                .collectLatest { streamsResource ->
                                    when (streamsResource.status) {
                                        Resource.Status.SUCCESS -> {
                                            val streams = streamsResource.data ?: emptyList()
                                            uiState = uiState.copy(
                                                isLoading = false,
                                                streams = streams
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

    /**
     * Applies filters to the stream history.
     *
     * @param activeOnly If true, shows only active streams
     */
    fun applyFilters(activeOnly: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            loginUseCase.getCurrentUser().collectLatest { userResource ->
                when (userResource.status) {
                    Resource.Status.SUCCESS -> {
                        val user = userResource.data
                        if (user != null) {
                            getStreamHistoryUseCase.getFilteredStreamHistory(
                                userId = user.id,
                                activeOnly = activeOnly
                            ).collectLatest { streamsResource ->
                                when (streamsResource.status) {
                                    Resource.Status.SUCCESS -> {
                                        val streams = streamsResource.data ?: emptyList()

                                        val filteredStreams = if (activeOnly) {
                                            streams.filter { it.endTime == null }
                                        } else {
                                            streams
                                        }

                                        uiState = uiState.copy(
                                            isLoading = false,
                                            streams = filteredStreams,
                                            activeOnlyFilter = activeOnly
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

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

data class StreamHistoryUiState(
    val isLoading: Boolean = false,
    val streams: List<Stream> = emptyList(),
    val activeOnlyFilter: Boolean = false,
    val errorMessage: String? = null
)