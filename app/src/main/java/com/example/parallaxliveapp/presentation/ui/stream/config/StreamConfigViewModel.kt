package com.example.parallaxliveapp.presentation.ui.stream.config

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parallaxliveapp.domain.model.StreamConfig
import com.example.parallaxliveapp.domain.usecase.auth.LoginUseCase
import com.example.parallaxliveapp.domain.usecase.stream.CreateStreamUseCase
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StreamConfigViewModel(
    private val loginUseCase: LoginUseCase,
    private val createStreamUseCase: CreateStreamUseCase
) : ViewModel() {

    var uiState by mutableStateOf(StreamConfigUiState())
        private set

    fun updateTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun updateViewerCount(count: Int) {
        uiState = uiState.copy(viewerCount = count)
    }
    fun updateMessageType(type: StreamConfig.MessageType) {
        uiState = uiState.copy(messageType = type)
    }

    fun updatePrivacy(isPrivate: Boolean) {
        uiState = uiState.copy(isPrivate = isPrivate)
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun createStream() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)


            loginUseCase.getCurrentUser().collectLatest { userResource ->
                when (userResource.status) {
                    Resource.Status.SUCCESS -> {
                        val user = userResource.data
                        if (user != null) {

                            val streamConfig = StreamConfig(
                                targetViewerCount = uiState.viewerCount,
                                messageType = uiState.messageType,
                                title = uiState.title.ifEmpty { "Stream by ${user.name}" },
                                isPrivate = uiState.isPrivate
                            )


                            createStreamUseCase.createStream(user.id, streamConfig)
                                .collectLatest { streamResource ->
                                    when (streamResource.status) {
                                        Resource.Status.SUCCESS -> {
                                            val stream = streamResource.data
                                            if (stream != null) {
                                                uiState = uiState.copy(
                                                    isLoading = false,
                                                    streamId = stream.id
                                                )
                                            } else {
                                                uiState = uiState.copy(
                                                    isLoading = false,
                                                    errorMessage = "Error creating stream"
                                                )
                                            }
                                        }
                                        Resource.Status.ERROR -> {
                                            uiState = uiState.copy(
                                                isLoading = false,
                                                errorMessage = streamResource.message
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
}


data class StreamConfigUiState(
    val title: String = "",
    val viewerCount: Int = 50,
    val messageType: StreamConfig.MessageType = StreamConfig.MessageType.POSITIVE,
    val isPrivate: Boolean = false,
    val isLoading: Boolean = false,
    val streamId: String? = null,
    val errorMessage: String? = null
)