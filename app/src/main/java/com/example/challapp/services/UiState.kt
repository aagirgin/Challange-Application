package com.example.challapp.services

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val errorMessage: String) : UiState<Nothing>
    object Loading : UiState<Nothing>
    object Empty : UiState<Nothing>
}