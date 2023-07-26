package com.example.challapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class LoginState {
    object Loading : LoginState()
    data class Error(val errorMessage: String) : LoginState()
    object Success : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState?>(null)
    val loginState: StateFlow<LoginState?> get() = _loginState

    fun resetLoginState() {
        viewModelScope.launch {
            _loginState.value = null
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            AuthService.signIn(email, password) { user, exception ->
                if (exception != null) {
                    _loginState.value = LoginState.Error(exception.message ?: "Sign-in failed.")
                } else {
                    _loginState.value = LoginState.Success
                }
            }
        }

    }
}