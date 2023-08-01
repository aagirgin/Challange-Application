package com.example.challapp.ui.login.forgotpassowrd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.domain.state.UiState
import com.example.challapp.repository.FirestoreUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {
    private val _forgotPwState = MutableStateFlow<UiState<*>>(UiState.Empty)
    val forgotPwState: StateFlow<UiState<*>> get() = _forgotPwState

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _forgotPwState.value = UiState.Loading

            val isEmailSent = userRepository.sendPasswordResetEmail(email)
            if (isEmailSent) {
                _forgotPwState.value = UiState.Success(true)
            } else {
                _forgotPwState.value = UiState.Error("Reset mail could not be sent.")
            }
        }
    }
}