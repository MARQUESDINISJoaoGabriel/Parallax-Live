package com.example.parallaxliveapp.presentation.ui.stream.live

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parallaxliveapp.domain.model.Comment
import com.example.parallaxliveapp.domain.model.Stream
import com.example.parallaxliveapp.domain.usecase.auth.LoginUseCase
import com.example.parallaxliveapp.domain.usecase.stream.GetStreamDetailsUseCase
import com.example.parallaxliveapp.util.result.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Random
import java.util.UUID
import java.util.Date


class LiveStreamViewModel(
    private val loginUseCase: LoginUseCase,
    private val getStreamDetailsUseCase: GetStreamDetailsUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf(LiveStreamUiState())
    val uiState: State<LiveStreamUiState> = _uiState

    private var commentGenerationJob: Job? = null
    private var reactionGenerationJob: Job? = null
    private val random = Random()


    fun loadStream(streamId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                streamId = streamId
            )

            getStreamDetailsUseCase.getStreamDetails(streamId)
                .collectLatest { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            val stream = resource.data
                            if (stream != null) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    stream = stream,
                                    comments = stream.comments
                                )

                                if (stream.endTime == null) {
                                    startCommentGeneration(stream)
                                    startReactionGeneration()
                                }
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = "Stream not found"
                                )
                            }
                        }
                        Resource.Status.ERROR -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
                        Resource.Status.LOADING -> {

                        }
                    }
                }

            getStreamDetailsUseCase.getStreamComments(streamId)
                .collectLatest { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            val comments = resource.data ?: emptyList()
                            _uiState.value = _uiState.value.copy(
                                comments = comments
                            )
                        }
                        Resource.Status.ERROR -> {

                            println("Error loading comments: ${resource.message}")
                        }
                        Resource.Status.LOADING -> {
                        }
                    }
                }
        }
    }


    fun addComment(text: String) {
        val streamId = _uiState.value.streamId ?: return

        viewModelScope.launch {
            loginUseCase.getCurrentUser().collectLatest { userResource ->
                when (userResource.status) {
                    Resource.Status.SUCCESS -> {
                        val user = userResource.data
                        if (user != null) {
                            getStreamDetailsUseCase.addComment(
                                streamId = streamId,
                                userId = user.id,
                                text = text
                            ).collectLatest { commentResource ->
                                when (commentResource.status) {
                                    Resource.Status.SUCCESS -> {

                                    }
                                    Resource.Status.ERROR -> {
                                        _uiState.value = _uiState.value.copy(
                                            errorMessage = commentResource.message
                                        )
                                    }
                                    Resource.Status.LOADING -> {

                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }


    fun endStream() {
        val streamId = _uiState.value.streamId ?: return

        viewModelScope.launch {
            stopCommentGeneration()
            stopReactionGeneration()

            getStreamDetailsUseCase.endStream(streamId)
                .collectLatest { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            _uiState.value = _uiState.value.copy(
                                isStreamEnded = true
                            )
                        }
                        Resource.Status.ERROR -> {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = resource.message
                            )
                        }
                        Resource.Status.LOADING -> {

                        }
                    }
                }
        }
    }


    private fun startCommentGeneration(stream: Stream) {
        stopCommentGeneration()

        commentGenerationJob = viewModelScope.launch {
            while (true) {
                val delayTime = (2000 + random.nextInt(3000)).toLong()
                delay(delayTime)

                val currentComments = _uiState.value.comments.toMutableList()

                val newComment = generateSimulatedComment(
                    stream.id,
                    stream.config.messageType.name
                )
                currentComments.add(newComment)

                _uiState.value = _uiState.value.copy(
                    comments = currentComments
                )
            }
        }
    }


    private fun stopCommentGeneration() {
        commentGenerationJob?.cancel()
        commentGenerationJob = null
    }


    private fun startReactionGeneration() {
        stopReactionGeneration()

        reactionGenerationJob = viewModelScope.launch {
            while (true) {
                val delayTime = (5000 + random.nextInt(5000)).toLong()
                delay(delayTime)

            }
        }
    }


    private fun stopReactionGeneration() {
        reactionGenerationJob?.cancel()
        reactionGenerationJob = null
    }

    private fun generateSimulatedComment(streamId: String, messageType: String): Comment {
        val usernames = listOf(
            "viewer123", "stream_fan", "live_enthusiast", "mobile_user",
            "tech_lover", "android_dev", "kotlin_coder", "app_tester"
        )

        val comments = when (messageType) {
            "POSITIVE" -> listOf(
                "This is amazing!",
                "Great stream, keep it up!",
                "Loving the content!",
                "You're doing great!",
                "This app is so cool!",
                "Impressive work!",
                "Best stream ever!",
                "I'm learning so much!"
            )
            "QUESTIONS" -> listOf(
                "How does this work?",
                "Can you explain that again?",
                "What's your tech stack?",
                "How long did it take to build this?",
                "Will you do more streams?",
                "Can you show that feature again?",
                "Are you using Jetpack Compose?",
                "How did you learn Android development?"
            )
            else -> listOf(
                "This is great! 👏",
                "How did you build this? 🤔",
                "Loving the UI design! ❤️",
                "Can you explain the camera setup?",
                "Awesome stream! 🔥",
                "Is this in the Play Store?",
                "First time here, loving it! 👍",
                "Will you show the code? 💻"
            )
        }

        val username = usernames[random.nextInt(usernames.size)]
        val text = comments[random.nextInt(comments.size)]

        return Comment(
            id = UUID.randomUUID().toString(),
            streamId = streamId,
            userId = null,
            username = username,
            avatarUrl = null,
            text = text,
            timestamp = Date(),
            reactions = emptyList(),
            isSystemGenerated = true
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopCommentGeneration()
        stopReactionGeneration()
    }
}

data class LiveStreamUiState(
    val isLoading: Boolean = false,
    val streamId: String? = null,
    val stream: Stream? = null,
    val comments: List<Comment> = emptyList(),
    val isStreamEnded: Boolean = false,
    val errorMessage: String? = null
)