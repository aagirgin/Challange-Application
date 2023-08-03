package com.example.challapp.ui.profile.profilechangepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challapp.repository.FirestoreUserRepository
import com.example.challapp.repository.StorageRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileChangePwViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository,
    private val storageRepository: StorageRepository
): ViewModel() {
    private val _usernameFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    val usernameFlow: Flow<String?>
        get() = _usernameFlow

    val currentUser: MutableStateFlow<FirebaseUser?>
        get() = _currentUser

    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: MutableStateFlow<String?>
        get() = _profileImageUrl
    init {
        _currentUser.value = firestoreUserRepository.getCurrentUser()
        val userId = _currentUser.value?.uid
        viewModelScope.launch {
            fetchUsername(userId!!)
        }
    }

    fun loadProfileImage(email: String) {
        viewModelScope.launch {
            val imageUrl = storageRepository.loadProfileImage(email)
            _profileImageUrl.value = imageUrl
        }
    }
    suspend fun fetchUsername(userId: String) {
        viewModelScope.launch {
            _usernameFlow.value = firestoreUserRepository.getUsername(userId)
        }
    }
}