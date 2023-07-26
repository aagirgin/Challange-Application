package com.example.challapp.ui.profile.profilechangepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.repository.FirestoreUserRepository
import com.example.challapp.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileChangePwViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository
): ViewModel() {
    private val _usernameFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    val usernameFlow: Flow<String?>
        get() = _usernameFlow

    init {
        val currentUser = AuthService.getCurrentUser()
        val userId = currentUser?.uid
        viewModelScope.launch {
            fetchUsername(userId!!)
        }
    }
    suspend fun fetchUsername(userId: String) {
        viewModelScope.launch {
            _usernameFlow.value = firestoreUserRepository.getUsername(userId)
        }
    }
}