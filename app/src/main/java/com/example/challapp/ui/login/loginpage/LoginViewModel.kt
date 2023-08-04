package com.example.challapp.ui.login.loginpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class LoginState {
    object Loading : LoginState()
    data class Error(val errorMessage: String) : LoginState()
    object Success : LoginState()
}


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState?>(null)
    val loginState: StateFlow<LoginState?> get() = _loginState

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = userRepository.signIn(email, password)
            if (user != null) {
                if(user.isEmailVerified){
                    _loginState.value = LoginState.Success
                    _currentUser.value = userRepository.getCurrentUser()
                    userRepository.giveInviteKeyIfNull(user.uid)
                }
                else{
                    _loginState.value = LoginState.Error("Please verify your email and try again.")
                    userRepository.signOut()
                }
            } else {
                _loginState.value = LoginState.Error("Invalid credentials. Please try again.")
            }
        }
    }
}