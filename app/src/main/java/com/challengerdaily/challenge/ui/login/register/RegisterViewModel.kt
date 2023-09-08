package com.challengerdaily.challenge.ui.login.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengerdaily.challenge.repository.FirestoreUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterState {
    object Empty : RegisterState()
    object Loading : RegisterState()
    data class Error(val errorMessage: String) : RegisterState()
    object Success : RegisterState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
):ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState?>(null)
    val registerState: StateFlow<RegisterState?> get() = _registerState

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            val user = userRepository.signUp(email, password)

            if (user != null) {
                val addUserResult = userRepository.addUserToFirestore(user.uid, email, fullName)
                if (addUserResult) {
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error("Failed to add user to Firestore.")
                }

            } else {
                val errorMessage = userRepository.getFirebaseErrorMessage()
                _registerState.value = RegisterState.Error(errorMessage ?: "Sign-up failed.")
            }
        }
    }

    fun resetState(){
        _registerState.value = RegisterState.Empty
    }

}