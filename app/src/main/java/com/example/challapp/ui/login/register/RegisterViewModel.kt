package com.example.challapp.ui.login.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.domain.models.ApplicationUser
import com.example.challapp.repository.FirestoreUserRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterState {
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
                val db = Firebase.firestore
                val applicationUser = ApplicationUser(username = fullName)
                db.collection("Users").document(user.uid)
                    .set(applicationUser)
                    .addOnSuccessListener {
                        _registerState.value = RegisterState.Success
                    }
                    .addOnFailureListener { e ->
                        _registerState.value = e.message?.let { RegisterState.Error(it) }
                    }
            } else {
                _registerState.value = RegisterState.Error("Sign-up failed.")
            }
        }
    }

}