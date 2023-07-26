package com.example.challapp.ui.login

import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.domain.models.ApplicationUser
import com.example.challapp.services.AuthService
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Loading : RegisterState()
    data class Error(val errorMessage: String) : RegisterState()
    object Success : RegisterState()
}


class RegisterViewModel:ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState?>(null)
    val registerState: StateFlow<RegisterState?> get() = _registerState

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            AuthService.signUp(email, password) { _, exception ->
                if (exception == null) {
                    val db = Firebase.firestore
                    val user = ApplicationUser(username = fullName)
                    AuthService.getCurrentUser()?.let { currentUser ->
                        db.collection("Users").document(currentUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                _registerState.value = RegisterState.Success
                            }
                            .addOnFailureListener { e ->
                                _registerState.value = e.message?.let { RegisterState.Error(it) }
                            }
                    }
                } else {
                    _registerState.value = exception.message?.let { RegisterState.Error(it) }
                }
            }
        }
    }

}