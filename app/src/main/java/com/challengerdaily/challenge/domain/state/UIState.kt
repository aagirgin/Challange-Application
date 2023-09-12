package com.challengerdaily.challenge.domain.state

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val error: String) : UiState<Nothing>
    object Loading : UiState<Nothing>
    object Empty : UiState<Nothing>
}