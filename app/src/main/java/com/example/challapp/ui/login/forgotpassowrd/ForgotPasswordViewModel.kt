package com.example.challapp.ui.login.forgotpassowrd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ForgotPwState {
    object empty : ForgotPwState()
    object Loading : ForgotPwState()
    data class Error(val errorMessage: String) : ForgotPwState()
    object Success : ForgotPwState()
}

class ForgotPasswordViewModel:ViewModel() {
    private val _forgotPwState = MutableStateFlow<ForgotPwState?>(ForgotPwState.empty)
    val forgotPwState: StateFlow<ForgotPwState?> get() = _forgotPwState

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _forgotPwState.value = ForgotPwState.Loading

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _forgotPwState.value = ForgotPwState.Success
                    } else {
                        val errorMessage = task.exception?.message ?: "An error occurred"
                        _forgotPwState.value = ForgotPwState.Error(errorMessage)
                    }
                }
        }
    }
}